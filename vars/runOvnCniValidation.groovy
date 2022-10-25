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
                cp examples/ocp_ovnkube_cni_vars.yaml ocp_ovnkube_cni_vars.yaml
                sed -i "s|ocp_ovnkube_cni:.*$|ocp_ovnkube_cni: true|g" ocp_ovnkube_cni_vars.yaml
                sed -i "s|ovnkube_cni_tests_enabled:.*$|ovnkube_cni_tests_enabled: true|g" ocp_ovnkube_cni_vars.yaml
                sed -i "s|egressfirewall_tests_enabled:.*$|egressfirewall_tests_enabled: true|g" ocp_ovnkube_cni_vars.yaml
                sed -i "s|egressip_tests_enabled:.*$|egressip_tests_enabled: true|g" ocp_ovnkube_cni_vars.yaml
                sed -i "s|networkpolicy_tests_enabled:.*$|networkpolicy_tests_enabled: true|g" ocp_ovnkube_cni_vars.yaml
                cat ocp_ovnkube_cni_vars.yaml
                cp examples/inventory ./cni_inventory
                sed -i "s|localhost|${BASTION_IP}|g" cni_inventory
                sed -i 's/ansible_connection=local/ansible_connection=ssh/g' cni_inventory
                sed -i "s|ssh|ssh ansible_ssh_private_key_file=${WORKSPACE}/deploy/id_rsa|g" cni_inventory
                cat cni_inventory
                cp ansible.cfg ansible_copy.cfg
                echo "[ssh_connection]" >> ansible.cfg
                echo "ssh_args = -C -o ControlMaster=auto -o ControlPersist=120m -o ServerAliveInterval=30" >> ansible.cfg
                cat ansible.cfg
                ansible-playbook  -i cni_inventory -e @ocp_ovnkube_cni_vars.yaml playbooks/ocp-ovnkube-cni.yml
                rm -f ansible.cfg
                mv ansible_copy.cfg ansible.cfg
                rm -f ansible_copy.cfg
            '''
        }
        catch (err) {
            echo 'Error ! OVN CNI Validation Failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
