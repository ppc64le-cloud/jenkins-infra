#!/bin/bash

INFRA_ROOT="$(cd "$(dirname "${BASH_SOURCE}")/.." && pwd -P)"
TEST_INFRA_IMAGE=ppc64le-cloud/jenkins-infra-amd64:0.1

docker_run()
{
	DOCKER_IMAGE=$1
	docker run -tidP ${DOCKER_IMAGE}
}

container_rm()
{
	CONTAINER_ID=$1
	docker rm -f ${CONTAINER_ID}
}

container_ip()
{
	CONTAINER_ID=$1
	docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' ${CONTAINER_ID}
}

func_name="$1"; shift
eval ${func_name} $*
