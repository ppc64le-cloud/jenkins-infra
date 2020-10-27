###############################################################################
# Licensed Materials - Property of IBM Copyright IBM Corporation 2017. All Rights Reserved.
# U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP
# Schedule Contract with IBM Corp.
#
# Contributors:
#  IBM Corporation - initial API and implementation
###############################################################################

SHELL := /bin/bash
TOP := $(BUILD_DIR)
DATE ?= $(shell date +%Y%m%d)

# GITHUB_USER containing '@' char must be escaped with '%40'
GITHUB_USER := $(shell echo $(GITHUB_USER) | sed 's/@/%40/g')
GITHUB_TOKEN ?=

DOCKER_USER ?=
DOCKER_PASS ?=

ARTIFACTORY_URL ?= na.artifactory.swg-devops.com
ARTIFACTORY_USER ?=
ARTIFACTORY_TOKEN ?=
TERRAFORM_STATE_ARTIFACTORY_TOKEN ?= $(ARTIFACTORY_TOKEN)

ICP_RELEASE_BRANCH ?= master

TRAVIS_BUILD_NUMBER ?= $(shell date +%H%M%S)

IMAGE ?=
TAG ?= latest-$(DATE)-$(TRAVIS_BUILD_NUMBER)

CHART ?=
FROM_REPO ?=
TO_REPO ?=

CANARY_DEPLOY_DIR ?=.$(TARGET)

ifeq ($(CANARY_DEPLOY_DIR),.)
$(error CANARY_DEPLOY_DIR is not set - this is used to further define TERRAFORM_DIR and TERRAFORM_VARS_FILE)
endif

TERRAFORM_DIR ?=$(CANARY_DEPLOY_DIR)
TERRAFORM_VARS_FILE ?= $(CANARY_DEPLOY_DIR).tfvars

VERSION ?= latest
EDITION ?= ee
#Default to blank, not supported for CE version. Set to .(FIXPACK VERSION), i.e. .1906 for June 2019)
FIXPACK ?=

## -------- Fyre parameters --------

FYRE_VARS_DIR ?= "."
FYRE_DEPLOY_DIR ?=$(CANARY_DEPLOY_DIR)
FYRE_TERRAFORM_VARS_FILE ?= $(CANARY_DEPLOY_DIR).tfvars
FYRE_OVERRIDES_DIR ?= fyre_overrides

## -------- PowerVC CI Specifc parameters --------

AUTH_URL ?=
OS_TENANT_NAME ?= ibm-default
AVAILABILITY_ZONE ?= 
OS_IMAGE ?=
IMAGE_DISTRO ?= 
NUM_OF_MASTERS ?= 1
NUM_OF_MANAGERS ?= 0
NUM_OF_WORKERS ?= 0
NUM_OF_VA ?= 0
NUM_OF_PROXIES ?= 0
NUM_OF_GLUSTERS ?= 0
CREATE_STORAGE ?= false
SYSTEM_TUNING ?= disabled
ICP_CONFIG_FILE ?= 
OS_PRIVATE_NETWORK ?= icp_network
OS_NETWORK ?= icp_network
VA ?= disabled
ISTIO ?= disabled
GLUSTER_FS ?= disabled
MCM ?= disabled
PSN ?= disabled
NPDD ?= disabled
MINIO ?= disabled
KNATIVE ?= disabled
OFFLINE_IMAGE_LOCATION ?= 

MASTER_TEMPLATE ?= 
MANAGER_TEMPLATE ?= 
WORKER_TEMPLATE ?= 
VA_TEMPLATE ?= 
PROXY_TEMPLATE ?= 
GLUSTER_TEMPLATE ?=  


# ---------------------------------
## ----- default, init, keys -----
# ---------------------------------

.PHONY: default
default:: init;

.PHONY: init\:
init::
ifndef GITHUB_USER
	$(info GITHUB_USER not defined)
	exit -1
endif
	$(info Using GITHUB_USER=$(GITHUB_USER))
ifndef GITHUB_TOKEN
	$(info GITHUB_TOKEN not defined)
	exit -1
endif

-include $(shell curl -so .build-harness -H "Authorization: token $(GITHUB_TOKEN)" -H "Accept: application/vnd.github.v3.raw" "https://raw.github.ibm.com/ICP-DevOps/build-harness/master/templates/Makefile.build-harness"; echo .build-harness)

.PHONY: keys\:
## Generate ssh key pair
keys:
	@$(SELF) -s ssh:keys
	@echo -e "\nContents of id_rsa:"
	@cat $(SSH_KEY_FILE)
	@echo -e "\nContents of id_rsa.pub:"
	@cat $(SSH_KEY_FILE).pub

.PHONY: keys\:clean
## Delete ssh key pair
keys\:clean:
	@$(shell rm -f id_rsa id_rsa.pub)


# ---------------------------------
## ----- power-powervc -----
# ---------------------------------

.PHONY: deploy-power-powervc\:
## Create power build on PowerVC environment
deploy-power-powervc: %deploy-power-powervc:
	@$(shell sed -e "s|__AUTH_URL__|$(AUTH_URL)|g" -e "s|__OS_TENANT_NAME__|$(OS_TENANT_NAME)|g" \
        -e "s|__AVAILABILITY_ZONE__|$(AVAILABILITY_ZONE)|g" -e "s|__OS_IMAGE__|$(OS_IMAGE)|g" -e "s|__INSTANCE_NAME__|$(INSTANCE_NAME)|g" \
        -e "s|__IMAGE_DISTRO__|$(IMAGE_DISTRO)|g" -e "s|__CREATE_STORAGE__|$(CREATE_STORAGE)|g" -e "s|__SYSTEM_TUNING__|$(SYSTEM_TUNING)|g" \
		-e "s|__SYSTEM_TUNING__|$(SYSTEM_TUNING)|g" -e "s|__NUM_OF_MASTERS__|$(NUM_OF_MASTERS)|g" \
        -e "s|__NUM_OF_MANAGERS__|$(NUM_OF_MANAGERS)|g" -e "s|__NUM_OF_WORKERS__|$(NUM_OF_WORKERS)|g" \
        -e "s|__NUM_OF_VA__|$(NUM_OF_VA)|g" -e "s|__NUM_OF_PROXIES__|$(NUM_OF_PROXIES)|g" -e "s|__NUM_OF_GLUSTERS__|$(NUM_OF_GLUSTERS)|g" \
		-e "s|__ICP_CONFIG_FILE__|$(ICP_CONFIG_FILE)|g" -e "s|__OS_PRIVATE_NETWORK__|$(OS_PRIVATE_NETWORK)|g" -e "s|__OS_NETWORK__|$(OS_NETWORK)|g"  \
		-e "s|__VA__|$(VA)|g" -e "s|__ISTIO__|$(ISTIO)|g" -e "s|__GLUSTER_FS__|$(GLUSTER_FS)|g" \
        -e "s|__MCM__|$(MCM)|g" -e "s|__PSN__|$(PSN)|g" -e "s|__NPDD__|$(NPDD)|g" -e "s|__REPO__|$(DEPLOY_REPO)|g" \
		-e "s|__MINIO__|$(MINIO)|g" -e "s|__KNATIVE__|$(KNATIVE)|g" -e "s|__VERSION__|$(ICP_VERSION)|g" \
        -e "s|__FIXPACK__|$(ICP_FIXPACK)|g" -e "s|__EDITION__|$(ICP_EDITION)|g" -e "s|__REPO__|$(DEPLOY_REPO)|g" \
        -e "s|__OS_USERNAME__|$(OS_USERNAME)|g" -e "s|__OS_PASSWORD__|$(OS_PASSWORD)|g"  \
        -e "s|__ARTIFACTORY_USER__|$(ARTIFACTORY_USER)|g" -e "s|__ARTIFACTORY_API_KEY__|$(ARTIFACTORY_TOKEN)|g" \
		-e "s|__MASTER_TEMPLATE__|$(MASTER_TEMPLATE)|g" -e "s|__MANAGER_TEMPLATE__|$(MANAGER_TEMPLATE)|g" \
        -e "s|__WORKER_TEMPLATE__|$(WORKER_TEMPLATE)|g" -e "s|__VA_TEMPLATE__|$(VA_TEMPLATE)|g" \
        -e "s|__PROXY_TEMPLATE__|$(PROXY_TEMPLATE)|g" -e "s|__GLUSTER_TEMPLATE__|$(GLUSTER_TEMPLATE)|g" -e "s|__HIGH_AVAILABILITY__|$(HIGH_AVAILABILITY)|g" templates/$(TERRAFORM_VARS_FILE).template > $(TERRAFORM_VARS_FILE))
	@echo -e        "\nAUTH_URL" = $(AUTH_URL)\
        "\nOS_TENANT_NAME" = $(OS_TENANT_NAME)\
        "\nAVAILABILITY_ZONE" = $(AVAILABILITY_ZONE)\
        "\nOS_IMAGE" = $(OS_IMAGE)\
        "\nIMAGE_DISTRO" = $(IMAGE_DISTRO)\
        "\nNUM_OF_MASTERS" = $(NUM_OF_MASTERS)\
        "\nNUM_OF_MANAGERS" = $(NUM_OF_MANAGERS)\
        "\nNUM_OF_WORKERS" = $(NUM_OF_WORKERS)\
        "\nNUM_OF_VA" = $(NUM_OF_VA)\
        "\nNUM_OF_PROXIES" = $(NUM_OF_PROXIES)\
        "\nNUM_OF_GLUSTERS" = $(NUM_OF_GLUSTERS)\
        "\nCREATE_STORAGE" = $(CREATE_STORAGE)\
        "\nSYSTEM_TUNING" = $(SYSTEM_TUNING)\
        "\nICP_CONFIG_FILE" = $(ICP_CONFIG_FILE)\
        "\nOS_PRIVATE_NETWORK" = $(OS_PRIVATE_NETWORK)\
        "\nOS_NETWORK" = $(OS_NETWORK)\
        "\nVA" = $(VA)\
        "\nISTIO" = $(ISTIO)\
        "\nGLUSTER_FS" = $(GLUSTER_FS)\
        "\nMCM" = $(MCM)\
        "\nPSN" = $(PSN)\
        "\nNPDD" = $(NPDD)\
        "\nMINIO" = $(MINIO)\
        "\nKNATIVE" = $(KNATIVE)\
        "\nMASTER_TEMPLATE" = $(MASTER_TEMPLATE)\
        "\nMANAGER_TEMPLATE" = $(MANAGER_TEMPLATE)\
        "\nWORKER_TEMPLATE" = $(WORKER_TEMPLATE)\
        "\nVA_TEMPLATE" = $(VA_TEMPLATE)\
        "\nPROXY_TEMPLATE" = $(PROXY_TEMPLATE)\
        "\nGLUSTER_TEMPLATE" = $(GLUSTER_TEMPLATE)\
        "\nHIGH_AVAILABILITY" = $(HIGH_AVAILABILITY)
	@$(SELF) -s deploy:powervc POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

