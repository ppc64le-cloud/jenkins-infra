def call(){
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
                cd ${WORKSPACE}/deploy
                scp -o 'StrictHostKeyChecking no' -i id_rsa ${WORKSPACE}/hack/cron.sh root@${BASTION_IP}:
                ssh -o 'StrictHostKeyChecking no' -i id_rsa root@${BASTION_IP} "chmod 755 cron.sh;
                                                                                echo '0 */2 * * * root ~/cron.sh >> ~/cron.log 2>&1' >> /etc/crontab;
                                                                                exit"
            '''
        } catch (err) {
            echo 'Running Crontab script failed!'
            getArtifactsAndCleanOcp4()
            throw err
        }
    }
}
