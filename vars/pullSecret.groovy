def call(){
    withCredentials([file(credentialsId: 'PULL_SECRET', variable: 'FILE')]) {
        sh 'cp  $FILE $PULL_SECRET_FILE'
    }
}
