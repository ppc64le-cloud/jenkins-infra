def call() {
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
                k=0
                echo "---SHOW CRON LOGS FROM LONGEVITY TEST - ONCE EVERY 8 hours / FOR 4 WEEKS---"
                while [ $k -le ${TIME_TO_GATHER_LOGS} ]; do
                    sleep ${SLEEP_TIME_FOR_CRON_LOG_GATHER}
                    cd ${WORKSPACE}/deploy
                    scp -i id_rsa -o StrictHostKeyChecking=no root@${BASTION_IP}:~/cron.log cron_longevity.log
                    echo "---SHOWING  CRON LOGS---"
                    cat cron_longevity.log
                    k=$((k+1))
                    echo $k
                done
           '''
        }
        catch (err) {
            echo 'Error ! Fetching Cron log failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
