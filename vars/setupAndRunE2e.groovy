def call(){
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
                echo 'Creating var.yaml'
                rm -rf ~/.ansible
                ansible all -m setup -a 'gather_subset=!all'
                cd ${WORKSPACE}/ocp4-playbooks-extras
                cp examples/e2e_vars.yaml e2e_vars.yaml
                sed -i "s|e2e_tests_enabled:.*$|e2e_tests_enabled: true|g" e2e_vars.yaml
                sed -i "s|e2e_tests_git:.*$|e2e_tests_git: ${E2E_GIT}|g" e2e_vars.yaml
                sed -i "s|e2e_tests_git_branch:.*$|e2e_tests_git_branch: ${E2E_BRANCH}|g" e2e_vars.yaml
                sed -i "s|e2e_tests_exclude_list_url:.*$|e2e_tests_exclude_list_url: ${E2E_EXCLUDE_LIST}|g" e2e_vars.yaml
                sed -i "s|golang_tarball:.*$|golang_tarball: ${GOLANG_TARBALL}|g" e2e_vars.yaml
                sed -i "s|github_token:.*$|github_token: ${GITHUB_TOKEN}|g" e2e_vars.yaml
                cat e2e_vars.yaml
                cp examples/inventory ./e2e_inventory
                sed -i "s|localhost|${BASTION_IP}|g" e2e_inventory
                sed -i 's/ansible_connection=local/ansible_connection=ssh/g' e2e_inventory
                sed -i "s|ssh|ssh ansible_ssh_private_key_file=${WORKSPACE}/deploy/id_rsa|g" e2e_inventory
                cat e2e_inventory
                cp ansible.cfg ansible_copy.cfg
                echo "[ssh_connection]" >> ansible.cfg
                echo "ssh_args = -C -o ControlMaster=auto -o ControlPersist=120m -o ServerAliveInterval=30" >> ansible.cfg
                cat ansible.cfg
                ansible-playbook  -i e2e_inventory -e @e2e_vars.yaml playbooks/ocp-e2e.yml
                rm -f ansible.cfg
                mv ansible_copy.cfg ansible.cfg
                rm -f ansible_copy.cfg
            '''
        }
        catch (err) {
            echo 'Error ! ansible setup failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
