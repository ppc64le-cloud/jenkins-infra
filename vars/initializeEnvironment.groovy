def call() {
    script {
      ansiColor('xterm') {
           echo ""
      }
      try {
           sh '''
            echo 'Initializing supporting repos and keys !'
            cd ${WORKSPACE}/deploy
            make init
            make keys
            make setup-dependencies
           '''
      }
      catch (err) {
           echo 'Error ! ENV setup failed!'
           getArtifactsAndCleanOcp4()
           throw err
      }
   }
}
