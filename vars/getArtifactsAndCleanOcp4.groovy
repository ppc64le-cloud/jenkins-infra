def call()
{
    if ( env.POWERVS == "true" ) {
        sh (returnStdout: false, script: "/bin/bash ${WORKSPACE}/hack/capture-artifacts-ocp4.sh || true")
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
        sh(returnStatus: false, returnStdout: false, script: "openstack  --os-auth-url \"${env.AUTH_URL}\" --insecure flavor delete ${env.MASTER_TEMPLATE}")
        sh(returnStatus: false, returnStdout: false, script: "openstack  --os-auth-url \"${env.AUTH_URL}\" --insecure flavor delete ${env.WORKER_TEMPLATE}")
        sh(returnStatus: false, returnStdout: false, script: "openstack  --os-auth-url \"${env.AUTH_URL}\" --insecure flavor delete ${env.BOOTSTRAP_TEMPLATE}")
        sh(returnStatus: false, returnStdout: false, script: "openstack  --os-auth-url \"${env.AUTH_URL}\" --insecure flavor delete ${env.BASTION_TEMPLATE}")
    }
}
