#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

INFRA_ROOT="$(cd "$(dirname "${BASH_SOURCE}")/.." && pwd -P)"

${INFRA_ROOT}/hack/load-jenkins-jjbs.sh
${INFRA_ROOT}/hack/load-jenkins-pipelines.sh
