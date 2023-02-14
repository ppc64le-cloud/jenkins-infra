def call(){
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
               scp -i ${WORKSPACE}/deploy/id_rsa -o 'StrictHostKeyChecking=no' ${WORKSPACE}/deploy/data/pull-secret.txt root@${BASTION_IP}:/root/
               scp -i ${WORKSPACE}/deploy/id_rsa -o 'StrictHostKeyChecking=no' ${WORKSPACE}/deploy/data/auth.yaml root@${BASTION_IP}:/root/
               ssh -o 'StrictHostKeyChecking=no' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "oc set data secret/pull-secret -n openshift-config --from-file=.dockerconfigjson=/root/pull-secret.txt;"
               echo "export PLATFORM=${PLATFORM}" > env_vars.sh
               echo "export OCP_VERSION=${OCP_RELEASE}" >> env_vars.sh
               echo "export OCS_VERSION=${ODF_VERSION}" >> env_vars.sh
               echo "export PVS_API_KEY=${IBMCLOUD_API_KEY}" >> env_vars.sh
               echo "export RHID_USERNAME=${REDHAT_USERNAME}" >> env_vars.sh
               echo "export RHID_PASSWORD=${REDHAT_PASSWORD}" >> env_vars.sh
               echo "export PVS_SERVICE_INSTANCE_ID=${SERVICE_INSTANCE_ID}" >> env_vars.sh
               echo "export TIER_TEST=${TIER_TEST}" >> env_vars.sh
               echo "export VAULT_SUPPORT=${ENABLE_VAULT}" >> env_vars.sh
               echo "export FIPS_ENABLEMENT=${ENABLE_FIPS}" >> env_vars.sh
               [ ! -z "$UPGRADE_OCS_VERSION" ] && echo "export UPGRADE_OCS_VERSION=${UPGRADE_OCS_VERSION}" >> env_vars.sh
               [ ! -z "$UPGRADE_OCS_REGISTRY" ] && echo "export UPGRADE_OCS_REGISTRY=${UPGRADE_OCS_REGISTRY}" >> env_vars.sh
               [ ! -z "$OCS_REGISTRY_IMAGE" ] && echo "export OCS_REGISTRY_IMAGE=${OCS_REGISTRY_IMAGE}" >> env_vars.sh
               [ ! -z "$RERUN_TIER_TEST" ] && echo "export RERUN_TIER_TEST=${RERUN_TIER_TEST}" >> env_vars.sh
               scp -i ${WORKSPACE}/deploy/id_rsa -o 'StrictHostKeyChecking=no' env_vars.sh root@${BASTION_IP}:/root/
               ssh -o 'StrictHostKeyChecking=no' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "git clone https://github.com/Neha-dot-Yadav/ocs-upi-kvm.git"
               ssh -o 'StrictHostKeyChecking=no' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "cd /root/ocs-upi-kvm; git submodule update --init;"
               ssh -o 'StrictHostKeyChecking=no' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "cp /root/openstack-upi/metadata.json /root/;"
               ssh -o 'StrictHostKeyChecking=no' -o 'ServerAliveInterval=5' -o 'ServerAliveCountMax=1200' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} 'chmod 0755 env_vars.sh; source env_vars.sh; cd /root/ocs-upi-kvm/scripts/helper; /bin/bash ./kustomize.sh > kustomize.log'
               [ "$ENABLE_VAULT" = "true" ] && ssh -o 'StrictHostKeyChecking=no' -o 'ServerAliveInterval=5' -o 'ServerAliveCountMax=1200' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} 'cd /root/ocs-upi-kvm/scripts/helper; /bin/bash ./vault-setup.sh > vault-setup.log'
               ssh -o 'StrictHostKeyChecking=no' -o 'ServerAliveInterval=5' -o 'ServerAliveCountMax=1200' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} 'source env_vars.sh; cd /root/ocs-upi-kvm/scripts; /bin/bash ./setup-ocs-ci.sh > setup-ocs-ci.log'
            '''
        }
        catch (err) {
            echo 'Error ! Setup script failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
