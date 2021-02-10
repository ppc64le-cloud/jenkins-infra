def call() {
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
                echo ' get the plugin from artifactory repo !'
                wget https://github.com/IBM-Cloud/terraform-provider-ibm/releases/download/v1.9.0/linux_amd64.zip
                mkdir -p ~/.terraform.d/plugins/linux_amd64/
                unzip -o linux_amd64.zip -d ~/.terraform.d/plugins/linux_amd64/
            '''
            }
        catch (err) {
            echo 'Error ! ENV setup failed!'
            getArtifactsAndCleanOcp4()
            throw err
        }
    }
}
