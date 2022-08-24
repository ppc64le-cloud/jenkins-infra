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
                cp examples/ocp_ovnkube_cni_vars.yaml vars.yaml
                sed -i "s|ocp_ovnkube_cni:.*$|ocp_ovnkube_cni: true|g" vars.yaml
                sed -i "s|ovnkube_cni_tests_enabled:.*$|ovnkube_cni_tests_enabled: true|g" vars.yaml
                sed -i "s|egressfirewall_tests_enabled:.*$|egressfirewall_tests_enabled: true|g" vars.yaml
                sed -i "s|egressip_tests_enabled:.*$|egressip_tests_enabled: true|g" vars.yaml
                sed -i "s|networkpolicy_tests_enabled:.*$|networkpolicy_tests_enabled: true|g" vars.yaml
                cat vars.yaml
                cp examples/inventory .
                sed -i "s|localhost|${BASTION_IP}|g" inventory
                sed -i 's/ansible_connection=local/ansible_connection=ssh/g' inventory
                sed -i "s|ssh|ssh ansible_ssh_private_key_file=${WORKSPACE}/deploy/id_rsa|g" inventory
                cat inventory
                echo "[ssh_connection]" >> ansible.cfg
                echo "ssh_args = -C -o ControlMaster=auto -o ControlPersist=120m -o ServerAliveInterval=30" >> ansible.cfg
                cat ansible.cfg
                ansible-playbook  -i inventory -e @vars.yaml playbooks/ocp-ovnkube-cni.yml
            '''
        }
        catch (err) {
            echo 'Error ! OVN CNI Validation Failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
