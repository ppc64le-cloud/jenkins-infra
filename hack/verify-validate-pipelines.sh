#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

#set -x

INFRA_ROOT="$(cd "$(dirname "${BASH_SOURCE}")/.." && pwd -P)"
JENKINS_URI="${JENKINS_URI:-http://localhost:8080}"
JENKINS_USER="${JENKINS_USER:-admin}"
JENKINS_PASSWORD="${JENKINS_PASSWORD:-passw0rd}"

pushd ${INFRA_ROOT}
jenkinsfiles=(`find . -name Jenkinsfile -print|sed 's|./||'`)
popd

for jenkinsfile in "${jenkinsfiles[@]}"
do
    echo "Validating : ${INFRA_ROOT}/${jenkinsfile}"
    curl -X POST --user ${JENKINS_USER}:${JENKINS_PASSWORD} -F "jenkinsfile=<${INFRA_ROOT}/${jenkinsfile}" ${JENKINS_URI}/pipeline-model-converter/validate
done
