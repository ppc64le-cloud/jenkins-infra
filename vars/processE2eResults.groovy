def call() {
    script {
        def e2e_summary = ""
        def clusterInfoFields = [:]
        def clusterInfo = [:]
        int fails_per_threshold
        int unstable_per_threshold
        boolean infra_issue = false
        def error_message = ""
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
        def logContent = Jenkins.getInstance().getItemByFullName(env.JOB_NAME).getBuildByNumber(Integer.parseInt(env.BUILD_NUMBER)).logFile.text
        def logContent_modified=logContent.toLowerCase()
        def infra_errors = readFile 'files/infra-issues.txt'
        infra_errors.split('\n').each { line ->
            line1=line.toLowerCase()
            if ( line1  != null) {
                if (logContent_modified.contains(line1)){
                    if ( ! DEPLOYMENT_STATUS ){
                        infra_issue = true
                        error_message = line1
                    }
                }
            }
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
        else{
            currentBuild.result = 'FAILURE'
        }

        if ( env.POWERVS == "true"  ) {
            clusterInfo['ocp_build'] = env.OPENSHIFT_INSTALL_TARBALL
            clusterInfo['master_node_cpu'] = "${MASTER_PROCESSORS}"
            clusterInfo['master_node_mem'] = "${MASTER_MEMORY}"
            clusterInfo['worker_node_cpu'] = "${WORKER_PROCESSORS}"
            clusterInfo['worker_node_mem'] = "${WORKER_MEMORY}"
            clusterInfo['cluster_masters'] = "${NUM_OF_MASTERS}"
            clusterInfo['cluster_workers'] = "${NUM_OF_WORKERS}"
            clusterInfo['system_type']     = "${SYSTEM_TYPE}"
            clusterInfoFields['clusterinfo'] = clusterInfo
        }
        //For powerVM
        else {
            clusterInfo['ocp_build'] = env.OPENSHIFT_INSTALL_TARBALL
            clusterInfo['master_node_cpu'] = "${MASTER_VCPUS}"
            clusterInfo['master_node_mem'] = "${MASTER_MEMORY}"
            clusterInfo['worker_node_cpu'] = "${WORKER_VCPUS}"
            clusterInfo['worker_node_mem'] = "${WORKER_MEMORY}"
            clusterInfo['cluster_masters'] = "${NUM_OF_MASTERS}"
            clusterInfo['cluster_workers'] = "${NUM_OF_WORKERS}"
            clusterInfo['server_type'] = "${HARDWARE_CHOSE}"
            clusterInfo['coreos_build'] = env.RHCOS_IMAGE_NAME
            clusterInfoFields['clusterinfo'] = clusterInfo
        }

        if ( ! infra_issue ) {
            if ( env.POWERVS == "true"  ) {
                step([$class: 'InfluxDbPublisher', selectedTarget: 'influxdbmollypowervs', customDataMap: clusterInfoFields])
            }
            else{
                step([$class: 'InfluxDbPublisher', selectedTarget: 'influxdbmolly', customDataMap: clusterInfoFields])
            }
        }
        else{
            echo "Skipping this run from updating the dashboard database, as this is an infra related issue"
            e2e_summary = error_message
        }
        OCP4_BUILD = env.OPENSHIFT_INSTALL_TARBALL.split(':')[1]
        env.MESSAGE = "e2e summary:`${e2e_summary}`, OCP4 Build: `${OCP4_BUILD}`, RHCOS: `${env.RHCOS_IMAGE_NAME}` "
    }
}
