FROM debian:jessie-backports
MAINTAINER Simão Martins "simao.martins@tecnico.ulisboa.pt"

RUN apt-get -qq -y update && \
  DEBIAN_FRONTEND=noninteractive apt-get install -y vim krb5-user \
    openafs-client openafs-krb5 openafs-modules-dkms apt-transport-https && \
  apt-get clean

# We need JDK and SBT because we are going to perform the tests inside this container
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list
RUN apt-get -qq -y update && \
  DEBIAN_FRONTEND=noninteractive apt-get install -y openjdk-8-jdk sbt && \
  apt-get clean

COPY init-script.sh configureAFSClient.sh configureKerberosClient.sh /tmp/ 
CMD /tmp/init-script.sh
