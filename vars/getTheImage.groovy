def call(String auth_url, String distro, String filter){
    // this get the image from powervc based on the distro chose.
    switch(distro) {
        case "rhcos-43":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i rhcos-43 | tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
        case "rhcos-44":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i rhcos-44 | tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
        case "rhcos-45":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i rhcos-45 | tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
        case "rhcos-46":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i rhcos-46 | tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
        case "RHEL8.0":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i rhel8.0| tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
        case "RHEL8.1":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i rhel8.1| tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
        case "RHEL7.8":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i \"${filter}\" | grep -i rhel7.8| tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
        case "RHEL7.7":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i \"${filter}\" | grep -i rhel7.7| tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
        case "RHEL7.6":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i \"${filter}\" | grep -i rhel7.6| grep -i -v  rhel7.6-alt|tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
        case "RHEL7.5":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i \"${filter}\" | grep -i rhel7.5| grep -i -v rhel7.5-alt|tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
        case "UBUNTU18.04":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list --format value -c Name | grep -i \"${filter}\" | grep -i u18.04| tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="ubuntu"
            break
        case "UBUNTU16.04":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i \"${filter}\" | grep -i u16.04| tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="ubuntu"
            break
        case "RHEL7.6-ALT":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i \"${filter}\" | grep -i rhel7.6-alt| tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
        case "RHEL7.5-ALT":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i \"${filter}\" | grep -i rhel7.5-alt| tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
        case "SLES12SP3":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i \"${filter}\" | grep -i sles12.*sp3| tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="sles"
            break
        case "SLES12SP4":
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i \"${filter}\" | grep -i sles12.*sp4| tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="sles"
            break
        default:
            IMAGE=sh(returnStdout: true, script: "openstack --os-auth-url \"${auth_url}\" --insecure image list  --format value -c Name | grep -i \"${filter}\" | grep -i rhel7.6| tail -n 1|tr '\n' ' ' ").trim()
            env.DISTRO="rhel"
            break
    }

    return IMAGE
}