.PHONY: deploy-power-powervc\:config
## Configure kubectl for access to PowerVC build
deploy-power-powervc\:config:
	#@$(SELF) -s kubectl:install
	@$(SELF) -s kubectl:config K8S_CLUSTER_NAME=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=cluster-name) \
								K8S_CLUSTER_MASTER_IP=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=master-node) \
								K8S_CLUSTER_SSH_USER=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=vm-username)
	@$(SELF) -s helm:config K8S_CLUSTER_NAME=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=cluster-name) \
								K8S_CLUSTER_MASTER_IP=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=master-node) \
								K8S_CLUSTER_SSH_USER=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=vm-username)
	@$(SELF) -s helm:init
	@echo INFO: You can now access your cluster with "kubectl config use-context $(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=cluster-name)"
	@echo INFO: You can access your cluster web console at $(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=web-console)

.PHONY: deploy-power-powervc\:clean
## Clean up power-powervc build artifacts
deploy-power-powervc\:clean:
	@$(SELF) -s deploy:powervc:clean POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

# ---------------------------------
## ----- openshift-powervc -----
# ---------------------------------

.PHONY: deploy-openshift-powervc\:
## Create power build on powervc tenant
deploy-openshift-powervc: %deploy-openshift-powervc:
	@$(shell sed -e "s|__AUTH_URL__|$(AUTH_URL)|g" -e "s|__OS_TENANT_NAME__|$(OS_TENANT_NAME)|g" \
        -e "s|__AVAILABILITY_ZONE__|$(AVAILABILITY_ZONE)|g" -e "s|__SUBSCRIPTION_POOL_LIST__|$(SUBSCRIPTION_POOL_LIST)|g" \
        -e "s|__OS_IMAGE__|$(OS_IMAGE)|g" -e "s|__OS_NETWORK__|$(OS_NETWORK)|g" \
		-e "s|__ROOK_DISK_SIZE__|$(ROOK_DISK_SIZE)|g" -e "s|__INSTANCE_NAME__|$(INSTANCE_NAME)|g" \
		-e "s|__NUM_OF_WORKERS__|$(NUM_OF_WORKERS)|g" -e "s|__WORKER_TEMPLATE__|$(WORKER_TEMPLATE)|g"  \
		-e "s|__NUM_OF_MASTERS__|$(NUM_OF_MASTERS)|g" -e "s|__MASTER_TEMPLATE__|$(MASTER_TEMPLATE)|g" \
		-e "s|__NUM_OF_MANAGERS__|$(NUM_OF_MANAGERS)|g" -e "s|__MANAGER_TEMPLATE__|$(MANAGER_TEMPLATE)|g" \
		-e "s|__NUM_OF_PROXIES__|$(NUM_OF_PROXIES)|g" -e "s|__PROXY_TEMPLATE__|$(PROXY_TEMPLATE)|g" \
		-e "s|__NUM_OF_VA__|$(NUM_OF_VA)|g" -e "s|__VA_TEMPLATE__|$(VA_TEMPLATE)|g" \
        -e "s|__ENABLED_MANAGEMENT_SERVICES__|$(ENABLED_MANAGEMENT_SERVICES)|g" -e "s|__ICP_INSTALLER_TAG__|$(ICP_INSTALLER_TAG)|g" \
		-e "s|__ICP_CONFIG_FILE__|$(ICP_CONFIG_FILE)|g"  -e "s|__STORAGE__|$(STORAGE)|g" \
		-e "s|__SKIP_ICP_INSTALL__|$(SKIP_ICP_INSTALL)|g" -e "s|__REDHAT_RELEASE__|$(REDHAT_RELEASE)|g" \
        -e "s|__REDHAT_USERNAME__|$(REDHAT_USERNAME)|g" -e "s|__REDHAT_PASSWORD__|$(REDHAT_PASSWORD)|g"  \
        -e "s|__OS_USERNAME__|$(OS_USERNAME)|g" -e "s|__OS_PASSWORD__|$(OS_PASSWORD)|g"  \
		-e "s|__FVD_USERNAME__|$(FVD_USERNAME)|g" -e "s|__FVD_PASSWORD__|$(FVD_PASSWORD)|g"  \
        -e "s|__ARTIFACTORY_USER__|$(ARTIFACTORY_USER)|g" -e "s|__ARTIFACTORY_TOKEN__|$(ARTIFACTORY_TOKEN)|g" \
        -e "s|__REPO__|$(DEPLOY_REPO)|g" templates/$(TERRAFORM_VARS_FILE).template > $(TERRAFORM_VARS_FILE))

	@$(shell ../hack/prepare_template.sh  "$${ICP_ADDITIONAL_CONFIG}" "${TERRAFORM_VARS_FILE}" "${SKIP_ICP_INSTALL}")
	@$(SELF) -s deploy:openshift:powervc OPENSHIFT_POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

.PHONY: deploy-openshift-powervc\:config
## Configure kubectl for access to power-openstack build
deploy-openshift-powervc\:config:
	@$(SELF) -s cloudctl:config K8S_CLUSTER_NAME=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=cluster-name) \
		K8S_CLUSTER_MASTER_IP=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=master-node) \
		K8S_CLUSTER_MASTER_PORT=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=icp-port) \
		K8S_CLUSTER_USER=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=admin-user) \
		K8S_CLUSTER_PASSWORD=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=admin-password) \
		OPENSHIFT_ICP_CONSOLE=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=icp-web-console)
	@$(SELF) -s helm:init
	@echo INFO: You can now access your cluster with "kubectl config use-context $(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=cluster-name)"
	@echo INFO: You can access your cluster web console at $(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=web-console)

.PHONY: deploy-openshift-powervc\:clean
## Clean up power-openstack build artifacts
deploy-openshift-powervc\:clean:
	@$(SELF) -s deploy:openshift:powervc:clean OPENSHIFT_POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)


