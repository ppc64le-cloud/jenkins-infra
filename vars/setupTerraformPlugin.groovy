def call() {
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
                echo ' get the plugin from artifactory repo !'
                wget https://releases.hashicorp.com/terraform/${TERRAFORM_VER}/terraform_${TERRAFORM_VER}_linux_amd64.zip
                mkdir -p ~/.terraform.d/plugins/linux_amd64/
                unzip terraform_${TERRAFORM_VER}_linux_amd64.zip -d /usr/local/bin
                terraform -version
            '''
            }
        catch (err) {
            echo 'Error ! Terraform setup plugin failed'
            throw err
        }
    }
}
