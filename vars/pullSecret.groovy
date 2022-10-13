def call(){
    withCredentials([string(credentialsId: 'PULL_SECRET', variable: 'FILE')]) {
        sh 'set +x; echo  $FILE > $PULL_SECRET_FILE'
    }
}
