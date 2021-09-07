def call(){
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            writeFile file: 'ingress.yaml', text: 'spec:\n  nodePlacement:\n    nodeSelector:\n      matchLabels:\n        node-role.kubernetes.io/infra: ""'
            sh 'cat ingress.yaml'
            writeFile file: 'registry.yaml', text: 'spec:\n  nodeSelector:\n    node-role.kubernetes.io/infra: ""'
            sh 'cat registry.yaml'
            sh '''#!/bin/bash
                cd ${WORKSPACE}
                apt-get update
                apt-get install -y npm time uuid-runtime
                npm install -g @alexlafroscia/yaml-merge
                git clone https://github.com/RobertKrawitz/OpenShift4-tools.git
                cd ${WORKSPACE}/OpenShift4-tools
                echo './clusterbuster -N ${SCALE_NUM_OF_NAMESPACES} -d 0 ;while [ `oc get ns | grep clusterbuster | grep -v Active | wc -l` -ne 0 ];do sleep .0000000001;done' > ./namespaces.sh
                chmod +x ./namespaces.sh
                /usr/bin/time -o ${WORKSPACE}/time_taken_namespaces -f  "real-%E user-%U system-%S" ./namespaces.sh
                ./clusterbuster --cleanup
                sleep 60
                for nodes in $(oc get node --selector='node-role.kubernetes.io/worker' --no-headers | grep 'worker-1\\|worker-2' | awk '{print $1}'); do oc label node $nodes node-role.kubernetes.io/infra=""; done
                oc get nodes
                oc get ingresscontroller default -n openshift-ingress-operator -o yaml > base_1.yaml && yaml-merge base_1.yaml ~/ingress.yaml | kubectl apply -f -
                oc get pod -n openshift-ingress -o wide
                sleep 600;
                oc get pod -n openshift-ingress -o wide
                oc get configs.imageregistry.operator.openshift.io cluster -o yaml > base_2.yaml && yaml-merge base_2.yaml ~/registry.yaml | kubectl apply -f -
                oc get pods -o wide -n openshift-image-registry
                sleep 900
                oc get pods -o wide -n openshift-image-registry
                for nodes in $(oc get node --selector='node-role.kubernetes.io/worker' --no-headers | grep 'worker-1\\|worker-2' | awk '{print $1}'); do oc adm cordon $nodes; done
                echo './clusterbuster -d ${SCALE_NUM_OF_DEPLOYMENTS} -B ltccci ;while [ `oc get pods -n ltccci-0 | grep -v NAME | grep -v Running | wc -l` -ne 0 ];do sleep .0000000001;done' > deployments.sh
                chmod +x deployments.sh
                /usr/bin/time -o ${WORKSPACE}/time_taken_deployments -f  'real-%E user-%U system-%S'  sh deployments.sh
                ./clusterbuster --cleanup -B ltccci
                for nodes in $(oc get node --selector='node-role.kubernetes.io/worker' --no-headers | grep 'worker-1\\|worker-2' | awk '{print $1}'); do oc adm uncordon $nodes; done
                oc get nodes
                echo 'Get the Cluster Operators'
                oc get co
            '''
        }
        catch (err) {
            echo 'Error ! Scale test failed to run !'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
