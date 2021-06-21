def call() {
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
                echo 'Deploying Cluster!'
                if [ "${SCRIPT_DEPLOYMENT}" = false ]; then
                    cd ${WORKSPACE}/deploy
                    make $TARGET || true
                    retries=0
                    until [ "$retries" -ge 3 ]
                    do
                        if [ "$retries" -eq 2 ]; then
                            make $TARGET:redeploy
                            sleep 60
                        else
                            make $TARGET:redeploy || true
                        fi
                        retries=$((retries+1))
                        sleep 10
                    done
                else
                    export CLOUD_API_KEY=$IBMCLOUD_API_KEY
                    cd ${WORKSPACE}/deploy
                    make $TARGET
                fi
            '''
            if ( env.POWERVS == "true"  ) {
                if (env.SCRIPT_DEPLOYMENT == "true" ){
                    env.BASTION_IP=sh(returnStdout: true, script: "cd ${WORKSPACE}/deploy && make $TARGET:output TERRAFORM_OUTPUT_VAR=bastion_public_ip|grep -Eo '[0-9]{1,3}(.[0-9]{1,3}){3}'").trim()
                }
                else{
                    env.BASTION_IP=sh(returnStdout: true, script: "cd ${WORKSPACE}/deploy && make terraform:output TERRAFORM_DIR=.${TARGET} TERRAFORM_OUTPUT_VAR=bastion_public_ip").trim()
                }
            }
            else {
                 env.BASTION_IP=sh(returnStdout: true, script: "cd ${WORKSPACE}/deploy && make terraform:output TERRAFORM_DIR=.${TARGET} TERRAFORM_OUTPUT_VAR=bastion_ip").trim()
            }
            env.DEPLOYMENT_STATUS = true
        }
        catch (err) {
            env.FAILED_STAGE=env.STAGE_NAME
	        def timeout_sec=0
            def timeout_hrs =  env.WAIT_FOR_DEBUG.toInteger()
            if ( timeout_hrs != 0 ) {
                timeout_sec=timeout_hrs*60*60
            }
            echo "HOLDING THE Cluster FOR DEBUGGING, FOR $timeout_hrs Hours"
            sleep timeout_sec
            throw err
        }
    }
}
