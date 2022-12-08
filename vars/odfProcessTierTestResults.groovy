def call() {
    script {
        if (fileExists('slacksummary.txt')) {
            slack = readFile 'slacksummary.txt'
            slacksummary = slack.trim()
        }
        else {
            slacksummary = "Tier test didn't run"
        }
        if (env.OPENSHIFT_IMAGE != "") {
              OCP4_BUILD = env.OPENSHIFT_IMAGE.split(':')[1]
        }
        if ( env.FAILED_STAGE != ""  ) {
            env.MESSAGE = "   OCP Build: `${OCP4_BUILD}`, ODF Build: `${env.ODF_BUILD}` ,  Tier: `${env.TIER_TEST}` , Failed Stage: `${env.FAILED_STAGE}` "
        }
        else {
            env.MESSAGE = "   OCP Build: `${OCP4_BUILD}`, ODF Build: `${env.ODF_BUILD}` , Tier: `${env.TIER_TEST}` , Summary: `${slacksummary}` "
        }
        if ( fileExists("test_results_tier${TIER_TEST}_1.xml")) {
            step([$class: 'JUnitResultArchiver', skipMarkingBuildUnstable: true, allowEmptyResults: true,  testResults: 'test_results*.xml'])

        }
        else {
            step([$class: 'JUnitResultArchiver', allowEmptyResults: true,  testResults: 'scripts/dummy-test-summary.xml'])
            currentBuild.result = 'FAILURE'
        }
    }
}
