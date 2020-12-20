#!/usr/bin/env bash

function help_function() {
  cat <<-EOF
Automation for creating and uploading images on PowerVS
Usage:
  powervs_create_and_upload_image.sh -u <url> -n <rhel_user_name> -p <rhel_password> -a <api-key>
EOF
  exit 0
}

while getopts u:n:p:a:s: option; do
  case "${option}" in

  u) URL=${OPTARG} ;;
  n) RH_USERNAME=${OPTARG} ;;
  p) RH_PASSWORD=${OPTARG} ;;
  a) APIKEY=${OPTARG} ;;
  s) IMAGE_SIZE=${OPTARG} ;;
  *) help_function ;;
  esac
done

function install_pvsadm_tool() {
  curl -sL https://raw.githubusercontent.com/ppc64le-cloud/pvsadm/master/get.sh | FORCE=1 bash
}

function standardize_object_name() {
  log "Formatting Object name $1"
  local object_original_url=$1
  local object_original_name=${object_original_url##*/}
  local object_extension=""
  local object_temp_name=""
  if echo $object_original_name | grep -q -i centos; then
    echo $object_original_name | grep 'ova.gz' >/dev/null
    [ $? -ne 0 ] && error "Unsupported file format"
    object_extension="ova.gz"
    object_temp_name=${object_original_name%.*.*}
    DISTRO="centos"
  elif echo $object_original_name | grep -q -i rhcos; then
    echo $object_original_name | grep 'qcow2.gz' >/dev/null
    [ $? -ne 0 ] && error "Unsupported file format"
    object_extension="ova.gz"
    object_temp_name=${object_original_name%.*.*}
    DISTRO="coreos"
  elif echo $object_original_name | grep -q -i rhel; then
    echo $object_original_name | grep 'qcow2' >/dev/null
    [ $? -ne 0 ] && error "Unsupported file format"
    object_extension="ova.gz"
    object_temp_name=${object_original_name%.*}
    DISTRO="rhel"
  fi
  IMAGE_NAME=$(echo $object_temp_name | sed -e 's/\.//g' -e 's/ppc64le//g' -e 's/openstack//g' -e 's/kvm//g' -e 's/_/-/g' -e 's/---*//g' -e 's/\([0-9]\+\)-GenericCloud-//g' | tr '[:upper:]' '[:lower:]' | grep -o -E '^[a-z]*-[0-9]{2}')-$(date +%m%d%Y)
  OBJECT_NAME=$IMAGE_NAME.${object_extension}
}

install_pvsadm_tool

#Standerdise object name
standardize_object_name $URL
OBJECT_NAME=$OBJECT_NAME
IMAGE_NAME=$IMAGE_NAME

#Create image

echo "Creating image $IMAGE_NAME "
pvsadm image qcow2ova --image-name $IMAGE_NAME --image-url $URL --image-dist $DISTRO --image-size $IMAGE_SIZE --rhn-user $RH_USERNAME --rhn-password $RH_PASSWORD 2>&1 | tee output.log
if [ $? -eq 0 ]; then
  echo "$OBJECT_NAME is successfully created"

  #Upload image
  export IBMCLOUD_API_KEY=$APIKEY
  echo "Uploading image in ocp4-images-bucket"
  pvsadm image upload -b ocp4-images-bucket --file $OBJECT_NAME --cos-instance-name ocp4-on-power
  if [ $? -eq 0 ]; then
    password=$(grep "password:" <output.log)
    echo "$IMAGE_NAME $password" >>image_list.txt
    echo "------------$IMAGE_NAME image instances uploaded--------------" >>instance_list_output.txt
    while IFS= read -r instanceName; do
      if [ -n "$instanceName" ]; then
        echo "Importing image to $instanceName"
        pvsadm image import --pvs-instance-name $instanceName -b ocp4-images-bucket --object $OBJECT_NAME --pvs-image-name $IMAGE_NAME --bucket-region us-south
        if [ $? -eq 0 ]; then
          echo "$IMAGE_NAME successfully imported to $instanceName"
          echo "$instanceName   Success" >>instance_list_output.txt
        else
          echo "$instanceName   Failure" >>instance_list_output.txt
        fi
      fi
    done <powervs_instance_list.txt
  else
    echo "$OBJECT_NAME is not successfully uploaded"
  fi
else
  echo "$OBJECT_NAME is not created"
fi
rm -f ~/$OBJECT_NAME ~/output.log
