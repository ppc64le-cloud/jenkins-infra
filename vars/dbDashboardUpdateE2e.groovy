def call() {
    script {
        def clusterInfoFields = [:]
        def clusterInfo = [:]
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

        if ( env.INFRA_ISSUE == "false" ) {
            if ( env.POWERVS == "true"  ) {
                step([$class: 'InfluxDbPublisher', selectedTarget: 'influxdb-e2e-powervs', customDataMap: clusterInfoFields])
            }
            else{
                step([$class: 'InfluxDbPublisher', selectedTarget: 'influxdb-e2e-powervm', customDataMap: clusterInfoFields])
            }
        }
        else{
            echo "Skipping this run from updating the dashboard database, as this is an infra related issue"
        }
    }
}
