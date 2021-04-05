TEST_INFRA_IMAGE ?= ppc64le-cloud/jenkins-infra:0.1
JENKINS_IMAGE ?= ppc64le-cloud/jenkins-master-amd64:0.1

all: load-jenkins-jobs

load-jenkins-jobs: build-jenkins-infra-image
	@docker run -ti --rm -v $(shell pwd):/jenkins-infra:Z -e "JENKINS_USER=$(JENKINS_USER)" -e "JENKINS_PASSWORD=$(JENKINS_PASSWORD)" -e "JENKINS_URI=$(JENKINS_URI)" --rm $(TEST_INFRA_IMAGE) /jenkins-infra/hack/load-all-job.sh

build-jenkins-infra-image:
	$(MAKE) --no-print-directory -C images/jenkins-infra all

build-jenkins-master:
	$(MAKE) --no-print-directory -C images/jenkins-master build

verify: build-jenkins-infra-image build-jenkins-master
	$(eval JENKINS_CONT_ID = $(shell ./hack/jenkins.sh docker_run $(JENKINS_IMAGE)))
	$(eval JENKINS_IPADDR = $(shell ./hack/jenkins.sh container_ip $(JENKINS_CONT_ID)))
	@sleep 30
	@docker run -ti --rm -v $(shell pwd):/jenkins-infra:Z -e "JENKINS_USER=admin" -e "JENKINS_PASSWORD=admin" -e "JENKINS_URI=http://$(JENKINS_IPADDR):8080/" --rm $(TEST_INFRA_IMAGE) /jenkins-infra/hack/verify-all.sh
	./hack/jenkins.sh container_rm $(JENKINS_CONT_ID)
