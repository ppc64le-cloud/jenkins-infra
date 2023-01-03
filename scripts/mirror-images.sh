#!/bin/bash


# Added 4hrs to 24hrs if any delay in imagestream creation
compare_time=$(date -d  "1128 hour ago" +%s)
public_repo=$(./oc get is release-ppc64le -n ocp-ppc64le -o=json | jq -r -c '.status.publicDockerImageRepository')
target_repo="sys-powercloud-docker-local.artifactory.swg-devops.com/ocp-ppc64le/release-ppc64le"

echo here is the public repo: $public_repo
for annotation in $(./oc get is release-ppc64le -n ocp-ppc64le -o=json | jq -c '.spec.tags[]'); do
    _jq() {
     echo ${annotation} | jq -r ${1}
    }

    creation_time=$(_jq '.annotations."release.openshift.io/creationTimestamp"')
    if [ "$creation_time" == "null" ]; then
       continue
    fi
    creation_timestamp=$(date -d ${creation_time} +%s)
    if [ ${creation_timestamp} -gt ${compare_time} ]; then
        tag=$(_jq '.name')
        echo $creation_time
        nerdctl pull ${public_repo}:${tag}
        nerdctl tag ${public_repo}:${tag} ${target_repo}:${tag}
        nerdctl push ${target_repo}:${tag}
    fi
done

# Pulling EC builds
curl https://ppc64le.ocp.releases.ci.openshift.org/api/v1/releasestream/4-dev-preview-ppc64le/latest > build.txt
ec_build=$(jq ".pullSpec"  build.txt |tr -d '"')
nerdctl pull $ec_build
ec_tag=$(jq ".name"  build.txt |tr -d '"')
nerdctl tag $ec_build ${target_repo}:$ec_tag
nerdctl push ${target_repo}:$ec_tag
