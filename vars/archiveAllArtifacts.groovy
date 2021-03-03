def call( String... file_names ) {
    script {
        sh (returnStdout: false, script: "/bin/bash ${WORKSPACE}/hack/capture-artifacts-ocp4.sh || true")
        file_names.each { file ->
                   archiveArtifacts allowEmptyArchive: true, artifacts: file, fingerprint: true, onlyIfSuccessful: false
        }
    }
}