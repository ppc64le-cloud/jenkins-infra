def call() {
    script {
        if (fileExists('slacksummary.txt')) {
            slacksummary = readFile 'slacksummary.txt'
        }
        else {
            slacksummary = "Tier test didn't run"
        }
        if ( env.FAILED_STAGE != ""  ) {
            env.MESSAGE = "ODF version: `${env.ODF_VERSION}` , ODF build: `${env.ODF_BUILD}`,  Tier: `${env.TIER_TEST}` , Failed Stage: `${env.FAILED_STAGE}` "
        }
        else {
            env.MESSAGE = "ODF version: `${env.ODF_VERSION}` , ODF build: `${env.ODF_BUILD}` , Tier: `${env.TIER_TEST}` , Tier test summary:`${slacksummary}`"
        }
    }
}
