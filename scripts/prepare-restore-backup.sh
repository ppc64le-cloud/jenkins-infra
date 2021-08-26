#!/bin/bash

 #Jenkins Backup
echo "Copying backup script to jenkins pod"
jenkins_pod_id=$(kubectl get pods -n jenkins -l app.kubernetes.io/component=jenkins-controller -o custom-columns=PodName:.metadata.name | grep jenkins-)
cd ${WORKSPACE}/scripts
echo "${IBMCLOUD_API_KEY}" >> key
kubectl -n jenkins cp ./restore-backup.sh jenkins/${jenkins_pod_id}:./;
kubectl -n jenkins cp ./key jenkins/${jenkins_pod_id}:./;
kubectl exec -n jenkins $jenkins_pod_id -- bash -c "chmod +x restore-backup.sh; \
                                                    ./restore-backup.sh -j ${JENKINS_BUCKET} -c ${CRN}" -b ${JENKINS_BACKUP_FILE}

#Influx DB backup
echo "Copying backup script to influx pod"
influx_pod_id=$(kubectl get pods -n grafana-dashboard -l app=influxdb -o custom-columns=PodName:.metadata.name | grep influxdb-)
kubectl -n grafana-dashboard cp ./restore-backup.sh grafana-dashboard/${influx_pod_id}:./;
kubectl -n grafana-dashboard cp ./key grafana-dashboard/${influx_pod_id}:./;
kubectl exec -n grafana-dashboard $influx_pod_id -- bash -c "chmod +x restore-backup.sh; \
                                                    ./restore-backup.sh -i ${INFLUXDB_BUCKET} -c ${CRN}" -b ${INFLUX_BACKUP_FILE}
