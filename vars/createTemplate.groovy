def call(String auth_url, String vcpus, String memory, String punits, String templatetag){
    // this creates a compute template in PowerVC server based on the choice
    echo "Creating Compute Template"
    sh(returnStatus: true, returnStdout: false, script: "set +x ; openstack  --os-auth-url \"${auth_url}\" --insecure flavor create --private --project \"${env.OS_TENANT_NAME}\"  --ram ${memory} --vcpus ${vcpus} ${templatetag} --property powervm:proc_units=${punits}")
}
