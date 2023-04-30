#!/bin/bash
##
## This scripts runs ODF deplpy script from the ocs-upi-kvm in the remote bastion node
##
scp -r -i ${WORKSPACE}/deploy/id_rsa -o 'StrictHostKeyChecking=no' root@${BASTION_IP}:/root/openstack-upi/auth/ ${WORKSPACE}/
cp /usr/bin/oc ${WORKSPACE}/ocs-upi-kvm/src/ocs-ci/bin/
mkdir ${WORKSPACE}/bin; cp /usr/bin/oc ${WORKSPACE}/bin/;
. ${WORKSPACE}/env_vars.sh; cd ${WORKSPACE}/ocs-upi-kvm/scripts;  ./deploy-ocs-ci.sh > deploy-ocs-ci.log
