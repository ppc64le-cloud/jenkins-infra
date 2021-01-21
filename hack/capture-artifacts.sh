    #!/bin/bash -x
    echo "Capturing the System Information"
    if [ -d ${WORKSPACE}/deploy ];then
        cd ${WORKSPACE}/deploy
    else
        exit 1 
    fi
    # Capturing Terraform template
    if [ ! -f ${WORKSPACE}/deploy/.${TARGET}.tfvars ]; then
        echo "${WORKSPACE}/deploy/.${TARGET}.tfvars not found!"
        exit 1
    else
        cp ${WORKSPACE}/deploy/.${TARGET}.tfvars ${TARGET}.tfvars
        sed -i "s|ftp3_password.*$|ftp3_password  = ************|g" ${TARGET}.tfvars
        sed -i "s|repo_token.*$|repo_token  = ************|g" ${TARGET}.tfvars
        sed -i "s|icp_version.*=.*@|icp_version = \"user:************@|g" ${TARGET}.tfvars
        sed -i "s|docker_password.*$|docker_password: ************|g" ${TARGET}.tfvars
        sed -i "s|header.*X-JFrog-Art-Api.*$|header.*X-JFrog-Art-Api: ************|g" ${TARGET}.tfvars
        sed -i "s|password.*=.*$|password = ************|g" ${TARGET}.tfvars
        sed -i "s|rhel_subscription_password.*=.*$|rhel_subscription_password = ************|g" ${TARGET}.tfvars
        cp ${TARGET}.tfvars vars.tfvars
    fi
    if [ ${OCP_ENV} == true ];then install_dir="/root" ;else install_dir="/opt/ibm";fi
    MASTER_NODE=$(make terraform:output TERRAFORM_DIR=.${TARGET} TERRAFORM_OUTPUT_VAR=master-node || true)
    [ $? -ne 0 ] && exit 1;
    if [ ! -z "${MASTER_NODE}" ]; then
        ssh -q -i id_rsa -o StrictHostKeyChecking=no root@${MASTER_NODE} exit
        rc=$?
        if [ $? -eq 0 ] ; then
            scp -i id_rsa -o StrictHostKeyChecking=no ${WORKSPACE}/${PROJECTNAME}/hack/system_info.sh root@${MASTER_NODE}:
            ssh -i id_rsa -o StrictHostKeyChecking=no root@${MASTER_NODE} /root/system_info.sh > systeminfo.txt
            tar -czvf systeminfo.txt.tar.gz systeminfo.txt
            if [ "${SKIP_ICP_INSTALL}" != "true" ]; then
                if ssh -i id_rsa -o StrictHostKeyChecking=no root@${MASTER_NODE} [ -d ${install_dir}/cluster ] ;then
                    scp -i id_rsa -o StrictHostKeyChecking=no ${WORKSPACE}/${PROJECTNAME}/hack/health_check.sh root@${MASTER_NODE}:${install_dir}/cluster/
                    ssh -i id_rsa -o StrictHostKeyChecking=no root@${MASTER_NODE} rm -rf ${install_dir}/cluster/images
                    ssh -i id_rsa -o StrictHostKeyChecking=no root@${MASTER_NODE} ${install_dir}/cluster/health_check.sh ${OCP_ENV}
                    scp -i id_rsa -o StrictHostKeyChecking=no -r root@${MASTER_NODE}:${install_dir}/cluster .
                    sed -i "s|header: 'X-JFrog-Art-Api.*$|header: 'X-JFrog-Art-Api: ************|g" cluster/config.yaml
                    sed -i "s|docker_password.*$|docker_password: ************|g" cluster/config.yaml
                    sed -i "s|rhel_subscription_password.*$|rhel_subscription_password: ************|g" cluster/config.yaml
                    tar -czvf cluster.tar.gz cluster
                fi
            fi
        else
            echo 'Unable to access the cluster. You may delete the VMs manually'
        fi
    else
        echo 'Unable to access the cluster. You may delete the VMs manually'
    fi