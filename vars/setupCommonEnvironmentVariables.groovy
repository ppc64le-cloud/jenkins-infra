def call() {
    script {
        //VMs setup
        env.INSTANCE_NAME = "ltccci"
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
        env.HELPERNODE_TAG = "5eab3db53976bb16be582f2edc2de02f7510050d"
        env.INSTALL_PLAYBOOK_REPO = "https://github.com/ocp-power-automation/ocp4-playbooks"
        env.INSTALL_PLAYBOOK_TAG = "d2509c4b4a67879daa6338f68e8e7eb1e15d05e2"
        env.UPGRADE_IMAGE = ""
        env.UPGRADE_PAUSE_TIME = ""
        env.UPGRADE_DELAY_TIME = ""

        //E2e Variables
        env.E2E_GIT = "https://github.com/openshift/origin"
        env.E2E_BRANCH="release-${OCP_RELEASE}"
        env.E2E_EXCLUDE_LIST = "https://raw.github.ibm.com/redstack-power/e2e-exclude-list/${env.OCP_RELEASE}-powervm/ocp${env.OCP_RELEASE}_power_exclude_list.txt"

        //Makefile variables
        env.TERRAFORM_FORCE_KEYPAIR_CREATION = "0"

        //Build Harness org
        env.BUILD_HARNESS_ORG="powercloud-cicd"

        //Scale variables
        env.SCALE_NUM_OF_DEPLOYMENTS = "60"
        env.SCALE_NUM_OF_NAMESPACES = "1000"

        //Slack message
        env.MESSAGE=""
    }
}