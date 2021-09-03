#!/bin/bash
  # Parse commands and arguments
while getopts "j:c:i:b:" opt
do
   case "$opt" in
      j ) JENKINS_BUCKET="$OPTARG" ;;
      i ) INFLUXDB_BUCKET="$OPTARG" ;;
      c ) CRN="$OPTARG" ;;
      b ) BACKUP_FILE="$OPTARG" ;;
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
  echo "Download from ibmcloud $JENKINS_BUCKET"
  #Login
  API_KEY=`cat key`
  ibmcloud login -a cloud.ibm.com -r us-south -q --apikey=$API_KEY
  ibmcloud cos config crn --crn "$CRN"
  ibmcloud cos download --bucket "${JENKINS_BUCKET}" --key "${BACKUP_FILE}" "./${BACKUP_FILE}"
  if [ $? -eq 0 ]; then
     cd /var
     mv ./jenkins_home ./jenkins_home_old
     tar -xvf "/${BACKUP_FILE}"
     rm -rf  ./jenkins_home_old /${BACKUP_FILE} /create-backup.sh /key
  else
    echo "Error Jenkins Restore Unsuccessful!!!"
  fi
else
   echo "Download from ibmcloud $JENKINS_BUCKET"
   API_KEY=`cat key`
   ibmcloud login -a cloud.ibm.com -r us-south -q --apikey=$API_KEY
   ibmcloud cos config crn --crn "$CRN"
   ibmcloud cos download --bucket "${INFLUXDB_BUCKET}" --key ${BACKUP_FILE} "./${BACKUP_FILE}"
   if [ $? -eq 0 ]; then
    cd /var/lib
    mv ./influxdb ./influxdb_old
    tar -xvf "/${BACKUP_FILE}"
    rm -rf  ./influxdb_old /${BACKUP_FILE} /create-backup.sh /key
  else
    echo "Error Influxdb Restore Unsuccessful!!!"
   fi
   rm -rf influxdb_backup.tar.gz
fi
