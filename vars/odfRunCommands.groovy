def call(){
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "--------------------START-------------------"; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get clusterversion"; oc get clusterversion; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get nodes"; oc get nodes; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get csv -A"; oc get csv -A; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get pods -n openshift-local-storage"; oc get pods -n openshift-local-storage; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get localvolumeset -n openshift-local-storage"; oc get localvolumeset -n openshift-local-storage; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get pods -n openshift-storage"; oc get pods -n openshift-storage; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get pv"; oc get pv; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get pvc -n openshift-storage"; oc get pvc -n openshift-storage; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get sc -n openshift-storage"; oc get sc -n openshift-storage; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get storagecluster -n openshift-storage"; oc get storagecluster -n openshift-storage; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get cephcluster -n openshift-storage"; oc get cephcluster -n openshift-storage; echo) >> odf-commands.txt "               
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get storagesystem -n openshift-storage"; oc get storagesystem -n openshift-storage; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get storagecluster -n openshift-storage -o yaml"; oc get storagecluster -n openshift-storage -o yaml; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get backingstore -n openshift-storage"; oc get backingstore -n openshift-storage; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get bucketclass -n openshift-storage"; oc get bucketclass -n openshift-storage; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get noobaa -n openshift-storage"; oc get noobaa -n openshift-storage; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "oc get noobaa -n openshift-storage -o yaml"; oc get noobaa -n openshift-storage -o yaml; echo) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "ODF build:"; op=`oc get csv -n openshift-storage |grep odf-operator | awk {'print $1'}`  oc get csv $op  -n openshift-storage -o yaml | grep full_version) >> odf-commands.txt "
               ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=60' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "(echo "--------------------END-------------------"; echo) >> odf-commands.txt "
               scp -i ${WORKSPACE}/deploy/id_rsa -o StrictHostKeyChecking=no root@${BASTION_IP}:/root/odf-commands.txt ${WORKSPACE}
               tail -1 ${WORKSPACE}/odf-commands.txt  > odfbuild
            '''
             odfbuild = readFile 'odfbuild'
             env.ODF_BUILD = "`${odfbuild}`"
        }
        catch (err) {
            echo 'Error ! capturing command o/p failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
