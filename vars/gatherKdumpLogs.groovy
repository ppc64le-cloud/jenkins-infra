def call(){
    script {
        ansiColor('xterm') {
        echo ""
        }
        try {
            sh '''
                cd ${WORKSPACE}/deploy
                scp -o 'StrictHostKeyChecking no' -i id_rsa ${WORKSPACE}/scripts/kdump/kdump_gather_logs.sh root@${BASTION_IP}:
                ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=5' -o 'ServerAliveCountMax=1200' -i id_rsa root@${BASTION_IP} "chmod 755 kdump_gather_logs.sh;~/kdump_gather_logs.sh | tee ~/kdump-logs.log 2>&1 ; exit"
            '''
        }
        catch(err) {
            echo 'Running Gather kdump logs script failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
