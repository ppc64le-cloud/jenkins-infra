def call(String buildStatus = 'STARTED', String buildurl, String e2esummary, String rhcos) {
  // Build status of null means successful.
    buildStatus = buildStatus ?: 'SUCCESS'
    // Replace encoded slashes.
    def decodedJobName = env.JOB_NAME.replaceAll("%2F", "/")

    def colorSlack

    if (buildStatus == 'STARTED') {
        colorSlack = '#D4DADF'
    } else if (buildStatus == 'SUCCESS') {
        colorSlack = '#BDFFC3'
    } else if (buildStatus == 'UNSTABLE') {
        colorSlack = '#FFFE89'
    } else {
        colorSlack = '#FF9FA1'
    }

    def msgSlack = """${buildStatus}: `${decodedJobName}`
    Test Summary: `${e2esummary}` #${env.BUILD_NUMBER}: (<${env.BUILD_URL}|Open>)
    OCP4 BUILD: `${buildurl}` RHCOS: `${rhcos}` """

    slackSend(color: colorSlack, message: msgSlack)
}
