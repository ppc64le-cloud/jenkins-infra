def call(String auth_url)
{
    // Updating tempate file with env variables
    sh (returnStdout: false, script: "/bin/bash ${WORKSPACE}/hack/capture_artifacts.sh")
    KEYPAIR=sh(returnStdout: true, script: "cd ${WORKSPACE}/canary-deployments && make terraform:output TERRAFORM_DIR=.deploy-power-powervc TERRAFORM_OUTPUT_VAR=keypair-name || true") 
    sh (returnStdout: false, script: "cd ${WORKSPACE}/canary-deployments && make $TARGET:clean")
    sh (returnStdout: false, script: "openstack --os-auth-url \"${auth_url}\" --insecure keypair delete \"${KEYPAIR}\"")
}