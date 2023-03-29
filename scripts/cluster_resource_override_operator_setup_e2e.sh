#!/bin/bash
while getopts "g:o:" opt
do
   case "$opt" in
      g ) GO_VERSION="$OPTARG" ;;
      o ) OCP_VERSION="$OPTARG" ;;
      ? ) echo "Wrong Args" ;;
   esac
done
PODMAN_PASSWORD=`cat podman_pass`
oc patch operatorhub.config.openshift.io/cluster -p='{"spec":{"disableAllDefaultSources":true}}' --type=merge
oc get secret/pull-secret -n openshift-config -o json | jq -r '.data.".dockerconfigjson"' | base64 -d > authfile

podman login --authfile ./authfile --username "|shared-qe-temp.src5.75b4d5" --password $PODMAN_PASSWORD
oc set data secret/pull-secret -n openshift-config --from-file=.dockerconfigjson=authfile

cat << EOF > ICSP.yaml
apiVersion: operator.openshift.io/v1alpha1
kind: ImageContentSourcePolicy
metadata:
  name: brew-registry
spec:
  repositoryDigestMirrors:
  - mirrors:
    - brew.registry.redhat.io
    source: registry.redhat.io
  - mirrors:
    - brew.registry.redhat.io
    source: registry.stage.redhat.io
  - mirrors:
    - brew.registry.redhat.io
    source: registry-proxy.engineering.redhat.com
EOF
oc create -f ICSP.yaml

cat << EOF > test-catalogsource.yaml
apiVersion: operators.coreos.com/v1alpha1
kind: CatalogSource
metadata:
  name: redhat-operators-stage
  namespace: openshift-marketplace
spec:
  sourceType: grpc
  publisher: redhat
  displayName: Red Hat Operators v$OCP_VERSION Stage
  image: quay.io/openshift-release-dev/ocp-release-nightly:iib-int-index-art-operators-$OCP_VERSION
EOF
oc create -f test-catalogsource.yaml

cat << EOF > cro-namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  labels:
       pod-security.kubernetes.io/audit: privileged
       pod-security.kubernetes.io/enforce: privileged
       pod-security.kubernetes.io/warn: privileged
       security.openshift.io/scc.podSecurityLabelSync: "false"
       kubernetes.io/metadata.name: clusterresourceoverride-operator
       name: clusterresourceoverride-operator
  name: clusterresourceoverride-operator
spec:
  finalizers:
  - kubernetes
EOF
oc create -f cro-namespace.yaml

cat << EOF > cro-og.yaml
apiVersion: operators.coreos.com/v1
kind: OperatorGroup
metadata:
  generateName: clusterresourceoverride-
  name: clusterresourceoverride-operator
  namespace: clusterresourceoverride-operator
spec:
  targetNamespaces:
    - clusterresourceoverride-operator
EOF
oc create -f cro-og.yaml


cat << EOF > cro-sub.yaml
apiVersion: operators.coreos.com/v1alpha1
kind: Subscription
metadata:
  name: clusterresourceoverride
  namespace: clusterresourceoverride-operator
spec:
  channel: stable
  installPlanApproval: Automatic
  name: clusterresourceoverride
  source: redhat-operators-stage
  sourceNamespace: openshift-marketplace
EOF
oc create -f cro-sub.yaml

sleep_time=60
count=0
for((i=0;i<10;++i)) do
  count=$(oc get pod -A | grep "clusterresource"  | grep "Running" | wc -l)
  if [ $count -eq 1 ]; then
    oc get pod -A | grep "clusterresource"  | grep "Running"
    echo "Operator pod is up and running"
    break
  else
    echo "sleeping for 1 min operator pod is not up"
    sleep $sleep_time
  fi
done
if [ $count -eq 0 ]; then
  oc get pod -A | grep "clusterresource"
  echo "Operator pod not up after 10 mins"
  exit 1
fi

cat << EOF > clusterresourceoverride-cr.yaml
apiVersion: operator.autoscaling.openshift.io/v1
kind: ClusterResourceOverride
metadata:
  name: cluster
spec:
  podResourceOverride:
    spec:
      memoryRequestToLimitPercent: 50
      cpuRequestToLimitPercent: 25
      limitCPUToMemoryPercent: 200
EOF
oc create -f clusterresourceoverride-cr.yaml

git clone  https://github.com/openshift/cluster-resource-override-admission-operator.git

wget https://go.dev/dl/go$GO_VERSION.linux-ppc64le.tar.gz
tar -C /usr/local -xzf go$GO_VERSION.linux-ppc64le.tar.gz
export GOPATH=/usr/local/go
export PATH=$PATH:$GOPATH/bin

cd cluster-resource-override-admission-operator
make e2e OPERATOR_NAMESPACE=clusterresourceoverride-operator KUBECONFIG=/root/openstack-upi/auth/kubeconfig 2>&1 | tee /root/cro_e2e_output.txt
