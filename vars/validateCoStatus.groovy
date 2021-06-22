def call() {
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            // Get Cluster Operators status
            sh '''#!/bin/bash
                echo 'oc version'
                oc version
                echo 'Setting up kubectl!'
                oc get nodes
                cd ${WORKSPACE}
                echo "Gathering logs"
                oc adm must-gather
                tar -czvf must-gather.tar.gz ./must-gather*
                sleep_time=300
                flag=0
                for((i=0;i<18;++i)) do
                    flag=0
                    while IFS= read -r co; do
                        degraded_text=$(echo $co | cut -d " " -f 5)
                        if [ "$degraded_text" = "True" ]; then
                            flag=1
                            break
                        fi
                    done < <(oc get co | tail -n +2)
                    if [ $flag -eq 1 ]; then
                        echo "sleeping for 5 mins all co are not up"
                        sleep $sleep_time
                    else
                        echo "All cluster operators are up and running"
                        echo "All cluster operators were up and running" > ${WORKSPACE}/co_status.txt
                        oc get co
                        break
                    fi
                done
                if [ $flag -eq 1 ]; then
                    oc get co
                    echo "Cluster operators were in degraded state after 90 mins" > ${WORKSPACE}/co_status.txt
                    echo "Cluster operators are in degraded state after 90 mins Tearing off cluster!!"
                    exit 1
                fi
                '''
        }
        catch (err) {
            echo 'Error ! All Cluster Operators are not Up .. Skipping e2e  !'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