# ---------------------------------
## ----- openshift4-powervc -----
# ---------------------------------

.PHONY: deploy-openshift4-powervc\:
## Create power build on powervc tenant
deploy-openshift4-powervc: %deploy-openshift4-powervc:
	@$(shell sed -e "s|__AUTH_URL__|$(AUTH_URL)|g" -e "s|__OS_TENANT_NAME__|$(OS_TENANT_NAME)|g" \
        -e "s|__AVAILABILITY_ZONE__|$(AVAILABILITY_ZONE)|g" -e "s|__INSTANCE_NAME__|$(INSTANCE_NAME)|g" \
        -e "s|__OS_IMAGE__|$(OS_IMAGE)|g" -e "s|__OS_NETWORK__|$(OS_NETWORK)|g" \
		-e "s|__NUM_OF_WORKERS__|$(NUM_OF_WORKERS)|g" -e "s|__WORKER_TEMPLATE__|$(WORKER_TEMPLATE)|g"  \
		-e "s|__NUM_OF_MASTERS__|$(NUM_OF_MASTERS)|g" -e "s|__MASTER_TEMPLATE__|$(MASTER_TEMPLATE)|g" \
		-e "s|__BOOTSTRAP_TEMPLATE__|$(BOOTSTRAP_TEMPLATE)|g" -e "s|__BASTION_TEMPLATE__|$(BASTION_TEMPLATE)|g" \
		-e "s|__REDHAT_RELEASE__|$(REDHAT_RELEASE)|g" -e "s|__ENABLE_E2E_TEST__|$(ENABLE_E2E_TEST)|g" \
        -e "s|__REDHAT_USERNAME__|$(REDHAT_USERNAME)|g" -e "s|__REDHAT_PASSWORD__|$(REDHAT_PASSWORD)|g"  \
        -e "s|__OS_USERNAME__|$(OS_USERNAME)|g" -e "s|__OS_PASSWORD__|$(OS_PASSWORD)|g"  \
        -e "s|__ARTIFACTORY_USER__|$(ARTIFACTORY_USER)|g" -e "s|__ARTIFACTORY_TOKEN__|$(ARTIFACTORY_TOKEN)|g" \
		-e "s|__BASTION_IMAGE_ID__|$(BASTION_IMAGE_ID)|g" -e "s|__RHCOS_IMAGE_ID__|$(RHCOS_IMAGE_ID)|g" \
		-e "s|__RHEL_USERNAME__|$(RHEL_USERNAME)|g" -e "s|__OPENSHIFT_INSTALL_TARBALL__|$(OPENSHIFT_INSTALL_TARBALL)|g" \
		-e "s|__OPENSHIFT_IMAGE__|$(OPENSHIFT_IMAGE)|g" -e "s|__PULL_SECRET_FILE__|$(PULL_SECRET_FILE)|g" \
		-e "s|__OPENSHIFT_CLIENT_TARBALL__|$(OPENSHIFT_CLIENT_TARBALL)|g" -e "s|__OPENSHIFT_UPGRADE_IMAGE__|$(OPENSHIFT_UPGRADE_IMAGE)|g" \
		-e "s|__CLUSTER_DOMAIN__|$(CLUSTER_DOMAIN)|g" -e "s|__CLUSTER_ID__|$(CLUSTER_ID)|g" \
		-e "s|__E2E_GIT__|$(E2E_GIT)|g" -e "s|__E2E_BRANCH__|$(E2E_BRANCH)|g"  -e "s|__E2E_EXCLUDE_LIST__|$(E2E_EXCLUDE_LIST)|g"\
		-e "s|__CS_INSTALL__|$(CS_INSTALL)|g" -e "s|__ENABLE_E2E_UPGRADE__|$(ENABLE_E2E_UPGRADE)|g" \
		-e "s|__GITHUB_USER__|$(GITHUB_USER)|g" -e "s|__GITHUB_TOKEN__|$(GITHUB_TOKEN)|g" \
		-e "s|__MOUNT_ETCD_RAMDISK__|$(MOUNT_ETCD_RAMDISK)|g" -e "s|__SCALE_NUM_OF_DEPLOYMENTS__|$(SCALE_NUM_OF_DEPLOYMENTS)|g"\
		-e "s|__ENABLE_SCALE_TEST__|$(ENABLE_SCALE_TEST)|g" -e "s|__GOLANG_TARBALL__|$(GOLANG_TARBALL)|g"\
        -e "s|__UPGRADE_VERSION__|$(UPGRADE_VERSION)|g" -e "s|__UPGRADE_PAUSE_TIME__|$(UPGRADE_PAUSE_TIME)|g"\
        -e "s|__UPGRADE_CHANNEL__|$(UPGRADE_CHANNEL)|g" \
        -e "s|__UPGRADE_DELAY_TIME__|$(UPGRADE_DELAY_TIME)|g" -e "s|__CHRONY_CONFIG__|$(CHRONY_CONFIG)|g"\
        -e "s|__REPO__|$(DEPLOY_REPO)|g" templates/$(TERRAFORM_VARS_FILE).template > $(TERRAFORM_VARS_FILE))

##@$(shell ../hack/prepare_template.sh  "$${ICP_ADDITIONAL_CONFIG}" "${TERRAFORM_VARS_FILE}" "${SKIP_ICP_INSTALL}")
	@$(SELF) -s deploy:openshift4:powervc OPENSHIFT_POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

# .PHONY: deploy-openshift4-powervc\:config
# ## Configure kubectl for access to power-openstack build
# deploy-openshift4-powervc\:config:
# 	@$(SELF) -s cloudctl:config K8S_CLUSTER_NAME=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=cluster-id) \
# 		K8S_CLUSTER_MASTER_IP=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=bastion_ip) \
# 		K8S_CLUSTER_MASTER_PORT=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=icp-port) \
# 		K8S_CLUSTER_USER=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=admin-user) \
# 		K8S_CLUSTER_PASSWORD=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=admin-password) \
# 		OPENSHIFT_ICP_CONSOLE=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=icp-web-console)
# 	@$(SELF) -s helm:init
# 	@echo INFO: You can now access your cluster with "kubectl config use-context $(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=cluster-name)"
# 	@echo INFO: You can access your cluster web console at $(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=web-console)

.PHONY: deploy-openshift4-powervc\:redeploy
## Re deploy on powervc tenant
deploy-openshift4-powervc\:redeploy:
	@$(SELF) -s deploy:openshift4:powervc:redeploy OPENSHIFT_POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)


.PHONY: deploy-openshift4-powervc\:clean
## Clean up power-openstack build artifacts
deploy-openshift4-powervc\:clean:
	@$(SELF) -s deploy:openshift:powervc:clean OPENSHIFT_POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)


# ---------------------------------
## ----- openshift4-powervs-script -----
# ---------------------------------

