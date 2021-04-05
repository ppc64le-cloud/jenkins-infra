def call () {
    script {
            def clusterInfoFields = [:]
            def clusterInfo = [:]
            //PowerVS
            if ( env.POWERVS == "true"  ) {
                clusterInfo['master_node_cpu'] = "${MASTER_PROCESSORS}"
                clusterInfo['master_node_mem'] = "${MASTER_MEMORY}"
                clusterInfo['worker_node_cpu'] = "${WORKER_PROCESSORS}"
                clusterInfo['worker_node_mem'] = "${WORKER_MEMORY}"
                clusterInfo['system_type']     = "${SYSTEM_TYPE}"
            }
            //PowerVC
            else {
            clusterInfo['master_node_cpu'] = "${MASTER_VCPUS}"
            clusterInfo['master_node_mem'] = "${MASTER_MEMORY}"
            clusterInfo['worker_node_cpu'] = "${WORKER_VCPUS}"
            clusterInfo['worker_node_mem'] = "${WORKER_MEMORY}"
            clusterInfo['server_type'] = "${HARDWARE_CHOSE}"
            }

            clusterInfo['ocp_build'] = env.OPENSHIFT_INSTALL_TARBALL
            clusterInfo['cluster_masters'] = "${NUM_OF_MASTERS}"
            clusterInfo['cluster_workers'] = "${NUM_OF_WORKERS}"
            clusterInfo['coreos_build'] = env.RHCOS_IMAGE_NAME
            clusterInfo['real_time_namespace'] = env.real_time_ns
            clusterInfo['user_time_namespace'] = env.user_time_ns
            clusterInfo['system_time_namespace'] = env.system_time_ns
            clusterInfo['namespaces'] = "${SCALE_NUM_OF_NAMESPACES}"
            clusterInfo['real_time_deployments'] = env.real_time_dp
            clusterInfo['user_time_deployments'] = env.user_time_dp
            clusterInfo['system_time_deployments'] = env.system_time_dp
            clusterInfo['deployments'] = "${SCALE_NUM_OF_DEPLOYMENTS}"
            clusterInfoFields['clusterinfo'] = clusterInfo
            if ( env.INFRA_ISSUE == "false" ) {
                if ( env.POWERVS == "true"  ) {
                    step([$class: 'InfluxDbPublisher', selectedTarget: 'influxdb-scale-powervs', customDataMap: clusterInfoFields])
                }
                else {
                    step([$class: 'InfluxDbPublisher', selectedTarget: 'influxdb-scale-powervm', customDataMap: clusterInfoFields])
                }
            }
            else{
                echo "Skipping this run from updating the dashboard database, as this is an infra related issue"
            }
    }
}
