def call(String auth_url, String distro){
    // this get the image from powervc based on the distro chose.
    switch(distro) {
        case "RHEL7.6":
            IMAGE=sh(returnStdout: true, script: "openstack  --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i rhel7.6| grep -i -v  rhel7.6-alt|tail -n 1|tr '\n' ' ' ").trim()
            break
        case "RHEL7.5":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i rhel7.5| grep -i -v rhel7.5-alt|tail -n 1|tr '\n' ' ' ").trim()
            break
        case "UBUNTU18.04":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list --format value -c Name | grep -i u18.04| tail -n 1|tr '\n' ' ' ").trim()
            break
        case "UBUNTU16.04":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i u16.04| tail -n 1|tr '\n' ' ' ").trim()
            break
        case "RHEL7.6-ALT":
            IMAGE=sh(returnStdout: true, script: "openstack  --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i rhel7.6-alt| tail -n 1|tr '\n' ' ' ").trim()
            break
        case "RHEL7.5-ALT":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i rhel7.5-alt| tail -n 1|tr '\n' ' ' ").trim()
            break
        case "SLES12SP3":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i sles12.*sp3| tail -n 1|tr '\n' ' ' ").trim()
            break
        case "SLES12SP4":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i sles12.*sp4| tail -n 1|tr '\n' ' ' ").trim()
            break
        default:
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i rhel7.6| tail -n 1|tr '\n' ' ' ").trim()
            break
    }

    return IMAGE
}