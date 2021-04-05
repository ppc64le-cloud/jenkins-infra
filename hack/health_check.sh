#!/bin/bash
# -health check
if [ $1 == true ];then install_dir="/root" ;else install_dir="/opt/ibm";fi
echo -e "***** Getting cluster Info ICP Cluster *****"
inception=$(docker images | grep inception|tail -n 1 | awk '{print $3}')
[ -d ${install_dir}/cluster ] && cd ${install_dir}/cluster
docker run --net=host -t -e LICENSE=accept -v "$(pwd)":/installer/cluster ${inception} healthcheck || exit 0