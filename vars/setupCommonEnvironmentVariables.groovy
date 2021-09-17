def call() {
    script {
        //Failed stage
        env.FAILED_STAGE=""
        //VMs setup
        if ( env.POWERVS == "true" ) {
            env.INSTANCE_NAME = "rdr-cicd"
            env.NETWORK_NAME = "ocp-net"
            env.RHEL_USERNAME = "root"
            env.RHEL_SMT = "4"
            env.CLUSTER_DOMAIN = "redhat.com"
            env.SYSTEM_TYPE = "s922"
            env.ENABLE_LOCAL_REGISTRY = "false"
            env.LOCAL_REGISTRY_IMAGE = "docker.io/ibmcom/registry-ppc64le:2.6.2.5"
            env.SETUP_SQUID_PROXY = "true"

            // Bellow 4 variables are not used. Disabled in template
            env.HELPERNODE_REPO = "https://github.com/RedHatOfficial/ocp4-helpernode"
            env.HELPERNODE_TAG = "1ac7f276b537cd734240eda9ed554a254ba80629"
            env.INSTALL_PLAYBOOK_REPO = "https://github.com/ocp-power-automation/ocp4-playbooks"
            switch (env.OCP_RELEASE) {
                case "4.8":
                    env.INSTALL_PLAYBOOK_TAG = "64fd5edadf380820f71e44bb6bca93e9b083fe5c"
                    break
                case "4.7":
                    env.INSTALL_PLAYBOOK_TAG = "1d4c9cad20d7559392d34990ccde5813fdb855a1"
                    break
                case "4.6":
                    env.INSTALL_PLAYBOOK_TAG = "e89bef76cec089a481d6de2b7fa07944ae0481a5"
                    break
                case "4.5":
                    env.INSTALL_PLAYBOOK_TAG = "15d226e52b0bae11943e3127206dd454891032c8"
                    break
                default:
                     env.INSTALL_PLAYBOOK_TAG = "64fd5edadf380820f71e44bb6bca93e9b083fe5c"
            }

            //Upgrade variables
            env.UPGRADE_IMAGE = ""
            env.UPGRADE_PAUSE_TIME = ""
            env.UPGRADE_DELAY_TIME = ""


            //E2e Variables
            env.E2E_GIT = "https://github.com/openshift/origin"
            env.E2E_BRANCH="release-${env.OCP_RELEASE}"
            if (OCP_RELEASE == "4.5" || OCP_RELEASE == "4.6" ) {
                env.E2E_EXCLUDE_LIST = "https://raw.github.ibm.com/redstack-power/e2e-exclude-list/${env.OCP_RELEASE}-powervm/ocp${env.OCP_RELEASE}_power_exclude_list.txt"
            }
            else{
                env.E2E_EXCLUDE_LIST = "https://raw.github.ibm.com/redstack-power/e2e-exclude-list/${env.OCP_RELEASE}-powervs/ocp${env.OCP_RELEASE}_power_exclude_list.txt"
            }

            //Scale variables
            env.SCALE_NUM_OF_DEPLOYMENTS = "60"
            env.SCALE_NUM_OF_NAMESPACES = "1000"

            //Slack message
            env.MESSAGE=""

            env.DEPLOYMENT_STATUS = false
            env.BASTION_IP = ""

            //Pull Secret
            env.PULL_SECRET_FILE = "${WORKSPACE}/deploy/data/pull-secret.txt"
        }
        else {
            //PowerVC ENV Variables
            env.OS="linux"
            env.OS_IDENTITY_API_VERSION='3'
            env.OS_TENANT_NAME="ibm-default"
            env.OS_USER_DOMAIN_NAME="default"
            env.OS_PROJECT_DOMAIN_NAME="Default"
            env.OS_COMPUTE_API_VERSION=2.46
            env.OS_NETWORK_API_VERSION=2.0
            env.OS_IMAGE_API_VERSION=2
            env.OS_VOLUME_API_VERSION=2
            env.OS_AUTH_URL="https://scnlcicdpowervc.pok.stglabs.ibm.com:5000/v3/"
            env.OS_NETWORK="icp_network4"
            env.OS_PRIVATE_NETWORK="icp_network4"
            env.MASTER_TEMPLATE="${JOB_BASE_NAME}"+"-"+"${BUILD_NUMBER}"+"-"+"mas"
            env.WORKER_TEMPLATE="${JOB_BASE_NAME}"+"-"+"${BUILD_NUMBER}"+"-"+"wor"
            env.BOOTSTRAP_TEMPLATE="${JOB_BASE_NAME}"+"-"+"${BUILD_NUMBER}"+"-"+"boo"
            env.BASTION_TEMPLATE="${JOB_BASE_NAME}"+"-"+"${BUILD_NUMBER}"+"-"+"bas"
            env.RHEL_USERNAME = "root"
            env.OS_INSECURE = true

            // Pull secrets
            env.PULL_SECRET_FILE = "${WORKSPACE}/deploy/data/pull-secret.txt"

            env.OPENSHIFT_POWERVC_GIT_TF_DEPLOY_PROJECT="https://github.com/ocp-power-automation/ocp4-upi-powervm.git"

            //Cluster and vm details
            env.CLUSTER_DOMAIN="redhat.com"
            env.INSTANCE_NAME = "rdr-cicd"
            env.MOUNT_ETCD_RAMDISK="true"
            env.CHRONY_CONFIG="true"
            env.SCG_ID = "259fb507-3ac0-49bd-973b-c27da9ed0537"
            env.VOLUME_STORAGE_TEMPLATE = "c340f1_v7k base template"

            //e2e variables
            if ( env.ENABLE_E2E_TEST ) {
                env.E2E_GIT="https://github.com/openshift/origin"
                env.E2E_BRANCH="release-${env.OCP_RELEASE}"
                env.E2E_EXCLUDE_LIST="https://raw.github.ibm.com/redstack-power/e2e-exclude-list/${env.OCP_RELEASE}-powervm/ocp${env.OCP_RELEASE}_power_exclude_list.txt"
                env.ENABLE_E2E_UPGRADE="false"
            }

            //Scale test variables
            if ( env.ENABLE_SCALE_TEST ) {
                env.SCALE_NUM_OF_DEPLOYMENTS = "60"
                env.SCALE_NUM_OF_NAMESPACES = "1000"
                env.EXPOSE_IMAGE_REGISTRY = "false"
            }

            //Proxy setup
            env.SETUP_SQUID_PROXY = "false"
            env.PROXY_ADDRESS = ""

            //Slack message
            env.MESSAGE=""

            env.DEPLOYMENT_STATUS = false
            env.BASTION_IP = ""
            //Common Service
            env.CS_INSTALL = "false"

            // Compute Template Variables
            env.WORKER_MEMORY_MB=""
            env.MASTER_MEMORY_MB=""
            env.BASTION_MEMORY_MB=""
            env.BOOTSTRAP_MEMORY_MB=''
        }
    }
}
