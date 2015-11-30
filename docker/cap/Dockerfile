# Version : 0.0.1
FROM ubuntu:14.04
MAINTAINER Shengxi Lee "lee5hx@163.com"
RUN mv /etc/apt/sources.list /etc/apt/sources.list.bak_1
ADD apt-source/sources.list /etc/apt/
RUN apt-get -y update
RUN apt-get -y install openjdk-7-jre openjdk-7-jdk
RUN mkdir /CAP