.PHONY: deploy-openshift4-powervs-script\:
## Create power build on powervs tenant
deploy-openshift4-powervs-script: %deploy-openshift4-powervs-script:
	@$(shell sed -e "s|__IBMCLOUD_API_KEY__|$(IBMCLOUD_API_KEY)|g" -e "s|__IBMCLOUD_API_KEY__|$(IBMCLOUD_API_KEY)|g" \
        -e "s|__IBM_CLOUD_REGION__|$(IBM_CLOUD_REGION)|g" -e "s|__IBM_CLOUD_ZONE__|$(IBM_CLOUD_ZONE)|g" \
		-e "s|__BASTION_MEMORY__|$(BASTION_MEMORY)|g" -e "s|__BASTION_PROCESSORS__|$(BASTION_PROCESSORS)|g" \
		-e "s|__BOOTSTRAP_MEMORY__|$(BOOTSTRAP_MEMORY)|g" -e "s|__BOOTSTRAP_PROCESSORS__|$(BOOTSTRAP_PROCESSORS)|g" \
		-e "s|__MASTER_MEMORY__|$(MASTER_MEMORY)|g" -e "s|__MASTER_PROCESSORS__|$(MASTER_PROCESSORS)|g" -e "s|__NUM_OF_MASTERS__|$(NUM_OF_MASTERS)|g" \
		-e "s|__WORKER_MEMORY__|$(WORKER_MEMORY)|g" -e "s|__WORKER_PROCESSORS__|$(WORKER_PROCESSORS)|g" -e "s|__NUM_OF_WORKERS__|$(NUM_OF_WORKERS)|g" \
		-e "s|__BASTION_IMAGE_NAME__|$(BASTION_IMAGE_NAME)|g" -e "s|__RHCOS_IMAGE_NAME__|$(RHCOS_IMAGE_NAME)|g" -e "s|__SYSTEM_TYPE__|$(SYSTEM_TYPE)|g" \
		-e "s|__NETWORK_NAME__|$(NETWORK_NAME)|g" -e "s|__RHEL_USERNAME__|$(RHEL_USERNAME)|g" -e "s|__REDHAT_USERNAME__|$(REDHAT_USERNAME)|g" \
		-e "s|__REDHAT_PASSWORD__|$(REDHAT_PASSWORD)|g" -e "s|__OPENSHIFT_INSTALL_TARBALL__|$(OPENSHIFT_INSTALL_TARBALL)|g" \
		-e "s|__OPENSHIFT_CLIENT_TARBALL__|$(OPENSHIFT_CLIENT_TARBALL)|g" -e "s|__SETUP_SQUID_PROXY__|$(SETUP_SQUID_PROXY)|g" \
		-e "s|__OPENSHIFT_IMAGE__|$(OPENSHIFT_IMAGE)|g" -e "s|__CLUSTER_DOMAIN__|$(CLUSTER_DOMAIN)|g" -e "s|__INSTANCE_NAME__|$(INSTANCE_NAME)|g" \
		-e "s|__ENABLE_LOCAL_REGISTRY__|$(ENABLE_LOCAL_REGISTRY)|g" -e "s|__LOCAL_REGISTRY_IMAGE__|$(LOCAL_REGISTRY_IMAGE)|g" \
		-e "s|__OCP_RELEASE_TAG__|$(OCP_RELEASE_TAG)|g" -e "s|__HELPERNODE_REPO__|$(HELPERNODE_REPO)|g" \
		-e "s|__HELPERNODE_TAG__|$(HELPERNODE_TAG)|g" -e "s|__INSTALL_PLAYBOOK_REPO__|$(UPGRADE_PAUSE_TIME)|g" \
		-e "s|__INSTALL_PLAYBOOT_TAG__|$(INSTALL_PLAYBOOT_TAG)|g" -e "s|__UPGRADE_IMAGE__|$(UPGRADE_IMAGE)|g" \
		-e "s|__UPGRADE_PAUSE_TIME__|$(UPGRADE_PAUSE_TIME)|g" -e "s|__UPGRADE_DELAY_TIME__|$(UPGRADE_DELAY_TIME)|g" \
        -e "s|__RHEL_SMT__|$(RHEL_SMT)|g" \
        -e "s|__SERVICE_INSTANCE_ID__|$(SERVICE_INSTANCE_ID)|g" templates/$(TERRAFORM_VARS_FILE).template > $(TERRAFORM_VARS_FILE))

##@$(shell ../hack/prepare_template.sh  "$${ICP_ADDITIONAL_CONFIG}" "${TERRAFORM_VARS_FILE}" "${SKIP_ICP_INSTALL}")
	@$(SELF) -s deploy:openshift4:powervs:script OPENSHIFT_POWERVS_SCRIPT_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVS_SCRIPT_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

.PHONY: deploy-openshift4-powervs-script\:output
## Clean up power-openstack build artifacts
deploy-openshift4-powervs-script\:output:
	@$(SELF) -s deploy:openshift4:powervs:script:output OPENSHIFT_POWERVS_SCRIPT_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVS_SCRIPT_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE) TERRAFORM_OUTPUT_VAR=$(TERRAFORM_OUTPUT_VAR)



.PHONY: deploy-openshift4-powervs-script\:clean
## Clean up power-openstack build artifacts
deploy-openshift4-powervs-script\:clean:
	@$(SELF) -s deploy:openshift4:powervs:script:clean OPENSHIFT_POWERVS_SCRIPT_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVS_SCRIPT_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)



# ---------------------------------
## ----- openshift4-powervs -----
# ---------------------------------

.PHONY: deploy-openshift4-powervs\:
## Create power build on powervs tenant
deploy-openshift4-powervs: %deploy-openshift4-powervs:
	@$(shell sed -e "s|__IBMCLOUD_API_KEY__|$(IBMCLOUD_API_KEY)|g" -e "s|__IBMCLOUD_API_KEY__|$(IBMCLOUD_API_KEY)|g" \
        -e "s|__IBM_CLOUD_REGION__|$(IBM_CLOUD_REGION)|g" -e "s|__IBM_CLOUD_ZONE__|$(IBM_CLOUD_ZONE)|g" \
		-e "s|__BASTION_MEMORY__|$(BASTION_MEMORY)|g" -e "s|__BASTION_PROCESSORS__|$(BASTION_PROCESSORS)|g" \
		-e "s|__BOOTSTRAP_MEMORY__|$(BOOTSTRAP_MEMORY)|g" -e "s|__BOOTSTRAP_PROCESSORS__|$(BOOTSTRAP_PROCESSORS)|g" \
		-e "s|__MASTER_MEMORY__|$(MASTER_MEMORY)|g" -e "s|__MASTER_PROCESSORS__|$(MASTER_PROCESSORS)|g" -e "s|__NUM_OF_MASTERS__|$(NUM_OF_MASTERS)|g" \
		-e "s|__WORKER_MEMORY__|$(WORKER_MEMORY)|g" -e "s|__WORKER_PROCESSORS__|$(WORKER_PROCESSORS)|g" -e "s|__NUM_OF_WORKERS__|$(NUM_OF_WORKERS)|g" \
		-e "s|__BASTION_IMAGE_NAME__|$(BASTION_IMAGE_NAME)|g" -e "s|__RHCOS_IMAGE_NAME__|$(RHCOS_IMAGE_NAME)|g" -e "s|__SYSTEM_TYPE__|$(SYSTEM_TYPE)|g" \
		-e "s|__NETWORK_NAME__|$(NETWORK_NAME)|g" -e "s|__RHEL_USERNAME__|$(RHEL_USERNAME)|g" -e "s|__REDHAT_USERNAME__|$(REDHAT_USERNAME)|g" \
		-e "s|__REDHAT_PASSWORD__|$(REDHAT_PASSWORD)|g" -e "s|__OPENSHIFT_INSTALL_TARBALL__|$(OPENSHIFT_INSTALL_TARBALL)|g" \
		-e "s|__OPENSHIFT_CLIENT_TARBALL__|$(OPENSHIFT_CLIENT_TARBALL)|g" -e "s|__SETUP_SQUID_PROXY__|$(SETUP_SQUID_PROXY)|g" \
		-e "s|__OPENSHIFT_IMAGE__|$(OPENSHIFT_IMAGE)|g" -e "s|__CLUSTER_DOMAIN__|$(CLUSTER_DOMAIN)|g" -e "s|__INSTANCE_NAME__|$(INSTANCE_NAME)|g" \
		-e "s|__ENABLE_LOCAL_REGISTRY__|$(ENABLE_LOCAL_REGISTRY)|g" -e "s|__LOCAL_REGISTRY_IMAGE__|$(LOCAL_REGISTRY_IMAGE)|g" \
		-e "s|__OCP_RELEASE_TAG__|$(OCP_RELEASE_TAG)|g" -e "s|__HELPERNODE_REPO__|$(HELPERNODE_REPO)|g" \
		-e "s|__HELPERNODE_TAG__|$(HELPERNODE_TAG)|g" -e "s|__INSTALL_PLAYBOOK_REPO__|$(UPGRADE_PAUSE_TIME)|g" \
		-e "s|__INSTALL_PLAYBOOT_TAG__|$(INSTALL_PLAYBOOT_TAG)|g" -e "s|__UPGRADE_IMAGE__|$(UPGRADE_IMAGE)|g" \
		-e "s|__UPGRADE_PAUSE_TIME__|$(UPGRADE_PAUSE_TIME)|g" -e "s|__UPGRADE_DELAY_TIME__|$(UPGRADE_DELAY_TIME)|g" \
        -e "s|__RHEL_SMT__|$(RHEL_SMT)|g" \
        -e "s|__SERVICE_INSTANCE_ID__|$(SERVICE_INSTANCE_ID)|g" templates/$(TERRAFORM_VARS_FILE).template > $(TERRAFORM_VARS_FILE))

##@$(shell ../hack/prepare_template.sh  "$${ICP_ADDITIONAL_CONFIG}" "${TERRAFORM_VARS_FILE}" "${SKIP_ICP_INSTALL}")
	@$(SELF) -s deploy:openshift4:powervs OPENSHIFT_POWERVS_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVS_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

