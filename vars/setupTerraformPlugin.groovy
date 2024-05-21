def call() {
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
                echo ' get the plugin from artifactory repo !'
                wget --quiet --no-check-certificate https://releases.hashicorp.com/terraform/${TERRAFORM_VER}/terraform_${TERRAFORM_VER}_linux_amd64.zip -O terraform_${TERRAFORM_VER}_linux_amd64.zip
                [ $? -eq 1 ] && echo "unable to get oc tarball" && exit 1
                mkdir -p ~/.terraform.d/plugins/linux_amd64/
                unzip terraform_${TERRAFORM_VER}_linux_amd64.zip -d /usr/local/bin
                terraform -version
            '''
            }
        catch (err) {
            echo 'Error ! Terraform setup plugin failed'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
