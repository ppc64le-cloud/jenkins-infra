#!/usr/bin/env bash
: '
Copyright (C) 2021 IBM Corporation
Licensed under the Apache License, Version 2.0 (the “License”);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an “AS IS” BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
'
#-------------------------------------------------------------------------
set -e
source <(curl -L https://raw.githubusercontent.com/ocp-power-automation/openshift-install-power/fb8dd54019982f2b645084ae3886d73f1ed4ccce/openshift-install-powervs 2> /dev/null |  grep -v 'main "$@"')
source <(curl -L https://raw.githubusercontent.com/ppc64le-cloud/pvsadm/master/samples/convert-upload-images-powervs/convert-upload-images-powervs 2> /dev/null | grep -v 'main "$@"')

function help {
  cat <<-EOF
Automation for creating RHCOS/RHEL images to PowerVS Services. This is a wrapper for pvsadm tool.
Usage:
  ./convert-upload-images-powervs [ --rhel-url <url> | --rhcos-url <url> | --centos-url <url> ]  --region <bucket region> --cos-bucket <bucket name> --cos-instance-name <cos instance name>
Args:
      --service-name string         A list of PowerVS service instances with comma-separated(Mandatory)
      --region string               Object store bucket region(Mandatory)
      --cos-bucket string           Object store bucket name(Mandatory)
      --cos-instance-name string    COS instance name(Mandatory)
      --rhel-url url                url pointing to the RHEL qcow2 image(optional)
      --rhcos-url url               url pointing to the RHCOS qcow2 image(optional)
      --centos-url url              url pointing to the CentOS qcow2 image(optional)
      --pvsadm_version              Pvsadm version(optional)
      --help                        help for upload
EOF
  exit 0
}

#Final Output Logs
OUTPUT_LOG_FILE='output.txt'

#-------------------------------------------------------------------------
# Import images from the given COS bucket
#-------------------------------------------------------------------------
function import_image_remote {
  # Importing the the rhel/rhcos/centos ova images
  log "Importing $3 to $1"
  local service_name=$1
  local cos_bucket_name=$2
  local object_name=$3
  local image_name=$4
  local cos_bucket_region=$5
  SECONDS=0
  local message="Importing $3 image to $1 completed in"
  for i in $(seq 1 "$NO_OF_RETRY"); do
    echo "Attempt: $i/$NO_OF_RETRY"
    LOG_FILE="logs/import_image_remote_${image_name}_${service_name}_${i}_${LOGFILE}.log"
    ssh -n -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval 60' -o 'ServerAliveCountMax 10' -i $VAR_PRIVATE_KEY_FILE root@${BASTION_PUBLIC_IP}  "cd \"$REMOTE_TEMP_DIR\"; pvsadm image import --pvs-instance-name \"$service_name\" --bucket \"$cos_bucket_name\" --bucket-region \"$cos_bucket_region\" --object \"$object_name\" --pvs-image-name \"$image_name\" --api-key \"$IBMCLOUD_API_KEY\" 2>&1" > $LOG_FILE || true
    if grep "${image_name} already exists" $LOG_FILE >/dev/null; then
      warn "Image ${image_name} already exists in ${service_name}. Skipping"
      time_taken "$message"
      return 0
    elif grep "region not found for the zone" $LOG_FILE >/dev/null; then
         warn "region not found for the zone. Check the $LOG_FILE  for more details. Skipping"
         return 0
    elif grep "Error:" $LOG_FILE >/dev/null; then
         failure "Error has occurred while importing image $3 to $1 Check the $LOG_FILE  for more details."
         return 1
    else
        success "Importing $3 to $1 completed"
        time_taken "$message"
        return 0
    fi
  done
}

#-------------------------------------------------------------------------
# Setting up the environment variables for images
#-------------------------------------------------------------------------
function setup_image_env_variables {
  if [[ "${RHEL_IMAGE_NAME}" != "" ]]; then
    RHEL_OBJECT_NAME=$RHEL_IMAGE_NAME.ova.gz
  else
    standardize_object_name $RHEL_URL
    RHEL_OBJECT_NAME=$OBJECT_NAME
    RHEL_IMAGE_NAME=$IMAGE_NAME
  fi
  if [[ "${RHCOS_IMAGE_NAME}" != "" ]]; then
    RHCOS_OBJECT_NAME=$RHCOS_IMAGE_NAME.ova.gz
  else
    standardize_object_name $RHCOS_URL
    RHCOS_OBJECT_NAME=$OBJECT_NAME
    RHCOS_IMAGE_NAME=$IMAGE_NAME
  fi
  if [[ "${CENTOS_IMAGE_NAME}" != "" ]]; then
    CENTOS_OBJECT_NAME=CENTOS_IMAGE_NAME.ova.gz
  else
    standardize_object_name $CENTOS_URL
    CENTOS_OBJECT_NAME=$OBJECT_NAME
    CENTOS_IMAGE_NAME=$IMAGE_NAME
  fi
}

function main {
  mkdir ./logs

  # Only use sudo if not running as root
  [ "$(id -u)" -ne 0 ] && SUDO=sudo || SUDO=""
  platform_checks

  # Parse commands and arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
    "--rhel-url")
      shift
      RHEL_URL="$1"
      ;;
    "--rhcos-url")
      shift
      RHCOS_URL="$1"
      ;;
    "--centos-url")
      shift
      CENTOS_URL="$1"
      ;;
    "--cos-bucket")
      shift
      COS_BUCKET_NAME="$1"
      ;;
    "--region")
      shift
      COS_BUCKET_REGION="$1"
      ;;
    "--pvsadm_version")
      shift
      PVSADM_VERSION="$1"
      ;;
    "--cos-instance-name")
      shift
      COS_INSTANCE_NAME="$1"
      ;;
    "--help")
      help
      ;;
    esac
    shift
  done

  if [ -z "${COS_BUCKET_NAME}" ] || [ -z "${COS_BUCKET_REGION}" ] || [ -z "${PVSADM_VERSION}" ]  || [ -z "${COS_INSTANCE_NAME}" ]; then
    log "Some or all of the parameters are empty"
    help
  fi

  if [ -z "${RHCOS_URL}" ] && [ -z "${RHEL_URL}" ] && [ -z "${CENTOS_URL}" ]; then
    error "No RHEL/RHCOS/CENTOS url provided"
  fi
  SERVICE_NAME=$(head -1 powervs_instance_list.txt)
  RHEL_SUBS_PASSWORD="${RHEL_SUBSCRIPTION_PASSWORD}"
  VAR_RHEL_SUBS_USER="${RHEL_SUBSCRIPTION_USERNAME}"
  setup_image_env_variables
  setup
  variables

  # Downloading rhel locally, else the url may expire by the time the VM comes up
  if ! [ -z "${RHEL_URL}" ];then
    SECONDS=0
    MSG="Downloading rhel qcow2 image completed in"
    download_image "rhel"
    time_taken "$MSG"
  fi
  create_vm

  if !  prepare_remote; then  destroy_vm && exit 1 ; fi
  if !  setup_pvsadm_remote; then  destroy_vm && exit 1 ; fi

  if ! [ -z "${RHEL_URL}" ];then
    if ! copy_image_to_remote; then  destroy_vm && exit 1 ; fi
    if ! convert_image_remote "rhel" "${RHEL_IMAGE_NAME}" "${RHEL_URL}"; then  destroy_vm && exit 1 ; fi
    RHEL_IMAGE_PATH_REMOTE=${IMAGE_PATH}
    if ! upload_image_remote "${COS_BUCKET_NAME}" "${RHEL_IMAGE_PATH_REMOTE}" "${COS_BUCKET_REGION}"; then  destroy_vm && exit 1 ; fi
    echo "---------------- $RHEL_IMAGE_NAME $ROOT_PASSWORD ----------------" >> $OUTPUT_LOG_FILE
    echo "---------------- Instances Uploaded ----------------" >> $OUTPUT_LOG_FILE
    while IFS= read -r SERV_NAME; do
      if ! [ -z "$SERV_NAME" ];then
        if ! import_image_remote "${SERV_NAME}" "${COS_BUCKET_NAME}" "${RHEL_OBJECT_NAME}" "${RHEL_IMAGE_NAME}" "${COS_BUCKET_REGION}"; then
          warn "Unable to import ${RHEL_OBJECT_NAME} to ${SERV_NAME}"
          echo "${SERV_NAME} Failure " >> $OUTPUT_LOG_FILE
        else
          echo "${SERV_NAME} Success " >> $OUTPUT_LOG_FILE
        fi
      fi
    done <powervs_instance_list.txt
  fi
  if ! [ -z "${RHCOS_URL}" ];then
    if ! convert_image_remote "coreos" "${RHCOS_IMAGE_NAME}" "${RHCOS_URL}" ; then  destroy_vm && exit 1 ; fi
    RHCOS_IMAGE_PATH_REMOTE=${IMAGE_PATH}
    if !  upload_image_remote "${COS_BUCKET_NAME}" "${RHCOS_IMAGE_PATH_REMOTE}" "${COS_BUCKET_REGION}" ; then  destroy_vm && exit 1 ; fi
    echo "----------------$RHCOS_IMAGE_NAME ----------------" >> $OUTPUT_LOG_FILE
    echo "---------------- Instances Uploaded ----------------" >> $OUTPUT_LOG_FILE
    while IFS= read -r SERV_NAME; do
      if ! [ -z "$SERV_NAME" ];then
        if ! import_image_remote "${SERV_NAME}" "${COS_BUCKET_NAME}" "${RHCOS_OBJECT_NAME}" "${RHCOS_IMAGE_NAME}" "${COS_BUCKET_REGION}" ; then
          warn "Unable to import ${RHCOS_OBJECT_NAME} to ${SERV_NAME}"
          echo "${SERV_NAME} Failure " >> $OUTPUT_LOG_FILE
        else
          echo "${SERV_NAME} Success " >> $OUTPUT_LOG_FILE
        fi
      fi
    done <powervs_instance_list.txt
  fi
    if ! [ -z "${CENTOS_URL}" ];then
    if ! convert_image_remote "centos" "${CENTOS_IMAGE_NAME}" "${CENTOS_URL}" ; then  destroy_vm && exit 1 ; fi
    CENTOS_IMAGE_PATH_REMOTE=${IMAGE_PATH}
    if !  upload_image_remote "${COS_BUCKET_NAME}" "${CENTOS_IMAGE_PATH_REMOTE}" "${COS_BUCKET_REGION}" ; then  destroy_vm && exit 1 ; fi
    echo "---------------- $CENTOS_IMAGE_NAME $ROOT_PASSWORD ----------------" >> $OUTPUT_LOG_FILE
    echo "---------------- Instances Uploaded ----------------" >> $OUTPUT_LOG_FILE
    while IFS= read -r SERV_NAME; do
      if ! [ -z "$SERV_NAME" ];then
        if ! import_image_remote "${SERV_NAME}" "${COS_BUCKET_NAME}" "${CENTOS_OBJECT_NAME}" "${CENTOS_IMAGE_NAME}" "${COS_BUCKET_REGION}" ; then
          warn "Unable to import ${CENTOS_OBJECT_NAME} to ${SERV_NAME}"
          echo "${SERV_NAME} Failure " >> $OUTPUT_LOG_FILE
        else
          echo "${SERV_NAME} Success " >> $OUTPUT_LOG_FILE
        fi
      fi
    done <powervs_instance_list.txt
  fi
  tar -czvf logs.tar.gz ./logs
  destroy_vm # Destroying the VM
}

main "$@"