#!/bin/bash
set -e

echo "Creating ocp_cso_vars.yaml"

rm -rf ~/.ansible
ansible all -m setup -a 'gather_subset=!all'

cd ${WORKSPACE}/ocp4-playbooks-extras

cp examples/ocp_cso_vars.yaml ocp_cso_vars.yaml

sed -i "s|cso_enabled:.*$|cso_enabled: true|g" ocp_cso_vars.yaml
sed -i "s|cso_namespace:.*$|cso_namespace: quay-registry|g" ocp_cso_vars.yaml
sed -i "s|cso_catalogsource_name:.*$|cso_catalogsource_name: cso-catsrc|g" ocp_cso_vars.yaml
sed -i "s|cso_catalogsource_image:.*$|cso_catalogsource_image: ${CSO_CATALOGSOURCE_IMAGE}|g" ocp_cso_vars.yaml
sed -i "s|cso_operator_channel:.*$|cso_operator_channel : ${CSO_Channel}|g" ocp_cso_vars.yaml
sed -i "s|cso_enable_global_secret:.*$|cso_enable_global_secret: false|g" ocp_cso_vars.yaml

cat <<EOF > /tmp/pull-secret.json
${CSO_PULL_SECRET_INPUT}
EOF

cat <<EOF > /tmp/icsp.yaml
${CSO_ICSP_INPUT}
EOF

sed -i "s|cso_pull_secrets_path:.*$|cso_pull_secrets_path: /tmp/pull-secret.json|g" ocp_cso_vars.yaml
sed -i "s|cso_icsp_path:.*$|cso_icsp_path: /tmp/icsp.yaml|g" ocp_cso_vars.yaml

cat ocp_cso_vars.yaml

cp examples/inventory ./cso_inventory
sed -i "s|localhost|${BASTION_IP}|g" cso_inventory
sed -i 's/ansible_connection=local/ansible_connection=ssh/g' cso_inventory
sed -i "s|ssh|ssh ansible_ssh_private_key_file=${WORKSPACE}/deploy/id_rsa|g" cso_inventory

ansible-playbook -i cso_inventory -e @ocp_cso_vars.yaml playbooks/ocp-cso.yml
