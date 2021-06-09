#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

#set -x

INFRA_ROOT="$(cd "$(dirname "${BASH_SOURCE}")/.." && pwd -P)"
JENKINS_URI="${JENKINS_URI:-http://localhost:8080}"
JENKINS_USER="${JENKINS_USER:-admin}"
JENKINS_PASSWORD="${JENKINS_PASSWORD:-passw0rd}"
PULL_NUMBER="${PULL_NUMBER:-1}"
pushd ${INFRA_ROOT}
jenkinsfiles=(`find . -name Jenkinsfile -print|sed 's|./||'`)
popd

curl -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/ppc64le-cloud/jenkins-infra/pulls/${PULL_NUMBER}/files | grep  "\"filename\":"|cut -d ':' -f 2 |sed -e 's/,*$//g' -e 's/"//' -e 's/"$//'|awk '$1=$1' > files.txt

for jenkinsfile in "${jenkinsfiles[@]}"
do
    while IFS= read -r filename; do
    if [[ "${jenkinsfile}" == *"${filename}"* ]]; then
        echo "Validating : ${INFRA_ROOT}/${jenkinsfile}"
        curl -X POST --user ${JENKINS_USER}:${JENKINS_PASSWORD} -F "jenkinsfile=<${INFRA_ROOT}/${jenkinsfile}" ${JENKINS_URI}/pipeline-model-converter/validate	
    fi	    
    done < <(cat files.txt)
done
