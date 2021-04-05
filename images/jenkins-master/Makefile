all: build

VERSION?=0.1
PREFIX?=ppc64le-cloud
ARCH?=amd64
TEMP_DIR:=$(shell mktemp -d)

# Set default base image dynamically for each arch
ifeq ($(ARCH),amd64)
	BASEIMAGE?=jenkins/jenkins:latest
endif
ifeq ($(ARCH),ppc64le)
	BASEIMAGE?=ibmcom/cfc-jenkins-master-ppc64le:2.19.4-1.1
endif

build:
	# Copy the whole directory to a temporary dir and set the base image
	cp -r ./* $(TEMP_DIR)

	cd $(TEMP_DIR) && sed -i -e "s|BASEIMAGE|$(BASEIMAGE)|g" Dockerfile
	docker build --pull -t $(PREFIX)/jenkins-master-$(ARCH):$(VERSION) $(TEMP_DIR)
	rm -rf $(TEMP_DIR)
