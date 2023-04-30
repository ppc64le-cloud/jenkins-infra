def call(){
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
               sh ${WORKSPACE}/scripts/odf/odf-build-info.sh >> ${WORKSPACE}/odf-commands.txt
               grep "full_version" ${WORKSPACE}/odf-commands.txt | tail -1| awk '{print $2}' > odfbuild
            '''
             odfbuild = readFile 'odfbuild'
             env.ODF_BUILD = "${odfbuild}".trim()
        }
        catch (err) {
            echo 'Error ! capturing command o/p failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}
