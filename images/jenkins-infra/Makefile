all: build

VERSION?=0.1
PREFIX?=ppc64le-cloud

build:
	docker build --pull -t $(PREFIX)/jenkins-infra:$(VERSION) .
	rm -rf $(TEMP_DIR)
