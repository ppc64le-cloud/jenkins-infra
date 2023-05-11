#!/bin/bash
##
## This scripts updates the env_vars.sh and ocs-ci-conf.yaml after ODF upgrade
##
pip3 install yq
. env_vars.sh; export OCS_VERSION=`echo ${UPGRADE_OCS_VERSION} | cut -d "." -f 1-2`; export OCS_CSV_CHANNEL=stable-$OCS_VERSION; yq -y -i ".DEPLOYMENT.ocs_csv_channel |= env.OCS_CSV_CHANNEL" /root/ocs-ci-conf.yaml; yq -y -i ".ENV_DATA.ocs_version |= env.OCS_VERSION" /root/ocs-ci-conf.yaml ;  sed -i "s|log_dir:.*$|log_dir: /root/logs-ocs-ci/"$OCS_VERSION"|g"  /root/ocs-ci-conf.yaml
cat /root/ocs-ci-conf.yaml
. env_vars.sh; CURRENT_OCS_VERSION=`echo ${UPGRADE_OCS_VERSION} | cut -d "." -f 1-2`; sed -i "s|export OCS_VERSION=.*$|export OCS_VERSION=${CURRENT_OCS_VERSION}|g" /root/env_vars.sh
cat /root/env_vars.sh