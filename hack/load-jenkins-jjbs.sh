#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

INFRA_ROOT="$(cd "$(dirname "${BASH_SOURCE}")/.." && pwd -P)"

jenkins-jobs --user ${JENKINS_USER} --password ${JENKINS_PASSWORD} update ${INFRA_ROOT}/jobs/jjb