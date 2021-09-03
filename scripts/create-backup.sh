#!/bin/bash
  # Parse commands and arguments
while getopts "j:c:i:" opt
do
   case "$opt" in
      j ) JENKINS_BUCKET="$OPTARG" ;;
      i ) INFLUXDB_BUCKET="$OPTARG" ;;
      c ) CRN="$OPTARG" ;;
      ? ) echo "Wrong Args" ;;
   esac
done
#check if ibmcloud cli exist
ibmcloud -v
if [ $? -ne 0 ]; then
   apt update; apt-get install -y wget curl; \
   wget https://download.clis.cloud.ibm.com/ibm-cloud-cli/1.6.0/IBM_Cloud_CLI_1.6.0_amd64.tar.gz; \
   curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"; \
   install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl; \
   tar -xvzf "./IBM_Cloud_CLI_1.6.0_amd64.tar.gz"; \
   ./Bluemix_CLI/install; \
   ibmcloud update -f; \
   ibmcloud config --check-version false
   ibmcloud plugin install cloud-object-storage -f; \
   ibmcloud plugin install container-service -f;
fi
if [ -n "$JENKINS_BUCKET" ]; then
  echo "Zip jenkins folder"
  pwd
  ls
  cd /var
  tar -zcvf jenkins_backup.tar.gz ./jenkins_home
  echo "Copy to ibmcloud $JENKINS_BUCKET"
  #Login
  API_KEY=`cat /key`
  ibmcloud login -a cloud.ibm.com -r us-south -q --apikey=$API_KEY
  ibmcloud cos config crn --crn "$CRN"
  ibmcloud cos upload --bucket "$JENKINS_BUCKET" --key jenkins_backup_"$(date +%Y%m%d%H%M)".tar.gz  --file ./jenkins_backup.tar.gz
  if [ $? -eq 0 ]; then
    echo "Keeping only last 5 backups Removing rest from jenkins cloud storage bucket"
      while IFS= read -r object_name; do
        if [ -n "$object_name" ]; then
          ibmcloud cos object-delete --bucket "${JENKINS_BUCKET}" --key $object_name --force
        fi
      done < <(ibmcloud cos objects --bucket "${JENKINS_BUCKET}" |  cut -d ' ' -f 1  | tail -n +5 |head -n -6)
  fi
  rm -rf jenkins_backup.tar.gz /create-backup.sh /key
else
   echo "Zip influxdb folder"
   cd /var/lib
   tar -zcvf influxdb_backup.tar.gz ./influxdb
   API_KEY=`cat /key`
   ibmcloud login -a cloud.ibm.com -r us-south -q --apikey=$API_KEY
   ibmcloud cos config crn --crn "$CRN"
   ibmcloud cos upload --bucket "$INFLUXDB_BUCKET" --key influxdb_backup_"$(date +%Y%m%d%H%M)".tar.gz  --file ./influxdb_backup.tar.gz
   if [ $? -eq 0 ]; then
    echo "Keeping only last 5 backups Removing rest from influxdb cloud storage bucket"
    while IFS= read -r object_name; do
      if [ -n "$object_name" ]; then
        ibmcloud cos object-delete --bucket "${INFLUXDB_BUCKET}" --key $object_name --force
      fi
    done < <(ibmcloud cos objects --bucket "${INFLUXDB_BUCKET}" |  cut -d ' ' -f 1  | tail -n +5 |head -n -6)
   fi
   rm -rf influxdb_backup.tar.gz /create-backup.sh /key
fi
