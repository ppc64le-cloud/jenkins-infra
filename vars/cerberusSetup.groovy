def call() {
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
                echo "---RUN CERBERUS TO MONITOR THE CLUSTER HEALTH---"
                cd ${WORKSPACE}/deploy
                scp -o 'StrictHostKeyChecking no' -i id_rsa  root@${BASTION_IP}:/root/openstack-upi/auth/kubeconfig  ${WORKSPACE}/files/
                cd ${WORKSPACE}
                cp ${WORKSPACE}/files/custom_check_res_usage.py ${WORKSPACE}
                cp ${WORKSPACE}/files/cerberus-config.yaml ${WORKSPACE}
                git clone $OPENSHIFT_CERBERUS_GIT_DEPLOY_PROJECT
                git checkout $OPENSHIFT_CERBERUS_GIT_DEPLOY_BRANCH
                cd ${WORKSPACE}/cerberus
                pip3 install -r requirements.txt
                cd ${WORKSPACE}
                cp ${WORKSPACE}/custom_check_res_usage.py ${WORKSPACE}/cerberus/custom_checks/
                cp ${WORKSPACE}/cerberus-config.yaml ${WORKSPACE}/cerberus/config/
                export KUBECONFIG=${WORKSPACE}/files/kubeconfig
                cd ${WORKSPACE}/cerberus
                python3 start_cerberus.py --config ./config/cerberus-config.yaml 2>&1 > cerberus_output.txt
            '''
        }
        catch (err) {
            echo 'Error ! Cerberus monitoring failed!'
            throw err
        }
    }
}
