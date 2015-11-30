# Version : 0.0.1
FROM ubuntu:14.04
MAINTAINER Shengxi Lee "lee5hx@163.com"
RUN mv /etc/apt/sources.list /etc/apt/sources.list.bak_1
ADD apt-source/sources.list /etc/apt/
RUN apt-get update
RUN apt-get install oracle-java7-installer

EXPOSE 80
