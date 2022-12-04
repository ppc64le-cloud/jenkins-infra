#!/bin/bash
##
## This scripts runs ODF deplpy script from the ocs-upi-kvm in the remote bastion node
##

 ssh -o 'StrictHostKeyChecking=no' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "cp -r /root/openstack-upi/auth/ /root/; cp /usr/local/bin/oc /root/ocs-upi-kvm/src/ocs-ci/bin/; mkdir /root/bin; cp /usr/local/bin/oc /root/bin/;"
 ssh -o 'StrictHostKeyChecking=no' -o 'ServerAliveInterval=5' -o 'ServerAliveCountMax 1200'  -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "source /root/env_vars.sh; cd /root/ocs-upi-kvm/scripts;  ./deploy-ocs-ci.sh > deploy-ocs-ci.log "
