#!/bin/bash

ibmcloud login -a cloud.ibm.com -r us-south -g prow -q --apikey=${IBMCLOUD_API_KEY}
ibmcloud update -f
ibmcloud plugin update --all
ibmcloud ks cluster config --cluster "${CLUSTER_ID}"
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

cd ${WORKSPACE}/scripts

 #Jenkins Backup
echo "Copying backup script to jenkins pod"
jenkins_pod_id=$(kubectl get pods -n jenkins -l app.kubernetes.io/component=jenkins-controller -o custom-columns=PodName:.metadata.name | grep jenkins-)
kubectl exec -n jenkins ${jenkins_pod_id} -- bash -c "mkdir /tmp/jenkins_backup"
kubectl exec -n jenkins ${jenkins_pod_id} -- bash -c "tar --exclude=/var/jenkins_home/workspace -zcf /tmp/jenkins_backup/jenkins_backup.tar.gz /var/jenkins_home"
kubectl cp  --retries=5 jenkins/${jenkins_pod_id}:/tmp/jenkins_backup ./
ls
kubectl exec -n jenkins ${jenkins_pod_id} -- bash -c "rm -rf /tmp/jenkins_backup"
echo "Copy to ibmcloud ${JENKINS_BUCKET}"
ibmcloud login -a cloud.ibm.com -r us-south -q --apikey=${IBMCLOUD_API_KEY}
ibmcloud cos config crn --crn "${CRN}"
ibmcloud cos upload --bucket "${JENKINS_BUCKET}" --key jenkins_backup_"$(date +%Y%m%d%H%M)".tar.gz  --file ./jenkins_backup.tar.gz
if [ $? -eq 0 ]; then
  echo "Keeping only last 5 backups Removing rest from jenkins cloud storage bucket"
  while IFS= read -r object_name; do
    if [ -n "$object_name" ]; then
      ibmcloud cos object-delete --bucket "${JENKINS_BUCKET}" --key $object_name --force
    fi
  done < <(ibmcloud cos objects --bucket "${JENKINS_BUCKET}" |  cut -d ' ' -f 1  | tail -n +5 |head -n -6)
fi

#Influx DB backup
echo "Copying backup script to influx pod"
ibmcloud login -a cloud.ibm.com -r us-south -g prow -q --apikey=${IBMCLOUD_API_KEY}
ibmcloud ks cluster config --cluster "${CLUSTER_ID}"
influx_pod_id=$(kubectl get pods -n grafana-dashboard -l app=influxdb -o custom-columns=PodName:.metadata.name | grep influxdb-)
kubectl exec -n grafana-dashboard ${influx_pod_id} -- bash -c "mkdir /tmp/influx_backup"
kubectl exec -n grafana-dashboard ${influx_pod_id} -- bash -c "tar -zcf /tmp/influx_backup/influxdb_backup.tar.gz /var/lib/influxdb"
kubectl cp  --retries=5 grafana-dashboard/${influx_pod_id}:/tmp/influx_backup ./
ls
kubectl exec -n grafana-dashboard ${influx_pod_id} -- bash -c "rm -rf /tmp/influx_backup"
ibmcloud login -a cloud.ibm.com -r us-south -q --apikey=${IBMCLOUD_API_KEY}
ibmcloud cos config crn --crn "${CRN}"
ibmcloud cos upload --bucket "$INFLUXDB_BUCKET" --key influxdb_backup_"$(date +%Y%m%d%H%M)".tar.gz  --file ./influxdb_backup.tar.gz
if [ $? -eq 0 ]; then
  echo "Keeping only last 5 backups Removing rest from influxdb cloud storage bucket"
  while IFS= read -r object_name; do
    if [ -n "$object_name" ]; then
      ibmcloud cos object-delete --bucket "${INFLUXDB_BUCKET}" --key $object_name --force
    fi
  done < <(ibmcloud cos objects --bucket "${INFLUXDB_BUCKET}" |  cut -d ' ' -f 1  | tail -n +5 |head -n -6)
fi
