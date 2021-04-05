def call(String job_name, String file_name) {
    script {
        step([  $class: 'CopyArtifact', filter: file_name, fingerprintArtifacts: true, projectName: job_name, target: "${WORKSPACE}/deploy/artifactory", selector: lastSuccessful() ])
    }
}