def call(){
    script {
        try {
            ansiColor('xterm') {
                echo ""
            }
            sh '''
                cd ${WORKSPACE}/deploy
                ibmcloud login -a cloud.ibm.com -r ${VPCREGION} -g ${RESOURCE_GROUP} -q --apikey=${IBMCLOUD_API_KEY}
                ibmcloud pi service-target ${SERVICE_INSTANCE_CRN}
                ./powervs-hack/scripts/create-cluster.sh
                cp ${CLUSTER_DIR}/metadata.json ./
            '''
            def logContent = Jenkins.getInstance().getItemByFullName(env.JOB_NAME).getBuildByNumber(Integer.parseInt(env.BUILD_NUMBER)).logFile.text
            if (logContent.contains("DEPLOYMENT_SUCCESS=success")){
                env.DEPLOYMENT_STATUS="true"
                env.MESSAGE="IPI Deployment Successful Build:${OPENSHIFT_INSTALL_RELEASE_IMAGE_OVERRIDE}"
            }
            else {
                env.DEPLOYMENT_STATUS="false"
                env.MESSAGE="IPI Deployment Failed Build:${OPENSHIFT_INSTALL_RELEASE_IMAGE_OVERRIDE}"
                throw err
            }
        }
        catch (err) {
            echo 'Error ! IPI Cluster Creation Failed'
            throw err
        }
    }
}
