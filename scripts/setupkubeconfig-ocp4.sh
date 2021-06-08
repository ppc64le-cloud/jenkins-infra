#!/bin/bash -x
    echo "Capturing the System Information"
    if [ -d ${WORKSPACE}/deploy ];then
        cd ${WORKSPACE}/deploy
    else
        exit 1 
    fi
    # setup oc client
    if [ ${OPENSHIFT_CLIENT_TARBALL_AMD64} ]; then
        wget "${OPENSHIFT_CLIENT_TARBALL_AMD64}" -O - | tar -xz
        cp kubectl oc /usr/bin/
    fi
    # Capturing Terraform template
    if [ ${POWERVS} == "false" ] ; then
        BASTION_IP=$(make terraform:output TERRAFORM_DIR=.${TARGET} TERRAFORM_OUTPUT_VAR=bastion_ip )
        [ $? -ne 0 ] && exit 1
    else
        BASTION_IP=$(make $TARGET:output TERRAFORM_OUTPUT_VAR=bastion_public_ip | grep -Eo '[0-9]{1,3}(.[0-9]{1,3}){3}')
        [ $? -ne 0 ] && exit 1
    fi
    if [ ! -z "${BASTION_IP}" ]; then
        ssh -q -i id_rsa -o StrictHostKeyChecking=no root@${BASTION_IP} exit
        rc=$?
        if [ $? -eq 0 ] ; then
            rm -rf ~/.kube
            mkdir ~/.kube
            scp -i id_rsa -o StrictHostKeyChecking=no  root@${BASTION_IP}:/root/openstack-upi/auth/kubeconfig ~/.kube/config
            if [ ${POWERVS} == "false" ] ; then
                make terraform:output TERRAFORM_DIR=.${TARGET} TERRAFORM_OUTPUT_VAR=etc_hosts_entries >> /etc/hosts
            else
                make $TARGET:output TERRAFORM_OUTPUT_VAR=etc_hosts_entries >> /etc/hosts
            fi
        else
            echo 'Unable to access the cluster. You may delete the VMs manually'
        fi
    else
        echo 'Unable to access the cluster. You may delete the VMs manually'
    fi