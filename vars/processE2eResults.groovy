def call() {
    script {
        def e2e_summary = ""
        int fails_per_threshold
        int unstable_per_threshold
        if (fileExists('deploy/summary.txt')) {
            e2e_summary = readFile 'deploy/summary.txt'
            if (e2e_summary?.trim()){
                if (e2e_summary.contains('fail')) {
                    e2e_summary = e2e_summary.trim()
                    STR = e2e_summary.split(',')
                    FAILS = STR[0].split()
                    PASS = STR[1].split()
                    SKIP = STR[2].split()
                    TOTAL = FAILS[0].toInteger() + PASS[0].toInteger() + SKIP[0].toInteger()
                    fails_per_threshold = (TOTAL - SKIP[0].toInteger())
                    unstable_per_threshold = (TOTAL - SKIP[0].toInteger()) * 0.05 - 1
                }
                else {
                    e2e_summary = e2e_summary.trim()
                    STR = e2e_summary.split(',')
                    PASS = STR[0].split()
                    SKIP = STR[1].split()
                    TOTAL = PASS[0].toInteger() + SKIP[0].toInteger()
                    fails_per_threshold = (TOTAL - SKIP[0].toInteger())
                    unstable_per_threshold = (TOTAL - SKIP[0].toInteger()) * 0.05 - 1
                }
            }
        }
        if ( env.INFRA_ISSUE == "true" ) {
            e2e_summary = env.ERROR_MESSAGE
            currentBuild.result = 'FAILURE'
        }
        if ( env.OPENSHIFT_IMAGE != ""  ) {
            env.OPENSHIFT_INSTALL_TARBALL = env.OPENSHIFT_IMAGE
        }
        if ( fileExists('deploy/junit_e2e.xml')) {
            sh '''
                sed -i 's|^<testsuite |<testsuite errors="0"  |' deploy/junit_e2e.xml
                sed  -i  's|<property name=.*property>||'  deploy/junit_e2e.xml
            '''
            step([$class: 'XUnitPublisher', thresholds: [[$class: 'FailedThreshold', failureThreshold: fails_per_threshold.toString(), unstableThreshold: '10' ]], tools: [[$class: 'JUnitType', pattern: 'deploy/junit_e2e.xml']]])
        }
        else {
            step([$class: 'JUnitResultArchiver', allowEmptyResults: true,  testResults: 'hack/dummy-test-summary.xml'])
            currentBuild.result = 'FAILURE'
        }
        //If summary file exists but doesn't have results then marking it as unstable
        if (fileExists('deploy/summary.txt') && !e2e_summary?.trim()) {
            currentBuild.result = 'UNSTABLE'
            e2e_summary = "E2e test didn't run"
        }
        OCP4_BUILD = env.OPENSHIFT_INSTALL_TARBALL.split(':')[1]
        env.MESSAGE = "e2e summary:`${e2e_summary}`, OCP4 Build: `${OCP4_BUILD}`, RHCOS: `${env.RHCOS_IMAGE_NAME}` "
    }
}
