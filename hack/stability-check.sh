#!/usr/bin/env bash
yum install docker -y
echo "Disk space check"
df -kh
echo "Multipath output"
multipath -ll
if [ $? -ne 0 ]; then
  echo "Multipath not enabled"
  exit 1
fi
OCP_NET_GATEWAY_ADDRESS=$(netstat -rn|awk '(NR == 6) {print $2}')
echo "Ping to gateway address of private network ocp-net"
ping -c 4 "${OCP_NET_GATEWAY_ADDRESS}"
if [ $? -ne 0 ]; then
  echo "Private network communication not enabled"
  exit 1
fi
docker pull registry.access.redhat.com/ubi8/ubi
docker pull quay.io/baselibrary/ubuntu
docker pull gcr.io/google-containers/busybox
docker pull docker.io/nginx
echo "List of docker images:"
docker images
