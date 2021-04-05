#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

INFRA_ROOT=$(dirname "${BASH_SOURCE}")/..

# Some useful colors.
if [[ -z "${color_start-}" ]]; then
  declare -r color_start="\033["
  declare -r color_red="${color_start}0;31m"
  declare -r color_yellow="${color_start}0;33m"
  declare -r color_green="${color_start}0;32m"
  declare -r color_norm="${color_start}0m"
fi

# Excluded check patterns are always skipped.
EXCLUDED_PATTERNS=(
  "verify-all.sh"                # this script calls the make rule and would cause a loop
  )

EXCLUDED_CHECKS=$(ls ${EXCLUDED_PATTERNS[@]/#/${INFRA_ROOT}\/hack\/} 2>/dev/null || true)

function is-excluded {
  for e in ${EXCLUDED_CHECKS[@]}; do
    if [[ $1 -ef "$e" ]]; then
      return
    fi
  done
  return 1
}


function run-cmd {
  if ${SILENT}; then
    "$@" &> /dev/null
  else
    "$@"
  fi
}

FAILED_TESTS=()

function print-failed-tests {
  echo -e "========================"
  echo -e "${color_red}FAILED TESTS${color_norm}"
  echo -e "========================"
  for t in ${FAILED_TESTS[@]}; do
      echo -e "${color_red}${t}${color_norm}"
  done
}

function run-checks {
  local -r pattern=$1
  local -r runner=$2

  for t in $(ls ${pattern})
  do
    if is-excluded "${t}" ; then
      echo "Skipping ${t}"
      continue
    fi
    echo -e "Verifying ${t}"
    local start=$(date +%s)
    run-cmd "${runner}" "${t}" && tr=$? || tr=$?
    local elapsed=$(($(date +%s) - ${start}))
    if [[ ${tr} -eq 0 ]]; then
      echo -e "${color_green}SUCCESS${color_norm}  ${t}\t${elapsed}s"
    else
      echo -e "${color_red}FAILED${color_norm}   ${t}\t${elapsed}s"
      ret=1
      FAILED_TESTS+=(${t})
    fi
  done
}

ret=0
SILENT=false
run-checks "${INFRA_ROOT}/hack/verify-*.sh" bash

if [[ ${ret} -eq 1 ]]; then
    print-failed-tests
fi
exit ${ret}