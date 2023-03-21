def call()
{
    script {
        def logContent = Jenkins.getInstance().getItemByFullName(env.JOB_NAME).getBuildByNumber(Integer.parseInt(env.BUILD_NUMBER)).logFile.text
        if (env.VM_ID_PREFIX) {
            env.CLUSTER_ID_PREFIX = "${VM_ID_PREFIX}"
        }
        else {
            env.CLUSTER_ID_PREFIX = "${INSTANCE_NAME}"
        }

        env.CLUSTER_ID     = logContent.find("${CLUSTER_ID_PREFIX}-[A-Za-z0-9]{4}")
        env.COS_KEY_PREFIX = env.JOB_BASE_NAME-env.CLUSTER_ID.split("-")[-1]
        env.COS_CRN        = "d63f4ac3-7fd3-4221-8502-1648dd91a43d"
        env.COS_REGION     = "jp-tok"
        env.COS_BUCKET     = "rdr-validation-object-storage-jenkins-cos-standard-yw9"
        env.RESOURCE_GROUP = "upi-resource-group"

        if (env.CLUSTER_ID != null) {
            sh '''
                #!/bin/bash
                echo "Gathering logs"
                cd ${WORKSPACE}
                ibmcloud login -a cloud.ibm.com -r ${COS_REGION} -g ${RESOURCE_GROUP} -q --apikey=${IBMCLOUD_API_KEY}
                ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "oc adm must-gather; tar -czf must-gather.tar.gz ./must-gather*"
                scp -i ${WORKSPACE}/deploy/id_rsa -o StrictHostKeyChecking=no root@${BASTION_IP}:~/must-gather.tar.gz .
                ibmcloud cos config crn --crn ${COS_CRN}
                ocp_version=$(ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@$BASTION_IP "oc get clusterversion -o=jsonpath='{.items[0].status.history[0].version}'")
                echo "Uploading the logs"
                ibmcloud cos object-put --body must-gather.tar.gz --bucket ${COS_BUCKET} --region ${COS_REGION} --key ${COS_KEY_PREFIX}-${ocp_version}.tar.gz
                rm -rf must-gather.tar.gz
            '''
        }
    }
}
