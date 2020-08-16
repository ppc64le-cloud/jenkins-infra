FROM ubuntu:18.04
USER root
RUN apt update && apt install curl gnupg2 -y
RUN curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -
RUN echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | tee -a /etc/apt/sources.list.d/kubernetes.list
RUN echo "deb https://deb.nodesource.com/node_8.x xenial main" | tee /etc/apt/sources.list.d/node.list
RUN curl -s https://deb.nodesource.com/gpgkey/nodesource.gpg.key | apt-key add -
RUN apt-get update && apt install --yes nodejs imagemagick gifsicle \
        imagemagick \
        openjdk-8-jre \
        libtest-www-selenium-perl \
        firefoxdriver \
        ruby-childprocess \
        python3-pip \
        make \
        unzip \
        jq \
        pkg-config \
        wget \
        git \
        curl \
        kubectl \
        python-selenium \
	    sudo \
    && rm -rf /var/lib/apt/lists/*
RUN pip3 install python-openstackclient
RUN pip3 install ansible
RUN ssh-keygen -t rsa -N "" -f ~/.ssh/id_rsa