.PHONY: deploy-openshift4-powervs\:redeploy
## Re deploy on powervs tenant
deploy-openshift4-powervs\:redeploy:
	@$(SELF) -s deploy:openshift4:powervs:redeploy OPENSHIFT_POWERVS_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVS_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

.PHONY: deploy-openshift4-powervs\:clean
## Clean up power-openstack build artifacts
deploy-openshift4-powervs\:clean:
	@$(SELF) -s deploy:openshift4:powervs:clean OPENSHIFT_POWERVS_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVS_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

# ---------------------------------
## ----- single-instance-openshift4-powervs -----
# ---------------------------------

.PHONY: deploy-openshift4-powervs-single-instance\:
## Create power build on powervs tenant
deploy-openshift4-powervs-single-instance: %deploy-openshift4-powervs-single-instance:
	@$(shell sed -e "s|__IBMCLOUD_API_KEY__|$(IBMCLOUD_API_KEY)|g" -e "s|__IBMCLOUD_API_KEY__|$(IBMCLOUD_API_KEY)|g" \
        -e "s|__IBM_CLOUD_REGION__|$(IBM_CLOUD_REGION)|g" -e "s|__IBM_CLOUD_ZONE__|$(IBM_CLOUD_ZONE)|g" \
		-e "s|__BASTION_MEMORY__|$(BASTION_MEMORY)|g" -e "s|__BASTION_PROCESSORS__|$(BASTION_PROCESSORS)|g" \
		-e "s|__BASTION_IMAGE_NAME__|$(BASTION_IMAGE_NAME)|g" -e "s|__RHCOS_IMAGE_NAME__|$(RHCOS_IMAGE_NAME)|g" -e "s|__SYSTEM_TYPE__|$(SYSTEM_TYPE)|g" \
		-e "s|__NETWORK_NAME__|$(NETWORK_NAME)|g" -e "s|__RHEL_USERNAME__|$(RHEL_USERNAME)|g" -e "s|__REDHAT_USERNAME__|$(REDHAT_USERNAME)|g" \
		-e "s|__REDHAT_PASSWORD__|$(REDHAT_PASSWORD)|g" -e "s|__SETUP_SQUID_PROXY__|$(SETUP_SQUID_PROXY)|g" \
		-e "s|__OPENSHIFT_IMAGE__|$(OPENSHIFT_IMAGE)|g" -e "s|__CLUSTER_DOMAIN__|$(CLUSTER_DOMAIN)|g" -e "s|__INSTANCE_NAME__|$(INSTANCE_NAME)|g" \
		-e "s|__OCP_RELEASE_TAG__|$(OCP_RELEASE_TAG)|g" -e "s|__RHEL_SMT__|$(RHEL_SMT)|g" \
        -e "s|__SERVICE_INSTANCE_ID__|$(SERVICE_INSTANCE_ID)|g" templates/$(TERRAFORM_VARS_FILE).template > $(TERRAFORM_VARS_FILE))
	@$(SELF) -s deploy:openshift4:powervs:single:instance OPENSHIFT_POWERVS_SINGLE_INSTANCE_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVS_SINGLE_INSTANCE_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

.PHONY: deploy-openshift4-powervs-single-instance\:redeploy
## Re deploy on powervs tenant
deploy-openshift4-powervs-single-instance\:redeploy:
	@$(SELF) -s deploy:openshift4:powervs:single:instance:redeploy OPENSHIFT_POWERVS_SINGLE_INSTANCE_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVS_SINGLE_INSTANCE_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

.PHONY: deploy-openshift4-powervs-single-instance\:clean
## Clean up power-openstack build artifacts
deploy-openshift4-powervs-single-instance\:clean:
	@$(SELF) -s deploy:openshift4:powervs:single:instance:clean OPENSHIFT_POWERVS_SINGLE_INSTANCE_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVS_SINGLE_INSTANCE_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)


.PHONY: deploy-openshift-powervc-offline\:
## Create power build on powervc tenant
deploy-openshift-powervc-offline: %deploy-openshift-powervc-offline:
	@$(shell sed -e "s|__AUTH_URL__|$(AUTH_URL)|g" -e "s|__OS_TENANT_NAME__|$(OS_TENANT_NAME)|g" \
        -e "s|__AVAILABILITY_ZONE__|$(AVAILABILITY_ZONE)|g" -e "s|__INCEPTION_IMAGE__|$(INCEPTION_IMAGE)|g" \
        -e "s|__OS_IMAGE__|$(OS_IMAGE)|g" -e "s|__OS_NETWORK__|$(OS_NETWORK)|g" -e "s|__REDHAT_RELEASE__|$(REDHAT_RELEASE)|g" \
		-e "s|__ROOK_DISK_SIZE__|$(ROOK_DISK_SIZE)|g" -e "s|__OFFLINE_TARBALL_URL__|$(OFFLINE_TARBALL_URL)|g" \
		-e "s|__NUM_OF_WORKERS__|$(NUM_OF_WORKERS)|g" -e "s|__WORKER_TEMPLATE__|$(WORKER_TEMPLATE)|g"  \
		-e "s|__NUM_OF_MASTERS__|$(NUM_OF_MASTERS)|g" -e "s|__MASTER_TEMPLATE__|$(MASTER_TEMPLATE)|g" \
		-e "s|__NUM_OF_MANAGERS__|$(NUM_OF_MANAGERS)|g" -e "s|__MANAGER_TEMPLATE__|$(MANAGER_TEMPLATE)|g" \
		-e "s|__NUM_OF_PROXIES__|$(NUM_OF_PROXIES)|g" -e "s|__PROXY_TEMPLATE__|$(PROXY_TEMPLATE)|g" \
		-e "s|__NUM_OF_VA__|$(NUM_OF_VA)|g" -e "s|__VA_TEMPLATE__|$(VA_TEMPLATE)|g" -e "s|__SUBSCRIPTION_POOL_LIST__|$(SUBSCRIPTION_POOL_LIST)|g" \
        -e "s|__ENABLED_MANAGEMENT_SERVICES__|$(ENABLED_MANAGEMENT_SERVICES)|g" -e "s|__ICP_INSTALLER_TAG__|$(ICP_INSTALLER_TAG)|g" \
		-e "s|__ICP_CONFIG_FILE__|$(ICP_CONFIG_FILE)|g"  -e "s|__STORAGE__|$(STORAGE)|g" \
		-e "s|__SKIP_ICP_INSTALL__|$(SKIP_ICP_INSTALL)|g" -e "s|__INSTANCE_NAME__|$(INSTANCE_NAME)|g" \
        -e "s|__REDHAT_USERNAME__|$(REDHAT_USERNAME)|g" -e "s|__REDHAT_PASSWORD__|$(REDHAT_PASSWORD)|g"  \
        -e "s|__OS_USERNAME__|$(OS_USERNAME)|g" -e "s|__OS_PASSWORD__|$(OS_PASSWORD)|g"  \
		-e "s|__FVD_USERNAME__|$(FVD_USERNAME)|g" -e "s|__FVD_PASSWORD__|$(FVD_PASSWORD)|g"  \
        -e "s|__ARTIFACTORY_USER__|$(ARTIFACTORY_USER)|g" -e "s|__ARTIFACTORY_TOKEN__|$(ARTIFACTORY_TOKEN)|g" \
        -e "s|__REPO__|$(DEPLOY_REPO)|g" templates/$(TERRAFORM_VARS_FILE).template > $(TERRAFORM_VARS_FILE))

	@$(shell ../hack/prepare_template.sh  "$${ICP_ADDITIONAL_CONFIG}" "${TERRAFORM_VARS_FILE}" "${SKIP_ICP_INSTALL}")
	@$(SELF) -s deploy:openshift:powervc OPENSHIFT_POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

.PHONY: deploy-openshift-powervc-offline\:config
## Configure kubectl for access to power-openstack build
deploy-openshift-powervc-offline\:config:
	@$(SELF) -s cloudctl:config K8S_CLUSTER_NAME=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=cluster-name) \
		K8S_CLUSTER_MASTER_IP=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=master-node) \
		K8S_CLUSTER_MASTER_PORT=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=icp-port) \
		K8S_CLUSTER_USER=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=admin-user) \
		K8S_CLUSTER_PASSWORD=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=admin-password) \
		OPENSHIFT_ICP_CONSOLE=$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=icp-web-console)
	@$(SELF) -s helm:init
	@echo INFO: You can now access your cluster with "kubectl config use-context $(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=cluster-name)"
	@echo INFO: You can access your cluster web console at $(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=web-console)

