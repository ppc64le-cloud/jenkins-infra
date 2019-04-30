def call(String authurl, String pvczone, String distroimage, String mast, String work, String manger, String vul)
{
    // Updating tempate file with env variables
    sh " cd ${WORKSPACE}/canary-deployments && [ ! -z authurl ] && grep -q '^auth_url *=' \"${TERMPLATE_FILE}\" && sed -i \"s|^auth_url *=.*\$|auth_url = \\\"\"'${authurl}'\"\\\"|g\" \"${TERMPLATE_FILE}\" || sed -i \"4s|\$|\\nauth_url = \\\"\"'${authurl}'\"\\\"|g\" \"${TERMPLATE_FILE}\""
    sh " cd ${WORKSPACE}/canary-deployments && [ ! -z pvczone ] && grep -q '^availability_zone *=' \"${TERMPLATE_FILE}\" && sed -i \"s|^availability_zone *=.*\$|availability_zone = \\\"\"'${pvczone}'\"\\\"|g\" \"${TERMPLATE_FILE}\" || sed -i \"4s|\$|\\navailability_zone = \\\"\"'${pvczone}'\"\\\"|g\" \"${TERMPLATE_FILE}\""
    sh " cd ${WORKSPACE}/canary-deployments && [ ! -z distroimage ] && grep -q '^os_image_name *=' \"${TERMPLATE_FILE}\" && sed -i \"s|^os_image_name *=.*\$|os_image_name = \\\"\"${distroimage}\"\\\"|g\" \"${TERMPLATE_FILE}\" || sed -i \"4s|\$|\\nos_image_name = \\\"\"${distroimage}\"\\\"|g\" \"${TERMPLATE_FILE}\""
    sh " cd ${WORKSPACE}/canary-deployments && grep -q '^master =' \"${TERMPLATE_FILE}\" && sed -i \"s|^master *=.*\$|master = \"'${mast}'\"|g\" \"${TERMPLATE_FILE}\" || sed -i \"4s|\$|\\nmaster = \"'${mast}'\"|g\" \"${TERMPLATE_FILE}\""
    if (env.DEPLOY_WORKER == "true")
    {
        sh " cd ${WORKSPACE}/canary-deployments && grep -q '^worker *=' \"${TERMPLATE_FILE}\" && sed -i \"s|^worker =.*\$|worker = \"'${work}'\"|g\" \"${TERMPLATE_FILE}\" || sed -i \"4s|\$|\\nworker = \"'${work}'\"|g\" \"${TERMPLATE_FILE}\""
    }
    else if (env.DEPLOY_WORKER == "false")
    {
        WORKER1=sh (returnStdout: true, script: "echo ${work}|sed \"s|nodes *=.*,|nodes = \\\"0\\\",|g\" |tr '\n' ' '| sed \"s|\\s*\$||g\"")
        sh " cd ${WORKSPACE}/canary-deployments && grep -q '^worker *=' \"${TERMPLATE_FILE}\" && sed -i \"s|^worker *=.*\$|worker = \"'${WORKER1}'\"|g\" \"${TERMPLATE_FILE}\" || sed -i \"4s|\$|\\nworker = \"'${WORKER1}'\"|g\" \"${TERMPLATE_FILE}\""
    }
    if (env.DEPLOY_MANAGER == "true")
    {
        sh " cd ${WORKSPACE}/canary-deployments && grep -q '^manager *=' \"${TERMPLATE_FILE}\" && sed -i \"s|^manager *=.*\$|manager = \"'${manger}'\"|g\" \"${TERMPLATE_FILE}\" || sed -i \"4s|\$|\\nmanager = \"'${manger}'\"|g\" \"${TERMPLATE_FILE}\""
    }
    else if (env.DEPLOY_MANAGER == "false")
    {
        MANAGER1=sh (returnStdout: true, script: "echo ${manger}|sed \"s|nodes *=.*,|nodes = \\\"0\\\",|g\" |tr '\n' ' '| sed \"s|\\s*\$||g\"")
        sh " cd ${WORKSPACE}/canary-deployments && grep -q '^manager *=' \"${TERMPLATE_FILE}\" && sed -i \"s|^manager *=.*\$|manager = \"'${MANAGER1}'\"|g\" \"${TERMPLATE_FILE}\" || sed -i \"4s|\$|\\nmanager = \"'${MANAGER1}'\"|g\" \"${TERMPLATE_FILE}\""
    }
    if (env.DEPLOY_VA == "true")
    {
        sh " cd ${WORKSPACE}/canary-deployments && grep -q '^va *=' \"${TERMPLATE_FILE}\" && sed -i \"s|^va *=.*\$|va = \"'${vul}'\"|g\" \"${TERMPLATE_FILE}\" || sed -i \"4s|\$|\\nva = \"'${vul}'\"|g\" \"${TERMPLATE_FILE}\""
        sh " cd ${WORKSPACE}/canary-deployments && grep -q 'vulnerability-advisor *=' \"${TERMPLATE_FILE}\" && sed -i \"s|vulnerability-advisor *=.*\$|vulnerability-advisor = \\\"enabled\\\"|g\" \"${TERMPLATE_FILE}\" || sed -i \"|\"vulnerability-advisor = enabled\" |a  \"'management_services = {'\"|\" \"${TERMPLATE_FILE}\""
    }
    else if (env.DEPLOY_VA == "false")
    {
        VA1=sh (returnStdout: true, script: "echo ${vul}|sed \"s|nodes *=.*,|nodes = \\\"0\\\",|g\" |tr '\n' ' '| sed \"s|\\s*\$||g\"")
        sh " cd ${WORKSPACE}/canary-deployments && grep -q '^va *=' \"${TERMPLATE_FILE}\" && sed -i \"s|^va *=.*\$|va = \"'${VA1}'\"|g\" \"${TERMPLATE_FILE}\" || sed -i \"4s|\$|\\nva = \"'${VA1}'\"|g\" \"${TERMPLATE_FILE}\""
    }
    if ( conf != null ) {
         sh " cd ${WORKSPACE}/canary-deployments && sed -i \"\$(( \$( wc -l < \"${TERMPLATE_FILE}\")-1))s|\$|\\n\"${CONF}\"|\" \"${TERMPLATE_FILE}\""
    }
}