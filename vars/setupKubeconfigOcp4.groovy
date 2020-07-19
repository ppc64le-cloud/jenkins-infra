def call()
{
    // Run the script to setup kube config
    sh (returnStdout: false, script: "/bin/bash ${WORKSPACE}/hack/setupkubeconfig_ocp4.sh || true")
}
