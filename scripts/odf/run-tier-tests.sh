#!/bin/bash
##
## This scripts runs Tier test in the remote bastion node
##

ssh -o 'StrictHostKeyChecking=no' -o 'ServerAliveInterval=5' -o 'ServerAliveCountMax 1200'  -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "cd ocs-upi-kvm/scripts/; source /root/env_vars.sh; ./test-ocs-ci.sh --tier ${TIER_TEST} > tier${TIER_TEST}.log "
ssh -o 'StrictHostKeyChecking=no' -o 'ServerAliveInterval=5' -o 'ServerAliveCountMax 1200' -i ${WORKSPACE}/deploy/id_rsa root@${BASTION_IP} "sed -n '/short test summary info/, /Test result:/p' /root/ocs-upi-kvm/scripts/tier${TIER_TEST}.log" > ${WORKSPACE}/tier${TIER_TEST}-summary.txt
awk '/passed/||/failed/||/skipped/' ${WORKSPACE}/tier${TIER_TEST}-summary.txt | sed 's/= *//g' > ${WORKSPACE}/slacksummary.txt
