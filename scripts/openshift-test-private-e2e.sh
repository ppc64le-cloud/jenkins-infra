#!/bin/bash

set -e

echo "=========================================="
echo "OpenShift Test Private E2E Setup"
echo "=========================================="

# Clean up any previous ansible cache
echo "Cleaning up ansible cache..."
rm -rf ~/.ansible
ansible all -m setup -a 'gather_subset=!all'

# Navigate to ocp4-playbooks-extras directory
cd ${WORKSPACE}/ocp4-playbooks-extras

export GITHUB_USERNAME=${GITHUB_USER}
export GITHUB_ACCESS_TOKEN=${GITHUB_TOKEN}

# Create openshift-test-private e2e configuration file
echo "Creating openshift_test_private_vars.yaml configuration..."
cp examples/openshift-test-private-e2e-vars.yaml openshift_test_private_vars.yaml

# Configure openshift-test-private e2e settings
sed -i "s|openshift_test_private_validation:.*$|openshift_test_private_validation: true|g" openshift_test_private_vars.yaml
sed -i "s|openshift_test_private_golang_tarball:.*$|openshift_test_private_golang_tarball: ${GOLANG_TARBALL}|g" openshift_test_private_vars.yaml
sed -i "s|openshift_test_private_e2e_repo:.*$|openshift_test_private_e2e_repo: https://github.com/openshift/openshift-tests-private.git|g" openshift_test_private_vars.yaml
sed -i "s|openshift_test_private_git_branch:.*$|openshift_test_private_git_branch: ${OPENSHIFT_TEST_PRIVATE_BRANCH:-main}|g" openshift_test_private_vars.yaml
sed -i "s|openshift_test_private_cleanup:.*$|openshift_test_private_cleanup: ${OPENSHIFT_TEST_PRIVATE_CLEANUP:-true}|g" openshift_test_private_vars.yaml
sed -i "s|testcase_filters :.*$|testcase_filters : ${OpenshiftTestPrivateFilter:-}|g" openshift_test_private_vars.yaml

# Display configuration for verification
echo "=========================================="
echo "Configuration file content:"
echo "=========================================="
cat openshift_test_private_vars.yaml

# Create inventory file
echo "Creating inventory file..."
cp examples/inventory ./openshift_test_private_inventory
sed -i "s|localhost|${BASTION_IP}|g" openshift_test_private_inventory
sed -i 's/ansible_connection=local/ansible_connection=ssh/g' openshift_test_private_inventory
sed -i "s|ssh|ssh ansible_ssh_private_key_file=${WORKSPACE}/deploy/id_rsa|g" openshift_test_private_inventory

echo "=========================================="
echo "Inventory file content:"
echo "=========================================="
cat openshift_test_private_inventory

echo "=========================================="
echo "Ansible configuration:"
echo "=========================================="
cat ansible.cfg

# Run ansible playbook for openshift-test-private e2e
echo "=========================================="
echo "Running openshift-test-private e2e playbook..."
echo "=========================================="
ansible-playbook -i openshift_test_private_inventory -e @openshift_test_private_vars.yaml playbooks/openshift-test-private-e2e.yml

echo "=========================================="
echo "OpenShift Test Private E2E Completed"
echo "=========================================="
