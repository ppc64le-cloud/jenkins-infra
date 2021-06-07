FROM ubuntu:18.04
USER root
RUN apt update && apt-get install -y wget curl \
               && wget https://download.clis.cloud.ibm.com/ibm-cloud-cli/1.6.0/IBM_Cloud_CLI_1.6.0_amd64.tar.gz \
               && curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" \
               && install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl \
               && tar -xvzf "./IBM_Cloud_CLI_1.6.0_amd64.tar.gz" \
               &&  ./Bluemix_CLI/install \
               && ibmcloud update -f \
               && ibmcloud plugin install cloud-object-storage -f \
               && ibmcloud plugin install container-service -f
ENTRYPOINT ["/bin/bash"]