.PHONY: deploy-openshift-powervc-offline\:clean
## Clean up power-openstack build artifacts
deploy-openshift-powervc-offline\:clean:
	@$(SELF) -s deploy:openshift:powervc:clean OPENSHIFT_POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) OPENSHIFT_POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

# ---------------------------------
## ----- power-powervc-offline -----
# ---------------------------------

.PHONY: deploy-power-powervc-offline\:
## Create power build on PowerVC tenant
deploy-power-powervc-offline: %deploy-power-powervc-offline:
	@$(shell sed -e "s|__AUTH_URL__|$(AUTH_URL)|g" -e "s|__OS_TENANT_NAME__|$(OS_TENANT_NAME)|g" \
        -e "s|__AVAILABILITY_ZONE__|$(AVAILABILITY_ZONE)|g" -e "s|__OS_IMAGE__|$(OS_IMAGE)|g" -e "s|__INSTANCE_NAME__|$(INSTANCE_NAME)|g" \
        -e "s|__IMAGE_DISTRO__|$(IMAGE_DISTRO)|g" -e "s|__CREATE_STORAGE__|$(CREATE_STORAGE)|g" -e "s|__SYSTEM_TUNING__|$(SYSTEM_TUNING)|g" \
		-e "s|__SYSTEM_TUNING__|$(SYSTEM_TUNING)|g" -e "s|__NUM_OF_MASTERS__|$(NUM_OF_MASTERS)|g" \
        -e "s|__NUM_OF_MANAGERS__|$(NUM_OF_MANAGERS)|g" -e "s|__NUM_OF_WORKERS__|$(NUM_OF_WORKERS)|g" \
        -e "s|__NUM_OF_VA__|$(NUM_OF_VA)|g" -e "s|__NUM_OF_PROXIES__|$(NUM_OF_PROXIES)|g" -e "s|__NUM_OF_GLUSTERS__|$(NUM_OF_GLUSTERS)|g" \
		-e "s|__ICP_CONFIG_FILE__|$(ICP_CONFIG_FILE)|g" -e "s|__OS_PRIVATE_NETWORK__|$(OS_PRIVATE_NETWORK)|g" \-e "s|__OS_NETWORK__|$(OS_NETWORK)|g"  \
		-e "s|__VA__|$(VA)|g" -e "s|__ISTIO__|$(ISTIO)|g" -e "s|__GLUSTER_FS__|$(GLUSTER_FS)|g" \
        -e "s|__MCM__|$(MCM)|g" -e "s|__PSN__|$(PSN)|g" -e "s|__NPDD__|$(NPDD)|g" -e "s|__REPO__|$(DEPLOY_REPO)|g" \
		-e "s|__MINIO__|$(MINIO)|g" -e "s|__KNATIVE__|$(KNATIVE)|g" -e "s|__OFFLINE_IMAGE_LOCATION__|$(OFFLINE_IMAGE_LOCATION)|g" \
        -e "s|__OFFLINE_IMAGE__|$(DEPLOY_REPO)|g" -e "s|__ARTIFACTORY_USER__|$(ARTIFACTORY_USER)|g" \
        -e "s|__ARTIFACTORY_API_KEY__|$(ARTIFACTORY_TOKEN)|g" -e "s|__MASTER_TEMPLATE__|$(MASTER_TEMPLATE)|g" \
        -e "s|__MANAGER_TEMPLATE__|$(MANAGER_TEMPLATE)|g" -e "s|__WORKER_TEMPLATE__|$(WORKER_TEMPLATE)|g" -e "s|__VA_TEMPLATE__|$(VA_TEMPLATE)|g" \
        -e "s|__PROXY_TEMPLATE__|$(PROXY_TEMPLATE)|g" -e "s|__GLUSTER_TEMPLATE__|$(GLUSTER_TEMPLATE)|g" -e "s|__HIGH_AVAILABILITY__|$(HIGH_AVAILABILITY)|g" templates/$(TERRAFORM_VARS_FILE).template > $(TERRAFORM_VARS_FILE))
	@echo -e        "\nAUTH_URL" = $(AUTH_URL)\
        "\nOS_TENANT_NAME" = $(OS_TENANT_NAME)\
        "\nAVAILABILITY_ZONE" = $(AVAILABILITY_ZONE)\
        "\nOS_IMAGE" = $(OS_IMAGE)\
        "\nIMAGE_DISTRO" = $(IMAGE_DISTRO)\
        "\nNUM_OF_MASTERS" = $(NUM_OF_MASTERS)\
        "\nNUM_OF_MANAGERS" = $(NUM_OF_MANAGERS)\
        "\nNUM_OF_WORKERS" = $(NUM_OF_WORKERS)\
        "\nNUM_OF_VA" = $(NUM_OF_VA)\
        "\nNUM_OF_PROXIES" = $(NUM_OF_PROXIES)\
        "\nNUM_OF_GLUSTERS" = $(NUM_OF_GLUSTERS)\
        "\nCREATE_STORAGE" = $(CREATE_STORAGE)\
        "\nSYSTEM_TUNING" = $(SYSTEM_TUNING)\
        "\nICP_CONFIG_FILE" = $(ICP_CONFIG_FILE)\
        "\nOS_PRIVATE_NETWORK" = $(OS_PRIVATE_NETWORK)\
        "\nOS_NETWORK" = $(OS_NETWORK)\
        "\nVA" = $(VA)\
        "\nISTIO" = $(ISTIO)\
        "\nGLUSTER_FS" = $(GLUSTER_FS)\
        "\nMCM" = $(MCM)\
        "\nPSN" = $(PSN)\
        "\nNPDD" = $(NPDD)\
        "\nMINIO" = $(MINIO)\
        "\nKNATIVE" = $(KNATIVE)\
        "\nMASTER_TEMPLATE" = $(MASTER_TEMPLATE)\
        "\nMANAGER_TEMPLATE" = $(MANAGER_TEMPLATE)\
        "\nWORKER_TEMPLATE" = $(WORKER_TEMPLATE)\
        "\nVA_TEMPLATE" = $(VA_TEMPLATE)\
        "\nPROXY_TEMPLATE" = $(PROXY_TEMPLATE)\
        "\nGLUSTER_TEMPLATE" = $(GLUSTER_TEMPLATE)\
        "\nHIGH_AVAILABILITY" = $(HIGH_AVAILABILITY) 

	@$(SELF) -s deploy:powervc POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

.PHONY: deploy-power-powervc-offline\:config
## Configure kubectl for access to power-offline build
deploy-power-powervc-offline\:config:
    #@$(SELF) -s kubectl:install
	@$(SELF) -s kubectl:config K8S_CLUSTER_NAME=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=cluster-name) K8S_CLUSTER_MASTER_IP=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=master-node) K8S_CLUSTER_SSH_USER=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=vm-username)
	@$(SELF) -s helm:config K8S_CLUSTER_NAME=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=cluster-name) K8S_CLUSTER_MASTER_IP=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=master-node) K8S_CLUSTER_SSH_USER=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=vm-username)
	@$(SELF) -s helm:init
	@echo INFO: You can now access your cluster with "kubectl config use-context $(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=cluster-name)"
	@echo INFO: You can access your cluster web console at $(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=web-console)

.PHONY: deploy-power-powervc-offline\:clean
## Clean up power-offline build artifacts
deploy-power-powervc-offline\:clean:
	@$(SELF) -s deploy:powervc:clean POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

# ------------------------------------------------
## ----- powervc icp release upgrade offline -----
# ------------------------------------------------



