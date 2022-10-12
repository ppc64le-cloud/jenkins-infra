def call(){
    withCredentials([string(credentialsId: 'ODF_AUTH_YAML', variable: 'FILE')]) {
        sh 'echo  $FILE > $WORKSPACE/deploy/data/auth.yaml'
    }
}
