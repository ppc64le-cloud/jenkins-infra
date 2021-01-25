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
            sh '''
                cd ${WORKSPACE}/deploy
                scp -o 'StrictHostKeyChecking no' -i id_rsa ${WORKSPACE}/ingress.yaml root@${BASTION_IP}:
                scp -o 'StrictHostKeyChecking no' -i id_rsa ${WORKSPACE}/registry.yaml root@${BASTION_IP}:
                ssh -o 'StrictHostKeyChecking no' -i id_rsa root@${BASTION_IP} "yum update -y;
                yum install -y npm time;
                npm install -g @alexlafroscia/yaml-merge;
                git clone https://github.com/RobertKrawitz/OpenShift4-tools.git;
                cd ~/OpenShift4-tools;
                echo './clusterbuster -N ${SCALE_NUM_OF_NAMESPACES} -d 0 ;while [ `oc get ns | grep clusterbuster | grep -v Active | wc -l` -ne 0 ];do sleep .0000000001;done' > ./namespaces.sh;
                chmod +x ./namespaces.sh;
                /usr/bin/time  -o ~/time_taken_namespaces -f  'real-%E user-%U system-%S' ./namespaces.sh;
                ./clusterbuster --cleanup;
                sleep 60;
                oc label node worker-0 node-role.kubernetes.io/infra="";
                oc label node worker-1 node-role.kubernetes.io/infra="";
                oc get nodes;
                oc get ingresscontroller default -n openshift-ingress-operator -o yaml > base_1.yaml && yaml-merge base_1.yaml ~/ingress.yaml | kubectl apply -f - ;
                oc get pod -n openshift-ingress -o wide;
                sleep 600;
                oc get pod -n openshift-ingress -o wide;
                oc get configs.imageregistry.operator.openshift.io cluster -o yaml > base_2.yaml && yaml-merge base_2.yaml ~/registry.yaml | kubectl apply -f - ;
                oc get pods -o wide -n openshift-image-registry ;
                sleep 900 ;
                oc get pods -o wide -n openshift-image-registry ;
                echo './clusterbuster -d ${SCALE_NUM_OF_DEPLOYMENTS} -B ltccci ;while [ `oc get pods -n ltccci-0 | grep -v NAME | grep -v Running | wc -l` -ne 0 ];do sleep .0000000001;done' > deployments.sh;
                chmod +x deployments.sh ;
                /usr/bin/time -o ~/time_taken_deployments -f  'real-%E user-%U system-%S'  sh deployments.sh ;
                ./clusterbuster --cleanup -B ltccci ;
                oc get nodes ;
                echo 'Get the Cluster Operators' ;
                oc get co "
            '''
        }
        catch (err) {
            echo 'Error ! Tearing off the cluster !'
            getArtifactsAndCleanOcp4()
            throw err
        }
    }
}
