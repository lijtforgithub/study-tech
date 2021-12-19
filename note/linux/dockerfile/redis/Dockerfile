FROM centos
RUN ["yum", "install", "-y", "gcc", "gcc-c++", "net-tools", "make"]
WORKDIR /usr/local
ADD redis-5.0.14.tar.gz .
WORKDIR /usr/local/redis-5.0.14/src
RUN make && make install
WORKDIR /usr/local/redis-5.0.14
EXPOSE 6379
CMD ["redis-server"]