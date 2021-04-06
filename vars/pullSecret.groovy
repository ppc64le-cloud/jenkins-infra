def call(){
    withCredentials([string(credentialsId: 'PULL_SECRET', variable: 'FILE')]) {
        sh 'echo  $FILE > $PULL_SECRET_FILE'
    }
}