.PHONY: deploy-power-powervc-base-upgrade\:
## Create power build on PowerVC tenant
deploy-power-powervc-base-upgrade: %deploy-power-powervc-base-upgrade:
	@$(shell sed -e "s|__AUTH_URL__|$(AUTH_URL)|g" -e "s|__OS_TENANT_NAME__|$(OS_TENANT_NAME)|g" \
        -e "s|__AVAILABILITY_ZONE__|$(AVAILABILITY_ZONE)|g" -e "s|__OS_IMAGE__|$(OS_IMAGE)|g" -e "s|__ROLL_BACK__|$(ROLL_BACK)|g"\
        -e "s|__IMAGE_DISTRO__|$(IMAGE_DISTRO)|g" -e "s|__CREATE_STORAGE__|$(CREATE_STORAGE)|g" -e "s|__SYSTEM_TUNING__|$(SYSTEM_TUNING)|g" \
		-e "s|__SYSTEM_TUNING__|$(SYSTEM_TUNING)|g" -e "s|__NUM_OF_MASTERS__|$(NUM_OF_MASTERS)|g" \
        -e "s|__NUM_OF_MANAGERS__|$(NUM_OF_MANAGERS)|g" -e "s|__NUM_OF_WORKERS__|$(NUM_OF_WORKERS)|g" \
        -e "s|__NUM_OF_VA__|$(NUM_OF_VA)|g" -e "s|__NUM_OF_PROXIES__|$(NUM_OF_PROXIES)|g" -e "s|__NUM_OF_GLUSTERS__|$(NUM_OF_GLUSTERS)|g" \
		-e "s|__ICP_CONFIG_FILE__|$(ICP_CONFIG_FILE)|g" -e "s|__OS_PRIVATE_NETWORK__|$(OS_PRIVATE_NETWORK)|g" \-e "s|__OS_NETWORK__|$(OS_NETWORK)|g"  \
		-e "s|__VA__|$(VA)|g" -e "s|__ISTIO__|$(ISTIO)|g" -e "s|__GLUSTER_FS__|$(GLUSTER_FS)|g" -e "s|__INSTANCE_NAME__|$(INSTANCE_NAME)|g"\
        -e "s|__MCM__|$(MCM)|g" -e "s|__PSN__|$(PSN)|g" -e "s|__NPDD__|$(NPDD)|g" -e "s|__REPO__|$(DEPLOY_REPO)|g" \
		-e "s|__MINIO__|$(MINIO)|g" -e "s|__KNATIVE__|$(KNATIVE)|g" -e "s|__OFFLINE_IMAGE_LOCATION__|$(OFFLINE_IMAGE_LOCATION)|g" \
        -e "s|__OFFLINE_IMAGE__|$(DEPLOY_REPO)|g" -e "s|__ARTIFACTORY_USER__|$(ARTIFACTORY_USER)|g" \
        -e "s|__ARTIFACTORY_API_KEY__|$(ARTIFACTORY_TOKEN)|g" -e "s|__MASTER_TEMPLATE__|$(MASTER_TEMPLATE)|g" \
        -e "s|__MANAGER_TEMPLATE__|$(MANAGER_TEMPLATE)|g" -e "s|__WORKER_TEMPLATE__|$(WORKER_TEMPLATE)|g" -e "s|__VA_TEMPLATE__|$(VA_TEMPLATE)|g" \
		-e "s|__UPGRADE_IMAGE_LOCATION__|$(BASE_UPGRADE_LOCATION)|g"  \
        -e "s|__PROXY_TEMPLATE__|$(PROXY_TEMPLATE)|g" -e "s|__GLUSTER_TEMPLATE__|$(GLUSTER_TEMPLATE)|g" templates/$(TERRAFORM_VARS_FILE).template > $(TERRAFORM_VARS_FILE))
	
	@echo -e        "\nAUTH_URL" = $(AUTH_URL)\
        "\nOS_TENANT_NAME" = $(OS_TENANT_NAME)\
        "\nAVAILABILITY_ZONE" = $(AVAILABILITY_ZONE)\
        "\nOS_IMAGE" = $(OS_IMAGE)\
        "\nIMAGE_DISTRO" = $(IMAGE_DISTRO)\
        "\nNUM_OF_MASTERS" = $(NUM_OF_MASTERS)\
        "\nNUM_OF_MANAGERS" = $(NUM_OF_MANAGERS)\
        "\nNUM_OF_WORKERS" = $(NUM_OF_WORKERS)\
        "\nNUM_OF_VA" = $(NUM_OF_VA)\
        "\nNUM_OF_PROXIES" = $(NUM_OF_PROXIES)\
        "\nNUM_OF_GLUSTERS" = $(NUM_OF_GLUSTERS)\
        "\nCREATE_STORAGE" = $(CREATE_STORAGE)\
        "\nSYSTEM_TUNING" = $(SYSTEM_TUNING)\
        "\nICP_CONFIG_FILE" = $(ICP_CONFIG_FILE)\
        "\nOS_PRIVATE_NETWORK" = $(OS_PRIVATE_NETWORK)\
        "\nOS_NETWORK" = $(OS_NETWORK)\
        "\nVA" = $(VA)\
        "\nISTIO" = $(ISTIO)\
        "\nGLUSTER_FS" = $(GLUSTER_FS)\
        "\nMCM" = $(MCM)\
        "\nPSN" = $(PSN)\
        "\nNPDD" = $(NPDD)\
        "\nMINIO" = $(MINIO)\
        "\nKNATIVE" = $(KNATIVE)\
        "\nMASTER_TEMPLATE" = $(MASTER_TEMPLATE)\
        "\nMANAGER_TEMPLATE" = $(MANAGER_TEMPLATE)\
        "\nWORKER_TEMPLATE" = $(WORKER_TEMPLATE)\
        "\nVA_TEMPLATE" = $(VA_TEMPLATE)\
        "\nPROXY_TEMPLATE" = $(PROXY_TEMPLATE)\
        "\nGLUSTER_TEMPLATE" = $(GLUSTER_TEMPLATE)\
		"\nOFFLINE_IMAGE_LOCATION" = $(OFFLINE_IMAGE_LOCATION) \
		"\nUPGRADE_IMAGE_LOCATION" = $(BASE_UPGRADE_LOCATION)

	@$(SELF) -s deploy:powervc POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

.PHONY: deploy-power-powervc-base-upgrade\:config
## Configure kubectl for access to power-offline build
deploy-power-powervc-base-upgrade\:config:
    #@$(SELF) -s kubectl:install
	@$(SELF) -s kubectl:config K8S_CLUSTER_NAME=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=cluster-name) K8S_CLUSTER_MASTER_IP=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=master-node) K8S_CLUSTER_SSH_USER=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=vm-username)
	@$(SELF) -s helm:config K8S_CLUSTER_NAME=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=cluster-name) K8S_CLUSTER_MASTER_IP=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=master-node) K8S_CLUSTER_SSH_USER=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=vm-username)
	@$(SELF) -s helm:init
	@echo INFO: You can now access your cluster with "kubectl config use-context $(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=cluster-name)"
	@echo INFO: You can access your cluster web console at $(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=web-console)

.PHONY: deploy-power-powervc-base-upgrade\:clean
## Clean up power-offline build artifacts
deploy-power-powervc-base-upgrade\:clean:
	@$(SELF) -s deploy:powervc:clean POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)


# ------------------------------------------------
## ----- powervc icp fixpack upgrade offline -----
# ------------------------------------------------



