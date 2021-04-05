def call() {
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            // Run the script to setup kube config
            sh (returnStdout: false, script: "/bin/bash ${WORKSPACE}/scripts/setupkubeconfig-ocp4.sh || true")
            sh '''
                echo 'oc version'
                oc version
                echo 'Setting up kubectl!'
                oc get nodes
                echo 'Get the Cluster Operators'
                oc get co
            '''
        }
        catch (err) {
            echo 'Error ! Kubectl setup  !'
            throw err
        }
    }
}
