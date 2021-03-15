def call(){
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            // Run the script for Acmeair installation
            sh '''
                TAG="ppc64le"
                git clone https://${GITHUB_TOKEN}@github.ibm.com/powercloud/acmeair.git
                cd acmeair
                git checkout ${TAG}
                ./deploy.sh
                 echo "Acmeair deployed resources"
                 oc get all
                 total_pods_created=`oc get pods|tail -n +2|wc -l`
                 #Time to get the pods deployment in running state
                 timeout=300
                 time_taken=0
                 #Pods Verification
                 while [ "$time_taken" -le "$timeout" ]
                 do
                    actual_pods_running=`oc get pods|grep -i "running"|wc -l`
                    if [ "$total_pods_created" -eq "$actual_pods_running" ];then
                        break
                    else
                        time_taken=`expr $time_taken + 60`
                        sleep 60
                    fi
                 done
                 echo "Total pods in running state $actual_pods_running out of $total_pods_created"
                 oc get pods
            '''
        }
        catch (err) {
            echo 'Error ! Tearing off the cluster. Failed to install acmeair !'
            throw err
        }
    }
}
