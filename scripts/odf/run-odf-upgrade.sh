#!/bin/bash
##
## This scripts runs ODF upgrade script from the ocs-upi-kvm in the remote bastion node
##
ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=5' -o 'ServerAliveCountMax 1200' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "source env_vars.sh; echo "Upgrading ODF to ${UPGRADE_OCS_VERSION}"; cd /root/ocs-upi-kvm/scripts; ./upgrade-ocs-ci.sh > upgrade-ocs-ci.log ; "
ssh -o 'StrictHostKeyChecking no' -o 'ServerAliveInterval=5' -o 'ServerAliveCountMax 1200' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "odf_csv=`oc get csv -n openshift-storage |grep odf-operator | awk {'print $1'}`; oc get csv $odf_csv -n openshift-storage -o yaml |grep full_version |awk {'print $2'} | tail -n 1" > ${WORKSPACE}/odf-full-build.txt
