#!/bin/bash
if [ -n "${JENKINS_BACKUP_FILE}" ] ; then
  jenkins_pod_id=$(kubectl get pods -n jenkins -l app.kubernetes.io/component=jenkins-controller -o custom-columns=PodName:.metadata.name | grep jenkins-)
  ibmcloud cos download --bucket "${JENKINS_BUCKET}" --key "${JENKINS_BACKUP_FILE}"
  if [ ! -f "${JENKINS_BACKUP_FILE}" ]; then
    echo "Jenkins file doesn't exist"
    exit 1
  fi
  kubectl cp ./${JENKINS_BACKUP_FILE} jenkins/$jenkins_pod_id:/var
  kubectl exec -n jenkins $jenkins_pod_id -- bash -c "cd /var; \
                                                    mv jenkins_home jenkins_home_old; \
                                                    mkdir -p jenkins_home; \
                                                    tar -zxf ${JENKINS_BACKUP_FILE} --directory ./jenkins_home; \
                                                    rm -rf ${JENKINS_BACKUP_FILE} jenkins_home_old"
fi

if [ -n "${INFLUX_BACKUP_FILE}" ] ; then
  influx_pod_id=$(kubectl get pods -n grafana-dashboard -l app=influxdb -o custom-columns=PodName:.metadata.name | grep influxdb-)
  ibmcloud cos download --bucket "${INFLUXDB_BUCKET}" --key ${INFLUX_BACKUP_FILE}
  if [ ! -f "${INFLUX_BACKUP_FILE}" ]; then
    echo "influx db file doesn't exist"
    exit 1
  fi
  kubectl cp ./${INFLUX_BACKUP_FILE} grafana-dashboard/$influx_pod_id:/var/lib
  kubectl exec -n jenkins $influx_pod_id -- bash -c "cd /var/lib; \
                                                    mv influxdb influxdb_old; \
                                                    mkdir -p influxdb; \
                                                    tar -zxf ${INFLUX_BACKUP_FILE} --directory ./influxdb; \
                                                    rm -rf ${INFLUX_BACKUP_FILE} influxdb_old"
fi
