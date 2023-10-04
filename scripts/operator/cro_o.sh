#!/bin/bash
echo 'Creating var.yaml'
rm -rf ~/.ansible
ansible all -m setup -a 'gather_subset=!all'
cd ${WORKSPACE}/ocp4-playbooks-extras
cp examples/cro_vars.yaml cro_vars.yaml
sed -i "s|cro_install_operator:.*$|cro_install_operator: true|g" cro_vars.yaml
sed -i "s|cro_e2e:.*$|cro_e2e: true|g" cro_vars.yaml
sed -i "s|cro_enable_global_secret:.*$|cro_enable_global_secret: false|g" cro_vars.yaml
sed -i "s|cro_ocp_version:.*|cro_ocp_version: ${OCP_RELEASE}|g" cro_vars.yaml
sed -i "s|cro_go_tarball:.*$|cro_go_tarball: ${GOLANG_TARBALL}|g" cro_vars.yaml
cat cro_vars.yaml
#Inventory Details
cp examples/inventory ./cro_inventory
sed -i "s|localhost|${BASTION_IP}|g" cro_inventory
sed -i 's/ansible_connection=local/ansible_connection=ssh/g' cro_inventory
sed -i "s|ssh|ssh ansible_ssh_private_key_file=${WORKSPACE}/deploy/id_rsa|g" cro_inventory
cat cro_inventory

cat ansible.cfg
ansible-playbook  -i cro_inventory -e @cro_vars.yaml playbooks/cro.yml