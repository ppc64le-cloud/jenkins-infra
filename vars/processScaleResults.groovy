def call() {
    script {
       def scale_summary = ""
       def summary = ""
        if (fileExists('deploy/time_taken_namespaces') && fileExists('deploy/time_taken_namespaces')) {
                    scale_summary = readFile 'deploy/time_taken_namespaces'
                    scale_summary.split('\n').each { line ->
                        if ( line  != null) {
                            if (line.contains('real')){
                                env.real_time_ns = line.split()[0].split("-")[1]
                            }
                            if (line.contains('user')){
                                env.user_time_ns = line.split()[1].split("-")[1]
                            }
                            if (line.contains('sys')){
                                env.system_time_ns = line.split()[2].split("-")[1]
                            }
                        }
                    }
                    scale_summary = readFile 'deploy/time_taken_deployments'
                    scale_summary.split('\n').each { line ->
                        if ( line  != null) {
                            if (line.contains('real')){
                                env.real_time_dp = line.split()[0].split("-")[1]
                            }
                            if (line.contains('user')){
                                env.user_time_dp = line.split()[1].split("-")[1]
                            }
                            if (line.contains('sys')){
                                env.system_time_dp = line.split()[2].split("-")[1]
                            }
                        }
                    }
                summary = "Namespace time: " + env.real_time_ns + ", " + "Deployment time: " + env.real_time_dp
        }
        else
        {
            step([$class: 'JUnitResultArchiver', allowEmptyResults: true,  testResults: 'scripts/dummy-test-summary.xml'])
            currentBuild.result = 'FAILURE'
            summary = "Scale test didn't run"
        }
        if ( env.INFRA_ISSUE == "true" ) {
            summary = env.ERROR_MESSAGE
        }
        if ( env.OPENSHIFT_IMAGE != ""  ) {
            env.OPENSHIFT_INSTALL_TARBALL = env.OPENSHIFT_IMAGE
        }
        OCP4_BUILD = env.OPENSHIFT_INSTALL_TARBALL.split(':')[1]
        env.MESSAGE = "scale summary:`${summary}`, OCP4 Build: `${OCP4_BUILD}`, RHCOS: `${env.RHCOS_IMAGE_NAME}` "
    }
}
