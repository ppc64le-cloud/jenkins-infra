def call(String auth_url)
{
    // Updating tempate file with env variables
    sh (returnStdout: false, script: "/bin/bash ${WORKSPACE}/hack/capture-artifacts-ocp4.sh || true")
    sh (returnStdout: false, script: "export CLOUD_API_KEY=$IBMCLOUD_API_KEY && cd ${WORKSPACE}/deploy && make $TARGET:clean || true")
}
