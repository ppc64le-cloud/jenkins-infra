FROM jenkins/inbound-agent:4.6-1
USER root
RUN apt-get update && apt install curl gnupg2 --yes \
        python3 \
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
RUN pip3 install --upgrade pip ; pip3 install python-openstackclient; pip3 install ansible
ENV PATH $GOPATH/bin:/usr/local/go/bin:$PATH
RUN ssh-keygen -t rsa -N "" -f ~/.ssh/id_rsa
ENV VERSION="0.8.0"
RUN wget https://github.com/containerd/nerdctl/releases/download/v$VERSION/nerdctl-$VERSION-linux-amd64.tar.gz
RUN sudo tar zxvf  nerdctl-$VERSION-linux-amd64.tar.gz -C  /usr/local/bin/
RUN rm -f nerdctl-$VERSION-linux-amd64.tar.gz
RUN sed -i '2 a ip link set dev eth0 mtu 1420' /usr/local/bin/jenkins-agent
