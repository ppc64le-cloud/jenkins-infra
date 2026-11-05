#!/bin/bash
#
# load-jenkins-pipelines.sh — Load Jenkins pipeline jobs via Jenkins Job Builder (JJB).
#
# USAGE:
#   JENKINS_USER=<user> JENKINS_PASSWORD=<pass> JENKINS_URI=<url> [WHAT=<dirs>] ./hack/load-jenkins-pipelines.sh
#
# ENVIRONMENT VARIABLES:
#   JENKINS_USER      Jenkins username (required)
#   JENKINS_PASSWORD  Jenkins password or API token (required)
#   JENKINS_URI       URL of the target Jenkins (required)
#   WHAT              Controls which job directories are loaded (optional).
#                     Space-separated list of subdirectory names under jobs/pipelines/.
#                     See modes below.
#
# AVAILABLE DIRECTORIES (jobs/pipelines/):
#   powervm                  PowerVC (on-prem) OCP, ODF jobs
#   powervs                  PowerVS (IBM Cloud) OCP, IPI, hypershift jobs
#   utility-jobs             Backup, restore, cleanup jobs (IKS/kubernetes agent only)
#   mirror-openshift-release OCP build mirror job (needed by both PowerVC and PowerVS)
#   sync-jenkins-jobs        Jenkins job sync (NOT loaded to Upstream/prod Jenkins)
#
# MODES:
#   WHAT=""  (empty)          Load everything under jobs/pipelines/
#   WHAT="powervm"            Load only the powervm subtree
#   WHAT="powervs utility-jobs mirror-openshift-release"
#                             Load multiple subtrees (Upstream Jenkins)
#   WHAT="powervm/ocp/4.23/daily-ocp4.23-powervm-p9-min/Jenkinsfile"
#                             Load a single specific job (manual one-off)
#
# EXAMPLES:
#   # Load all jobs (original behaviour)
#   JENKINS_USER=admin JENKINS_PASSWORD=token ./hack/load-jenkins-pipelines.sh
#
#   # Load only PowerVC jobs (PowerVC Jenkins)
#   WHAT="powervm mirror-openshift-release sync-jenkins-jobs" ./hack/load-jenkins-pipelines.sh
#
#   # Load only PowerVS jobs (Upstream Jenkins — sync-jenkins-jobs intentionally excluded)
#   WHAT="powervs utility-jobs mirror-openshift-release" ./hack/load-jenkins-pipelines.sh
#
#   # Load a single job (manual fix)
#   WHAT="powervm/ocp/4.23/daily-ocp4.23-powervm-p9-min/Jenkinsfile" ./hack/load-jenkins-pipelines.sh

set -o errexit
set -o nounset
set -o pipefail

JENKINS_URI="${JENKINS_URI:-}"
if [[ -z "${JENKINS_URI}" ]]; then
    echo "ERROR: JENKINS_URI is required. Set it to the target Jenkins URL." >&2
    exit 1
fi

# Write the Jenkins URL into jenkins_jobs.ini so JJB targets the correct instance.
crudini --set /etc/jenkins_jobs/jenkins_jobs.ini jenkins url "${JENKINS_URI}"

WHAT="${WHAT:-}"
# Normalize: strip leading "jobs/pipelines/" if accidentally passed as full path
# Works for single value or space-separated list
normalized=()
for w in ${WHAT}; do
    normalized+=("${w#jobs/pipelines/}")
done
WHAT="${normalized[*]}"

INFRA_ROOT="$(cd "$(dirname "${BASH_SOURCE}")/.." && pwd -P)"

# Determine mode:
#   empty WHAT               → load everything under jobs/pipelines/
#   WHAT ends in Jenkinsfile → single job file
#   WHAT is one or more directory names (space-separated) → load those subtrees
if [[ -z "${WHAT}" ]]; then
    pushd ${INFRA_ROOT}
    folders=(`find jobs/pipelines -name Jenkinsfile -print | sed "s|jobs/pipelines/||g" | sed "s|/Jenkinsfile||" | xargs -I"{}" dirname {} | uniq`)
    jenkinsfiles=(`find jobs/pipelines -name Jenkinsfile -print`)
    popd
elif [[ "${WHAT}" == *Jenkinsfile ]]; then
    # Single Jenkinsfile path — derive its parent folder chain and use only that file
    pushd ${INFRA_ROOT}
    folders=(`echo "jobs/pipelines/${WHAT}" | sed "s|jobs/pipelines/||g" | sed "s|/Jenkinsfile||" | xargs -I"{}" dirname {}`)
    jenkinsfiles=("jobs/pipelines/${WHAT}")
    popd
else
    # One or more directory names — loop over each, collect all folders and jenkinsfiles
    pushd ${INFRA_ROOT}
    folders=()
    jenkinsfiles=()
    for dir in ${WHAT}; do
        folders+=(`find jobs/pipelines/${dir} -name Jenkinsfile -print | sed "s|jobs/pipelines/||g" | sed "s|/Jenkinsfile||" | xargs -I"{}" dirname {} | uniq`)
        jenkinsfiles+=(`find jobs/pipelines/${dir} -name Jenkinsfile -print`)
    done
    popd
fi

echo "Creating Folders: ${folders[@]}"
TMP_DIR=$(mktemp -d)
for folder in "${folders[@]}"
do
	IFS=/
        JOB_FOLDER=""
        file_name=""
	for i in $folder
	do
        if [[ "$folder" != "." ]]
        then
            unset IFS
            echo $i
            JOB_FOLDER+=$i
            file_name+=$i
            echo "{\"JOB_FOLDER\":${JOB_FOLDER}}" | jinja2 ${INFRA_ROOT}/hack/jjb_template_folder.jinja2 > ${TMP_DIR}/$file_name.yml
            JOB_FOLDER+='/'
            echo $JOB_FOLDER
            IFS=/
        fi
	done
	unset IFS
done

jenkins-jobs --user ${JENKINS_USER} --password ${JENKINS_PASSWORD} update ${TMP_DIR}

echo "Loading pipelines: ${jenkinsfiles[@]}"
TMP_DIR=$(mktemp -d)
for jenkinsfile in "${jenkinsfiles[@]}"
do
    JENKINS_FILE=${jenkinsfile}
    DIR_NAME="$(dirname "${jenkinsfile}")"
    JOB_NAME=$(dirname ${INFRA_ROOT}/${jenkinsfile}| xargs basename)
    JOB_FOLDER=$(echo $DIR_NAME | sed "s|jobs/pipelines/||g" | sed "s|/$JOB_NAME||g")
    if [[ "$JOB_FOLDER" = "$JOB_NAME" ]]; then JOB_PATH="${JOB_NAME}" ;else JOB_PATH="${JOB_FOLDER}/${JOB_NAME}"; fi
    echo "{\"JOB_NAME\":${JOB_PATH},\"JENKINS_FILE\":${JENKINS_FILE}}" | jinja2 ${INFRA_ROOT}/hack/jjb_template.jinja2 > ${TMP_DIR}/${JOB_NAME}.yml
done

jenkins-jobs --user ${JENKINS_USER} --password ${JENKINS_PASSWORD} update ${TMP_DIR}
