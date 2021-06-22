#!/bin/bash

 #Jenkins Backup
echo "Preparing backup Jenkins"
jenkins_pod_id=$(kubectl get pods -n jenkins -l app.kubernetes.io/component=jenkins-controller -o custom-columns=PodName:.metadata.name | grep jenkins-)
kubectl exec -n jenkins $jenkins_pod_id -- bash -c "cd /var; \
                                                    rm -rf jenkins_backup; \
                                                    mkdir -p jenkins_backup; \
                                                    cp -r jenkins_home jenkins_backup/jenkins_home; \
                                                    tar -zcvf jenkins_backup/jenkins_backup.tar.gz jenkins_backup/jenkins_home"

echo "Copying the Jenkins backup file to local system"
kubectl -n jenkins cp jenkins/${jenkins_pod_id}:/var/jenkins_backup/jenkins_backup.tar.gz jenkins_backup.tar.gz
if [ $? -ne 0 ] ; then
  kubectl -n jenkins cp jenkins/${jenkins_pod_id}:/var/jenkins_backup/jenkins_backup.tar.gz jenkins_backup.tar.gz
fi
echo "Upload Jenkins backup to ibm cloud"
ibmcloud cos upload --bucket "${JENKINS_BUCKET}" --key jenkins_backup_$(date +%Y%m%d%H%M).tar.gz  --file jenkins_backup.tar.gz
kubectl exec -n jenkins $jenkins_pod_id -- bash -c 'rm -rf /var/jenkins_backup'

#Influx DB backup
influx_pod_id=$(kubectl get pods -n grafana-dashboard -l app=influxdb -o custom-columns=PodName:.metadata.name | grep influxdb-)
kubectl exec -n grafana-dashboard $influx_pod_id -- bash -c "cd /var/lib; \
                                                             rm -rf influxdb_backup; \
                                                             mkdir -p influxdb_backup; \
                                                             cp -r influxdb influxdb_backup/influxdb; \
                                                             tar -zcvf influxdb_backup/influxdb_backup.tar.gz influxdb_backup/influxdb"
echo "Copying the InfluxDb backup file to local system"
kubectl -n grafana-dashboard cp grafana-dashboard/${influx_pod_id}:/var/lib/influxdb_backup/influxdb_backup.tar.gz influxdb_backup.tar.gz
if [ $? -ne 0 ] ; then
  kubectl -n grafana-dashboard cp grafana-dashboard/${influx_pod_id}:/var/lib/influxdb_backup/influxdb_backup.tar.gz influxdb_backup.tar.gz
fi
echo "Upload to ibm cloud InfluxDb"
ibmcloud cos upload --bucket "${INFLUXDB_BUCKET}" --key influxdb_backup_$(date +%Y%m%d%H%M).tar.gz  --file influxdb_backup.tar.gz
kubectl exec -n grafana-dashboard $influx_pod_id -- bash -c 'rm -rf /var/lib/influxdb_backup'

echo "Keeping only last 5 backups Removing rest from jenkins cloud storage bucket"
while IFS= read -r object_name; do
  if [ -n "$object_name" ]; then
    ibmcloud cos object-delete --bucket "${JENKINS_BUCKET}" --key $object_name --force
  fi
done < <(ibmcloud cos objects --bucket "${JENKINS_BUCKET}" |  cut -d ' ' -f 1  | tail -n +5 |head -n -6)

echo "Keeping only last 5 backups Removing rest from influxdb cloud storage bucket"
while IFS= read -r object_name; do
  if [ -n "$object_name" ]; then
    ibmcloud cos object-delete --bucket "${INFLUXDB_BUCKET}" --key $object_name --force
  fi
done < <(ibmcloud cos objects --bucket "${INFLUXDB_BUCKET}" |  cut -d ' ' -f 1  | tail -n +5 |head -n -6)
