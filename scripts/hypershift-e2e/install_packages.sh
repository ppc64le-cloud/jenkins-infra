#!/bin/sh

# This script install packages and set environment for hypershift

mkdir ${DUMP_DIR}

# Installing oc cli
curl https://mirror.openshift.com/pub/openshift-v4/clients/oc/latest/linux/oc.tar.gz -o oc.tar.gz
tar xzf oc.tar.gz
mv oc /usr/local/bin/
mv kubectl /usr/local/bin/

ibmcloud update -f
ibmcloud plugin update --all

apt update -y
apt install unzip -y

#Install Golang
curl https://dl.google.com/go/go"${GO_VERSION}".linux-amd64.tar.gz -o go"${GO_VERSION}".linux-amd64.tar.gz
tar -C /usr/local -xzf go"${GO_VERSION}".linux-amd64.tar.gz

#Pull Release image
mkdir ~/.docker
echo "${PULL_SECRET_CRED}" > ~/.docker/config.json
nerdctl pull ${RELEASE_IMAGE}
