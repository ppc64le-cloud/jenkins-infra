def call( String... file_names ) {
    script {
        file_names.each { file ->
                   archiveArtifacts allowEmptyArchive: true, artifacts: file, fingerprint: true, onlyIfSuccessful: false
        }
    }
}