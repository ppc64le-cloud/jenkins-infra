def call(String auth_url, String vcpus, String memory, String punits, String templatetag){
    // this creates a compute template in PowerVC server based on the choice
    sh(returnStatus: true, returnStdout: false, script: "openstack  --os-auth-url \"${auth_url}\" --insecure flavor create --id ${templatetag}  --ram ${memory} --vcpus ${vcpus} ${templatetag} --property powervm:proc_units=${punits}")
}
