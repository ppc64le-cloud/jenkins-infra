def call() {
    script {
        def logContent = Jenkins.getInstance().getItemByFullName(env.JOB_NAME).getBuildByNumber(Integer.parseInt(env.BUILD_NUMBER)).logFile.text
        def logContent_modified=logContent.toLowerCase()
        def infra_errors = readFile 'files/infra-issues.txt'
        env.INFRA_ISSUE = false
        env.ERROR_MESSAGE = ""
        infra_errors.split('\n').each { line ->
            line1=line.toLowerCase()
            if ( line1  != null) {
                if (logContent_modified.contains(line1)){
                    if ( env.DEPLOYMENT_STATUS == "false" ){
                        env.INFRA_ISSUE = true
                        env.ERROR_MESSAGE = line1
                    }
                }
            }
        }
    }
}
