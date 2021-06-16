def call() {
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
                if [ -d ${WORKSPACE}/deploy ];then
                    cd ${WORKSPACE}/deploy
                else
                    exit 1
                fi
                BASTION_IP=$(make terraform:output TERRAFORM_DIR=.${TARGET} TERRAFORM_OUTPUT_VAR=bastion_public_ip )
                [ $? -ne 0 ] && exit 1
                if [ ! -z "${BASTION_IP}" ]; then
                    scp -o 'StrictHostKeyChecking no' -i id_rsa ${WORKSPACE}/scripts/stability-check.sh root@${BASTION_IP}:
                    ssh -o 'StrictHostKeyChecking no' -i id_rsa root@${BASTION_IP} "chmod 755 stability-check.sh;
                    ./stability-check.sh 2>&1 | tee -a stability-check.log;
                    exit"
                fi
            '''
        }
        catch (err) {
            echo 'Error ! Vm stability check failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
