def call(String url, String relative_target_dir){
    script {
            checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: relative_target_dir], [$class: 'CleanBeforeCheckout'], [$class: 'CloneOption', depth: 0, noTags: false, reference: '', shallow: false, timeout: 20]], submoduleCfg: [], userRemoteConfigs: [[url:url , credentialsId: 'ibm-github']]])
    }
}