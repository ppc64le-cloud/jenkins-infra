#!/bin/bash
echo 'Creating compliance_vars.yaml'
rm -rf ~/.ansible
ansible all -m setup -a 'gather_subset=!all'
cd ${WORKSPACE}/ocp4-playbooks-extras
cp examples/nmstate_vars.yaml ocp_nmstate_vars.yaml
sed -i "s|nmstate_enabled:.*$|nmstate_enabled: true|g" ocp_nmstate_vars.yaml
sed -i "s|nmstate_install_operator:.*$|nmstate_install_operator: true|g" ocp_nmstate_vars.yaml
sed -i "s|nmstate_upgrade_channel:.*$|nmstate_upgrade_channel: stable|g" ocp_nmstate_vars.yaml
sed -i "s|nmstate_directory:.*$|nmstate_directory: /tmp/nmstate|g" ocp_nmstate_vars.yaml
sed -i "s|nmstate_catalogsource_image:.*$|nmstate_catalogsource_image: ${NMSTATE_CATALOGSOURCE_IMAGE}|g" ocp_nmstate_vars.yaml
sed -i "s|nmstate_golang_tarball:.*$|nmstate_golang_tarball: ${GOLANG_TARBALL}|g" ocp_nmstate_vars.yaml
sed -i "s|nmstate_e2e:.*$|nmstate_e2e: true|g" ocp_nmstate_vars.yaml
sed -i "s|nmstate_e2e_git_repository:.*$|nmstate_e2e_git_repository: https://github.com/openshift/kubernetes-nmstate.git|g" ocp_nmstate_vars.yaml
sed -i "s|nmstate_git_username:.*$|nmstate_git_username: ${GITHUB_USER}|g" ocp_nmstate_vars.yaml
sed -i "s|nmstate_git_token:.*$|nmstate_git_token: ${GITHUB_TOKEN}|g" ocp_nmstate_vars.yaml
sed -i "s|nmstate_git_branch:.*$|nmstate_git_branch: main|g" ocp_nmstate_vars.yaml
sed -i "s|nmstate_cleanup:.*$|nmstate_cleanup: false|g" ocp_nmstate_vars.yaml
sed -i "s|golang_installation_path:.*$|golang_installation_path: /usr/local/go|g" ocp_nmstate_vars.yaml
sed -i "s|golang_tarball_url:.*$|golang_tarball_url: ${GOLANG_TARBALL}|g" ocp_nmstate_vars.yaml
sed -i "s|nmstate_namespace:.*$|nmstate_namespace: openshift-nmstate|g" ocp_nmstate_vars.yaml
cat ocp_nmstate_vars.yaml
#Inventory Details
cp examples/inventory ./nmstate_inventory
sed -i "s|localhost|${BASTION_IP}|g" nmstate_inventory
sed -i 's/ansible_connection=local/ansible_connection=ssh/g' nmstate_inventory
sed -i "s|ssh|ssh ansible_ssh_private_key_file=${WORKSPACE}/deploy/id_rsa|g" nmstate_inventory
cat nmstate_inventory
cat ansible.cfg
ansible-playbook  -i nmstate_inventory -e @ocp_nmstate_vars.yaml playbooks/ocp-nmstate-operator.yml