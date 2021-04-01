#!/bin/bash

JENKINS_URI="${JENKINS_URI:-http://localhost:8080/}"

crudini --set /etc/jenkins_jobs/jenkins_jobs.ini jenkins url ${JENKINS_URI}

exec "$@"
