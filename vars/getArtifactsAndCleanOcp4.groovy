def call(String auth_url)
{
    // Updating tempate file with env variables
    sh (returnStdout: false, script: "/bin/bash ${WORKSPACE}/hack/capture-artifacts-ocp4.sh || true")
    sh (returnStdout: false, script: "cd ${WORKSPACE}/deploy && make $TARGET:clean || true")
    if ( "${POWERVS}" == "false" ) {
        sh(returnStatus: false, returnStdout: false, script: "openstack  --os-auth-url \"${auth_url}\" --insecure flavor delete ${env.MASTER_TEMPLATE}")
        sh(returnStatus: false, returnStdout: false, script: "openstack  --os-auth-url \"${auth_url}\" --insecure flavor delete ${env.WORKER_TEMPLATE}")
        sh(returnStatus: false, returnStdout: false, script: "openstack  --os-auth-url \"${auth_url}\" --insecure flavor delete ${env.BOOTSTRAP_TEMPLATE}")
        sh(returnStatus: false, returnStdout: false, script: "openstack  --os-auth-url \"${auth_url}\" --insecure flavor delete ${env.BASTION_TEMPLATE}")
    }
}
