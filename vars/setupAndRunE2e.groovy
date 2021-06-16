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
                cd ${WORKSPACE}/ansible_extra
                cp examples/e2e_vars.yaml vars.yaml
                sed -i "s|e2e_tests_enabled:.*$|e2e_tests_enabled: true|g" vars.yaml
                sed -i "s|e2e_tests_git:.*$|e2e_tests_git: ${E2E_GIT}|g" vars.yaml
                sed -i "s|e2e_tests_git_branch:.*$|e2e_tests_git_branch: ${E2E_BRANCH}|g" vars.yaml
                sed -i "s|e2e_tests_exclude_list_url:.*$|e2e_tests_exclude_list_url: ${E2E_EXCLUDE_LIST}|g" vars.yaml
                sed -i "s|golang_tarball:.*$|golang_tarball: ${GOLANG_TARBALL}|g" vars.yaml
                sed -i "s|github_token:.*$|github_token: ${GITHUB_TOKEN}|g" vars.yaml
                cat vars.yaml
                cp examples/inventory .
                sed -i "s|localhost|${BASTION_IP}|g" inventory
                sed -i 's/ansible_connection=local/ansible_connection=ssh/g' inventory
                sed -i "s|ssh|ssh ansible_ssh_private_key_file=${WORKSPACE}/deploy/id_rsa|g" inventory
                cat inventory
                echo "[ssh_connection]" >> ansible.cfg
                echo "ssh_args = -C -o ControlMaster=auto -o ControlPersist=120m -o ServerAliveInterval=30" >> ansible.cfg
                cat ansible.cfg
                ansible-playbook  -i inventory -e @vars.yaml playbooks/main.yml
            '''
        }
        catch (err) {
            echo 'Error ! ansible setup failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