.PHONY: deploy-power-powervc-fixpack-upgrade\:
## Create power build on PowerVC tenant
deploy-power-powervc-fixpack-upgrade: %deploy-power-powervc-fixpack-upgrade:
	@$(shell sed -e "s|__AUTH_URL__|$(AUTH_URL)|g" -e "s|__OS_TENANT_NAME__|$(OS_TENANT_NAME)|g" \
        -e "s|__AVAILABILITY_ZONE__|$(AVAILABILITY_ZONE)|g" -e "s|__OS_IMAGE__|$(OS_IMAGE)|g" -e "s|__ROLL_BACK__|$(ROLL_BACK)|g"\
        -e "s|__IMAGE_DISTRO__|$(IMAGE_DISTRO)|g" -e "s|__CREATE_STORAGE__|$(CREATE_STORAGE)|g" -e "s|__SYSTEM_TUNING__|$(SYSTEM_TUNING)|g" \
		-e "s|__SYSTEM_TUNING__|$(SYSTEM_TUNING)|g" -e "s|__NUM_OF_MASTERS__|$(NUM_OF_MASTERS)|g" \
        -e "s|__NUM_OF_MANAGERS__|$(NUM_OF_MANAGERS)|g" -e "s|__NUM_OF_WORKERS__|$(NUM_OF_WORKERS)|g" \
        -e "s|__NUM_OF_VA__|$(NUM_OF_VA)|g" -e "s|__NUM_OF_PROXIES__|$(NUM_OF_PROXIES)|g" -e "s|__NUM_OF_GLUSTERS__|$(NUM_OF_GLUSTERS)|g" \
		-e "s|__ICP_CONFIG_FILE__|$(ICP_CONFIG_FILE)|g" -e "s|__OS_PRIVATE_NETWORK__|$(OS_PRIVATE_NETWORK)|g" \-e "s|__OS_NETWORK__|$(OS_NETWORK)|g"  \
		-e "s|__VA__|$(VA)|g" -e "s|__ISTIO__|$(ISTIO)|g" -e "s|__GLUSTER_FS__|$(GLUSTER_FS)|g" -e "s|__INSTANCE_NAME__|$(INSTANCE_NAME)|g"\
        -e "s|__MCM__|$(MCM)|g" -e "s|__PSN__|$(PSN)|g" -e "s|__NPDD__|$(NPDD)|g" -e "s|__REPO__|$(DEPLOY_REPO)|g" \
		-e "s|__MINIO__|$(MINIO)|g" -e "s|__KNATIVE__|$(KNATIVE)|g" -e "s|__OFFLINE_IMAGE_LOCATION__|$(OFFLINE_IMAGE_LOCATION)|g" \
        -e "s|__OFFLINE_IMAGE__|$(DEPLOY_REPO)|g" -e "s|__ARTIFACTORY_USER__|$(ARTIFACTORY_USER)|g" \
        -e "s|__ARTIFACTORY_API_KEY__|$(ARTIFACTORY_TOKEN)|g" -e "s|__MASTER_TEMPLATE__|$(MASTER_TEMPLATE)|g" \
        -e "s|__MANAGER_TEMPLATE__|$(MANAGER_TEMPLATE)|g" -e "s|__WORKER_TEMPLATE__|$(WORKER_TEMPLATE)|g" -e "s|__VA_TEMPLATE__|$(VA_TEMPLATE)|g" \
		-e "s|__FIXPACK_DOWNLOAD_LOCATIONS__|$(FIXPACK_DOWNLOAD_LOCATIONS)|g" \
        -e "s|__PROXY_TEMPLATE__|$(PROXY_TEMPLATE)|g" -e "s|__GLUSTER_TEMPLATE__|$(GLUSTER_TEMPLATE)|g" templates/$(TERRAFORM_VARS_FILE).template > $(TERRAFORM_VARS_FILE))
	
	@echo -e        "\nAUTH_URL" = $(AUTH_URL)\
        "\nOS_TENANT_NAME" = $(OS_TENANT_NAME)\
        "\nAVAILABILITY_ZONE" = $(AVAILABILITY_ZONE)\
        "\nOS_IMAGE" = $(OS_IMAGE)\
        "\nIMAGE_DISTRO" = $(IMAGE_DISTRO)\
        "\nNUM_OF_MASTERS" = $(NUM_OF_MASTERS)\
        "\nNUM_OF_MANAGERS" = $(NUM_OF_MANAGERS)\
        "\nNUM_OF_WORKERS" = $(NUM_OF_WORKERS)\
        "\nNUM_OF_VA" = $(NUM_OF_VA)\
        "\nNUM_OF_PROXIES" = $(NUM_OF_PROXIES)\
        "\nNUM_OF_GLUSTERS" = $(NUM_OF_GLUSTERS)\
        "\nCREATE_STORAGE" = $(CREATE_STORAGE)\
        "\nSYSTEM_TUNING" = $(SYSTEM_TUNING)\
        "\nICP_CONFIG_FILE" = $(ICP_CONFIG_FILE)\
        "\nOS_PRIVATE_NETWORK" = $(OS_PRIVATE_NETWORK)\
        "\nOS_NETWORK" = $(OS_NETWORK)\
        "\nVA" = $(VA)\
        "\nISTIO" = $(ISTIO)\
        "\nGLUSTER_FS" = $(GLUSTER_FS)\
        "\nMCM" = $(MCM)\
        "\nPSN" = $(PSN)\
        "\nNPDD" = $(NPDD)\
        "\nMINIO" = $(MINIO)\
        "\nKNATIVE" = $(KNATIVE)\
        "\nMASTER_TEMPLATE" = $(MASTER_TEMPLATE)\
        "\nMANAGER_TEMPLATE" = $(MANAGER_TEMPLATE)\
        "\nWORKER_TEMPLATE" = $(WORKER_TEMPLATE)\
        "\nVA_TEMPLATE" = $(VA_TEMPLATE)\
        "\nPROXY_TEMPLATE" = $(PROXY_TEMPLATE)\
        "\nGLUSTER_TEMPLATE" = $(GLUSTER_TEMPLATE) \
		"\nOFFLINE_IMAGE_LOCATION" = $(OFFLINE_IMAGE_LOCATION) \
		"\nFIXPACK_DOWNLOAD_LOCATION" = $(FIXPACK_DOWNLOAD_LOCATIONS)

	@$(SELF) -s deploy:powervc POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)

.PHONY: deploy-power-powervc-fixpack-upgrade\:config
## Configure kubectl for access to power-offline build
deploy-power-powervc-fixpack-upgrade\:config:
    #@$(SELF) -s kubectl:install
	@$(SELF) -s kubectl:config K8S_CLUSTER_NAME=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=cluster-name) K8S_CLUSTER_MASTER_IP=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=master-node) K8S_CLUSTER_SSH_USER=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=vm-username)
	@$(SELF) -s helm:config K8S_CLUSTER_NAME=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=cluster-name) K8S_CLUSTER_MASTER_IP=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=master-node) K8S_CLUSTER_SSH_USER=$(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=vm-username)
	@$(SELF) -s helm:init
	@echo INFO: You can now access your cluster with "kubectl config use-context $(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=cluster-name)"
	@echo INFO: You can access your cluster web console at $(shell $(SELF) terraform:output TERRAFORM_DIR=$(TERRAFORM_DIR) TERRAFORM_OUTPUT_VAR=web-console)

.PHONY: deploy-power-powervc-fixpack-upgrade\:clean
## Clean up power-offline build artifacts
deploy-power-powervc-fixpack-upgrade\:clean:
	@$(SELF) -s deploy:powervc:clean POWERVC_DEPLOY_DIR=$(TERRAFORM_DIR) POWERVC_TERRAFORM_VARS_FILE=$(TERRAFORM_VARS_FILE)


# -----------------------------------------------
## ----- setup-dependencies, show-env -----
# -----------------------------------------------

.PHONY: setup-dependencies
setup-dependencies:
	@mkdir -p `pwd`/tmp

# when calling, FUNCTIONAL_TEST_REPO should be set to the location of the functional tests to be run
# these tests should have setup-dependencies and run-$(TEST-SUITE)-tests targets defined
.PHONY: run-functional-tests
run-functional-tests:
ifdef FUNCTIONAL_TEST_REPO
	git clone https://$(GITHUB_USER):$(GITHUB_TOKEN)@github.ibm.com/IBMPrivateCloud/$(FUNCTIONAL_TEST_REPO)
ifdef FUNCTIONAL_TEST_REPO_TAG
	cd $(FUNCTIONAL_TEST_REPO); git checkout tags/$(FUNCTIONAL_TEST_REPO_TAG)
endif
	ln -s $(PWD)/build-harness $(PWD)/$(FUNCTIONAL_TEST_REPO)/build-harness && \
	ln -s $(PWD)/id_rsa $(PWD)/$(FUNCTIONAL_TEST_REPO)/id_rsa && \
	ln -s $(PWD)/id_rsa.pub $(PWD)/$(FUNCTIONAL_TEST_REPO)/id_rsa.pub && \
	ln -s $(PWD)/.$(TARGET) $(PWD)/$(FUNCTIONAL_TEST_REPO)/.$(TARGET)
	cd $(FUNCTIONAL_TEST_REPO); make setup-dependencies
	cd $(FUNCTIONAL_TEST_REPO); make run-$(TEST_SUITE)-tests ICP_CONSOLE=$(shell $(SELF) terraform:output TERRAFORM_DIR=.$(TARGET) TERRAFORM_OUTPUT_VAR=web-console)
endif

.PHONY: run-upgrade
run-upgrade:
	chmod +x ${PWD}/upgrade.sh
	${PWD}/upgrade.sh
.PHONY: show-env
show-env:
	$(call assert-set,TARGET)
	@$(SELF) -s kubectl:cmd KUBECTL_COMMAND=cluster-info
	@echo -e "INFO: Cluster Pod Status:"
	@$(SELF) -s kubectl:cmd KUBECTL_COMMAND="get pods -o wide -n kube-system"
	@echo -e "INFO: docker images on master node"
	@ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i id_rsa ubuntu@$(shell $(SELF) terraform:output TERRAFORM_OUTPUT_VAR=master-node) 'docker images'
 
