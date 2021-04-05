def call(String email, String buildStatus = 'STARTED') {
    buildStatus = buildStatus ?: 'SUCCESS'
    // Replace encoded slashes.
    def decodedJobName = env.JOB_NAME.replaceAll("%2F", "/")

    emailext (
        subject: "${buildStatus}: Job '${decodedJobName} [${env.BUILD_NUMBER}]'",
        body: """<p>${buildStatus}: Job '${decodedJobName} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${decodedJobName} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
        to: email
    )
 }
