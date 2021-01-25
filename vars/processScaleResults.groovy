def call() {
    script {
       def real_time_ns
       def user_time_ns
       def system_time_ns
       def real_time_dp
       def user_time_dp
       def system_time_dp
       boolean infra_issue = false
       def error_message = ""
       def scale_summary = ""
       def summary = ""
       def clusterInfoFields = [:]
       def clusterInfo = [:]
        if (fileExists('deploy/time_taken_namespaces') && fileExists('deploy/time_taken_namespaces')) {
                    scale_summary = readFile 'deploy/time_taken_namespaces'
                    scale_summary.split('\n').each { line ->
                        if ( line  != null) {
                            if (line.contains('real')){
                                real_time_ns = line.split()[0].split("-")[1]
                            }
                            if (line.contains('user')){
                                user_time_ns = line.split()[1].split("-")[1]
                            }
                            if (line.contains('sys')){
                                system_time_ns = line.split()[2].split("-")[1]
                            }
                        }
                    }
                    scale_summary = readFile 'deploy/time_taken_deployments'
                    scale_summary.split('\n').each { line ->
                        if ( line  != null) {
                            if (line.contains('real')){
                                real_time_dp = line.split()[0].split("-")[1]
                            }
                            if (line.contains('user')){
                                user_time_dp = line.split()[1].split("-")[1]
                            }
                            if (line.contains('sys')){
                                system_time_dp = line.split()[2].split("-")[1]
                            }
                        }
                    }
                summary = "Namespace time: " + real_time_ns + ", " + "Deployment time: " + real_time_dp
        }
        else
        {
            step([$class: 'JUnitResultArchiver', allowEmptyResults: true,  testResults: 'hack/dummy-test-summary.xml'])
            currentBuild.result = 'FAILURE'
            summary = "Scale test didn't run"
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
            clusterInfo['ocp_build'] = env.OPENSHIFT_INSTALL_TARBALL
            clusterInfo['master_node_cpu'] = "${MASTER_PROCESSORS}"
            clusterInfo['master_node_mem'] = "${MASTER_MEMORY}"
            clusterInfo['worker_node_cpu'] = "${WORKER_PROCESSORS}"
            clusterInfo['worker_node_mem'] = "${WORKER_MEMORY}"
            clusterInfo['cluster_masters'] = "${NUM_OF_MASTERS}"
            clusterInfo['cluster_workers'] = "${NUM_OF_WORKERS}"
            clusterInfo['system_type']     = "${SYSTEM_TYPE}"
            clusterInfo['coreos_build'] = env.RHCOS_IMAGE_NAME
            clusterInfo['real_time_namespace'] = real_time_ns
            clusterInfo['user_time_namespace'] = user_time_ns
            clusterInfo['system_time_namespace'] = system_time_ns
            clusterInfo['namespaces'] = "${SCALE_NUM_OF_NAMESPACES}"
            clusterInfo['real_time_deployments'] = real_time_dp
            clusterInfo['user_time_deployments'] = user_time_dp
            clusterInfo['system_time_deployments'] = system_time_dp
            clusterInfo['deployments'] = "${SCALE_NUM_OF_DEPLOYMENTS}"
            clusterInfoFields['clusterinfo'] = clusterInfo

            if ( ! infra_issue ) {
                step([$class: 'InfluxDbPublisher', selectedTarget: 'influxdbmollypowervsscale', customDataMap: clusterInfoFields])
            }
            else{
                echo "Skipping this run from updating the dashboard database, as this is an infra related issue"
                summary = error_message
            }
            OCP4_BUILD = env.OPENSHIFT_INSTALL_TARBALL.split(':')[1]
            env.MESSAGE = "scale summary:`${summary}`, OCP4 Build: `${OCP4_BUILD}`, RHCOS: `${env.RHCOS_IMAGE_NAME}` "
    }
}
