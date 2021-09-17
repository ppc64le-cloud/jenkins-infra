def call() {
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            env.CLUSTER_ID=""
            sh '''
                echo 'Deploying Cluster!'
                if [ "${SCRIPT_DEPLOYMENT}" = false ]; then
                    cd ${WORKSPACE}/deploy
                    exit_status=0
                    make $TARGET || exit_status=$?
                    retries=0
                    until [ "$retries" -ge 3 ]
                    do
                        if [ "$retries" -eq 2 ]; then
                            if [ "$exit_status" -ne 0 ] ;then
                                if [ "${POWERVS}" = false  ]; then
                                    CLUSTER_ID=$(make terraform:output TERRAFORM_DIR=.${TARGET} TERRAFORM_OUTPUT_VAR="cluster_id")
                                    if ! [ "$CLUSTER_ID" = "" ]; then
                                        SERVER_LIST=$(openstack server list --insecure | grep "$CLUSTER_ID" | grep -v "bastion\\|bootstrap" | awk '{print $4}')
                                        echo "$SERVER_LIST" | while IFS= read -r line ; do openstack server reboot --insecure $line; done || true
                                        sleep 180
                                    fi
                                fi
                                exit_status=0
                            fi
                            make $TARGET:redeploy
                            sleep 60
                        else
                            if [ "$exit_status" -ne 0 ]; then
                                if [ "${POWERVS}" = false  ]; then
                                    CLUSTER_ID=$(make terraform:output TERRAFORM_DIR=.${TARGET} TERRAFORM_OUTPUT_VAR="cluster_id")
                                    if ! [ "$CLUSTER_ID" = "" ]; then
                                        SERVER_LIST=$(openstack server list --insecure | grep "$CLUSTER_ID" | grep -v "bastion\\|bootstrap" | awk '{print $4}')
                                        echo "$SERVER_LIST" | while IFS= read -r line ; do openstack server reboot --insecure $line; done || true
                                        sleep 180
                                    fi
                                fi
                                exit_status=0
                            fi
                            make $TARGET:redeploy|| exit_status=$?
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
                    env.BASTION_IP=sh(returnStdout: true, script: "cd ${WORKSPACE}/deploy && make $TARGET:output TERRAFORM_OUTPUT_VAR=bastion_public_ip|grep -Eo '[0-9]{1,3}(\\.[0-9]{1,3}){3}'").trim()
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
