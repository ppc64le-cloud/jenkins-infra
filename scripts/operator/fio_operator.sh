#!/bin/bash
echo 'Creating compliance_vars.yaml'
rm -rf ~/.ansible
ansible all -m setup -a 'gather_subset=!all'
cd ${WORKSPACE}/ocp4-playbooks-extras
cp examples/fio_vars.yaml ocp_fio_vars.yaml
sed -i "s|fio_enabled:.*$|fio_enabled: true|g" ocp_fio_vars.yaml
sed -i "s|fio_catalogsource_image:.*$|fio_catalogsource_image: ${FIO_CATALOGSOURCE_IMAGE}|g" ocp_fio_vars.yaml
sed -i "s|fio_golang_tarball:.*$|fio_golang_tarball: ${GOLANG_TARBALL}|g" ocp_fio_vars.yaml
sed -i "s|fio_e2e:.*$|fio_e2e: true|g" ocp_fio_vars.yaml
sed -i "s|fio_e2e_git_repository:.*$|fio_e2e_git_repository: https://github.com/openshift/openshift-tests-private.git|g" ocp_fio_vars.yaml
sed -i "s|fio_git_username:.*$|fio_git_username: ${GITHUB_USER}|g" ocp_fio_vars.yaml
sed -i "s|fio_git_token:.*$|fio_git_token: ${GITHUB_TOKEN}|g" ocp_fio_vars.yaml
sed -i "s|fio_git_branch:.*$|fio_git_branch: master|g" ocp_fio_vars.yaml
sed -i "s|fio_cleanup:.*$|fio_cleanup: false|g" ocp_fio_vars.yaml
cat ocp_fio_vars.yaml
#Inventory Details
cp examples/inventory ./fio_inventory
sed -i "s|localhost|${BASTION_IP}|g" fio_inventory
sed -i 's/ansible_connection=local/ansible_connection=ssh/g' fio_inventory
sed -i "s|ssh|ssh ansible_ssh_private_key_file=${WORKSPACE}/deploy/id_rsa|g" fio_inventory
cat fio_inventory
cat ansible.cfg
ansible-playbook  -i fio_inventory -e @ocp_fio_vars.yaml playbooks/ocp-fio.yaml