> Docker本身并不是容器，它是创建容器的工具，是应用容器引擎。
#### CentOS 安装 Docker
1. yum install -y yum-utils device-mapper-persistent-data lvm2
2. yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
3. yum makecache fast
4. yum -y install docker-ce
5. service docker start
6. docker version
7. 配置阿里云镜像加速器（容器镜像服务）
#### 常用命令
| 命令                                          | 说明                           |
|---------------------------------------------|------------------------------|
| docker info                                 |                              |
| docker version                              |                              |
| docker build -t 机构/镜像名<:tags>  Dockerfile目录 |                              |
| docker pull 镜像名<:tags>                      | 从远程仓库拉取镜像                    |
| docker images                               | 查看本地镜像                       |
| docker create 镜像名<:tags>                    | 创建容器                         |
| docker rmi <-f> 镜像名<:tags>                  | 删除镜像                         |
| docker run 镜像名<:tags>                       | 创建容器 启动应用                    |
| docker run -p 8000:8080 -d tomcat           | 创建容器 启动应用                    |
| docker port 容器ID                            |                 |
| docker ps                                   | 查看正在运行容器                     |
| docker ps -a                                | 查看所有容器                       |
| docker container ls -a                      |                        |
| docker container prune                      |                        |
| docker inspect 容器ID                         | 容器的元信息                       |
| docker exec [-it] 容器id 命令                   | exec 在对应容器中执行命令 -it采用交互方式执行命令 |
| docker start 容器ID                           | 启动容器                         |
| docker restart 容器ID                         | 重启容器                         |
| docker stop 容器ID                            | 停止容器 die状态                   |
| docker pause 容器ID                           |                              |
| docker unpause 容器ID                         |                              |
| docker kill 容器ID                            | 停止容器 新的进程                    |
| docker rm <-f> 容器ID                         | 删除容器                         |

1. 容器间单向通信
```
docker run -d --name database -ti centos /bin/bash
docker run -d --name web --link database tomcat:8.5.46-jdk8-openjdk
```
2. 容器间双向通信网桥
```
docker network ls
docker network create -d bridge my-bridge
docker network connect my-bridge web
```
3. 容器间数据共享 Volume
```
docker run --name 容器名 -v 宿主机路径:容器内挂载路径 镜像名
docker run --name t1 -v /usr/webapps:/usr/local/tomcat/webapps tomcat

创建共享容器
docker create --name webpage -v /werbapps:/usr/local/tomcate/webapps tomcate /bin/true
docker run --volumes-from webpage --name t1 -d tomcat
```
#### Dockerfile 指令
1. FROM - 基于基准镜像
   - FROM centos #基于centos:lastest
   - FROM scratch #不依赖任何基准镜像 base image
   - FROM tomcat:9.0.22-jdk8-openjdk
2. LABEL & MAINTAINER - 说明信息
   - MAINTAINER LiJingTang
   - LABEL version = "1.0"
   - LABEL description = "学习"
3. WORKDIR - 设置工作目录
   - WORKDIR /usr/local
   - WORKDIR /usr/local/新目录 #自动创建 尽量使用绝对路径
4. ADD & COPY - 复制文件
   - ADD hello / #复制到根路径
   - ADD test.tar.gz / #复制到根目录并解压
   - ADD 除了复制 还具备添加远程文件的功能
5. ENV - 设置环境常量
   - ENV JAVA_HOME /usr/local/jdk8
   - RUN ${JAVA_HOME}/bin/java -jar test.jar
6. EXPOSE - 暴露容器端口
   - EXPOSE 8080 #将容器内部8080端口暴露给物理机 docker run -p 8000:8080 tomcat
7. RUN & CMD & ENTRYPOINT
   - RUN 在Build构建命令时执行
   - ENTRYPOINT 容器启动时执行的命令
   - CMD 容器启动后执行默认的命令或参数
   ```
   RUN yum install -y vim #shell 命令格式 会创建子线程
   RUN ["yum", "install", "-y", "vim"] #Exec命令格式
   ```