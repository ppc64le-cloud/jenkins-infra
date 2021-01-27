FROM ubuntu:16.04

ENV JENKINS_URI http://9.47.83.59:8080
ENV JENKINS_USER admin
ENV JENKINS_PASSWORD passw0rd

RUN apt-get update -y && apt-get install -y curl python python-pip libyaml-dev git crudini python-dev python-setuptools libffi-dev libssl-dev \
    && python -m pip install --upgrade "pip < 21.0" \
    && git clone https://git.openstack.org/openstack-infra/jenkins-job-builder \
    && cd jenkins-job-builder && pip install -r requirements.txt && python setup.py install \
    && pip install jinja2-cli

COPY jenkins_jobs.ini /etc/jenkins_jobs/
COPY entrypoint.sh /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
