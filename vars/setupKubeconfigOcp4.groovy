def call() {
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            // Run the script to setup kube config
            sh (returnStdout: false, script: "/bin/bash ${WORKSPACE}/scripts/setupkubeconfig-ocp4.sh || true")
        }
        catch (err) {
            echo 'Error ! Kubectl setup  !'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
