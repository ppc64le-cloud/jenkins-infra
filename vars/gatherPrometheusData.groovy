def call(){
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
               ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "oc get --raw /debug/pprof/profile --as=system:admin" > cpu-pre.pprof || true
               ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "oc get --raw /debug/pprof/heap --as=system:admin" > heap-pre.pprof || true
               ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "oc --insecure-skip-tls-verify exec -n openshift-monitoring prometheus-k8s-0 -- tar czf - -C /prometheus . " > prometheus.tar.gz || true
            '''
        }
        catch (err) {
            echo 'Error ! Gather Prometheus Data failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
