def call()
{
    if ( env.POWERVS == "true" ) {
        if ( env.SCRIPT_DEPLOYMENT == "true" ) {
            //PowerVS script
            sh (returnStdout: false, script: "export CLOUD_API_KEY=$IBMCLOUD_API_KEY && cd ${WORKSPACE}/deploy && make $TARGET:clean || true")
        }
        else {
            //PowerVS
            sh (returnStdout: false, script: "cd ${WORKSPACE}/deploy && make $TARGET:clean || true")
            sh (returnStdout: false, script: "cd ${WORKSPACE}/deploy && make $TARGET:clean || true")
            sh (returnStdout: false, script: "cd ${WORKSPACE}/deploy && make $TARGET:clean || true")
        }
    }
    else {
        // PowerVM
        sh (returnStdout: false, script: "cd ${WORKSPACE}/deploy && make $TARGET:clean || true")
        sh (returnStdout: false, script: "cd ${WORKSPACE}/deploy && make $TARGET:clean || true")
        sh (returnStdout: false, script: "cd ${WORKSPACE}/deploy && make $TARGET:clean || true")
        sh(returnStatus: false, returnStdout: false, script: "openstack  --os-auth-url \"${env.OS_AUTH_URL}\" --insecure flavor delete ${env.MASTER_TEMPLATE}")
        sh(returnStatus: false, returnStdout: false, script: "openstack  --os-auth-url \"${env.OS_AUTH_URL}\" --insecure flavor delete ${env.WORKER_TEMPLATE}")
        sh(returnStatus: false, returnStdout: false, script: "openstack  --os-auth-url \"${env.OS_AUTH_URL}\" --insecure flavor delete ${env.BOOTSTRAP_TEMPLATE}")
        sh(returnStatus: false, returnStdout: false, script: "openstack  --os-auth-url \"${env.OS_AUTH_URL}\" --insecure flavor delete ${env.BASTION_TEMPLATE}")
    }
}
