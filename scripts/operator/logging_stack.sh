#!/bin/bash

echo 'Creating var.yaml'
rm -rf ~/.ansible
ansible all -m setup -a 'gather_subset=!all'
cd ${WORKSPACE}/ocp4-playbooks-extras 

cp examples/inventory ./clo-inventory
cat clo-inventory
sed -i "s|localhost|${BASTION_IP}|g" clo-inventory
sed -i 's/ansible_connection=local/ansible_connection=ssh/g' clo-inventory
sed -i "s|ssh|ssh ansible_ssh_private_key_file=${WORKSPACE}/deploy/id_rsa|g" clo-inventory
fluentd_ip=$(echo "${ElasticsearchURL}" | sed -E 's#^[a-zA-Z]+://([^:]+):[0-9]+$#\1#')
syslog_ip=$(echo "${SyslogURL}" | sed -E 's#^[a-zA-Z]+://([^:]+):[0-9]+$#\1#')
kafka_ip=$(echo "${KafkaURL}" | sed -E 's#^[a-zA-Z]+://([^:]+):[0-9]+$#\1#')
echo "#######Creating inventory file with external server entries#######"
cat >> clo-inventory <<EOF

[external_vms]
fluentd ansible_host=${fluentd_ip} ansible_python_interpreter=/usr/bin/python3 ansible_connection=ssh ansible_user=root
syslog ansible_host=${syslog_ip} ansible_python_interpreter=/usr/bin/python3 ansible_connection=ssh ansible_user=root
kafka ansible_host=${kafka_ip} ansible_python_interpreter=/usr/bin/python3 ansible_connection=ssh ansible_user=root
EOF

cat clo-inventory

cp examples/ocp_cluster_logging_vars.yml logging_vars.yaml
sed -i \
-e "s|ocp_cluster_logging:.*$|ocp_cluster_logging: true|g" \
-e "s|cluster_log_forwarder:.*$|cluster_log_forwarder: true|g" \
-e "s|log_enable_global_secret:.*$|log_enable_global_secret: false|g" \
-e "s|clusterlogging_clf_cs:.*$|clusterlogging_clf_cs: \"${CLO_CATALOGSOURCE_IMAGE}\"|" \
-e "s|loki_clf_cs:.*$|loki_clf_cs: \"${LOKI_CATALOGSOURCE_IMAGE}\"|" \
-e "s|cluster_logging_channel:.*$|cluster_logging_channel: \"${CLUSTER_LOGGING_CHANNEL}\"|" \
-e "s|loki_channel:.*$|loki_channel: \"${LOKI_CHANNEL}\"|" \
-e "s|elasticsearch_url:.*$|elasticsearch_url: \"${ELASTICSEARCH_URL}\"|" \
-e "s|kafka_url:.*$|kafka_url: \"${KAFKA_URL}\"|" \
-e "s|loki_url:.*$|loki_url: \"${LOKI_URL}\"|" \
-e "s|syslog_url:.*$|syslog_url: \"${SYSLOG_URL}\"|" \
-e "s|kafka_path:.*$|kafka_path: /usr/local/kafka/bin|g" \
-e "s|aws_region:.*$|aws_region: \"ap-south-1\"|" \
-e "s|cloudwatch_secret:.*$|cloudwatch_secret: \"cw-secret\"|g" \
-e "s|kibana_ldap_validation:.*$|kibana_ldap_validation: false|g" \
-e "s|log_label:.*$|log_label: \"${LOG_LABEL}\"|" \
-e "s|log_collector_type:.*$|log_collector_type: vector|g" \
-e "s|log_dir_path:.*$|log_dir_path: /tmp/logging/files/clf_logs|g" \
-e "s|enable_logging_uiplugin:.*$|enable_logging_uiplugin: false|g" \
-e "s|clf_clean_up:.*$|clf_clean_up: false|g" \
-e "s|manifest_dir_path:.*$|manifest_dir_path: /tmp/logging/files|g" \
logging_vars.yaml
cat logging_vars.yaml

#the below commented code needs to be included in below ssh block
# oc extract secret/pull-secret -n openshift-config --confirm
# jq --argjson secret "$ART_PULLSECRET" '.auths = (.auths // {}) * $secret' .dockerconfigjson > .update_docker.json
# oc set data secret/pull-secret -n openshift-config  --from-file=.dockerconfigjson=.update_docker.json

ssh -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} <<EOF


oc get ns openshift-logging >/dev/null 2>&1 || oc create ns openshift-logging

oc create secret generic lokicred-secret \
  --from-literal=endpoint=https://s3.jp-tok.cloud-object-storage.appdomain.cloud \
  --from-literal=region=jp-tok \
  --from-literal=bucketnames=cos-standard-upi-validation \
  --from-literal=access_key_id=${COS_STANDARD_KEY} \
  --from-literal=access_key_secret=${COS_STANDARD_SECRET} \
  -n openshift-logging \
  --dry-run=client -o yaml | oc apply -f -

oc create secret generic cw-secret \
  --from-literal=aws_access_key_id=${AWS_ACCESS_KEY_ID} \
  --from-literal=aws_secret_access_key=${AWS_SECRET_ACCESS_KEY} \
  -n openshift-logging \
  --dry-run=client -o yaml | oc apply -f -
EOF



# Install dependency
python3 -m pip install --break-system-packages kubernetes openshift

ansible-galaxy collection install kubernetes.core
ansible-playbook -i clo-inventory -e @logging_vars.yaml playbooks/ocp-cluster-logging.yml
