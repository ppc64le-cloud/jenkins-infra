FROM ubuntu:18.04
USER root
RUN apt-get update && apt install curl gnupg2 --yes \
        python3-pip \
        make \
        unzip \
        jq \
        pkg-config \
        wget \
        git \
        curl \
	    sudo \
    && rm -rf /var/lib/apt/lists/*
RUN pip3 install python-openstackclient
RUN wget --quiet --no-check-certificate -O go.tgz https://golang.org/dl/go1.14.6.linux-amd64.tar.gz && \
	tar -C /usr/local/ -xzf go.tgz
ENV PATH $GOPATH/bin:/usr/local/go/bin:$PATH
#ENV GOPATH /go
#RUN go version && mkdir -p "$GOPATH/src" "$GOPATH/bin" && chmod -R 777 "$GOPATH"
#RUN go get -u github.com/IBM-Cloud/terraform-provider-ibm && \
#	cd $GOPATH/src/github.com/IBM-Cloud/terraform-provider-ibm && \
#	ls $GOPATH/bin && mkdir -p ~/.terraform.d/plugins/linux_amd64 && \
#	cp $GOPATH/bin/* ~/.terraform.d/plugins/linux_amd64/
RUN ssh-keygen -t rsa -N "" -f ~/.ssh/id_rsa