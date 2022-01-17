#### CentOS 安装k8s集群
###### 准备主机环境
```
1. 设置主机名与时区
    timedatectl set-timezone Asia/Shanghai  #都要执行
    hostnamectl set-hostname node20   #120执行
    hostnamectl set-hostname node21   #121执行
    hostnamectl set-hostname node22   #122执行
    
2. 添加hosts网络主机配置 三台虚拟机都要设置
    vi /etc/hosts
    192.168.3.120 node20
    192.168.3.121 node21
    192.168.3.122 node22
    
3. 关闭防火墙 三台虚拟机都要设置 生产环境跳过这一步  
   sed -i 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/selinux/config
   setenforce 0
   systemctl disable firewalld
   systemctl stop firewalld
```
###### 安装Kubeadmin加载K8S镜像
```
1. 将镜像包上传至服务器每个节点
mkdir /usr/local/k8s-install
cd /usr/local/k8s-install
把文件夹k8s-1.14上传到k8s-install

2. 每个Centos上安装Docker
cd /usr/local/k8s-install/k8s-1.14
tar -zxvf docker-ce-18.09.tar.gz
cd docker 
yum localinstall -y *.rpm
systemctl start docker
systemctl enable docker

3. 确保从cgroups均在同一个从groupfs
cgroups是control groups的简称，它为Linux内核提供了一种任务聚集和划分的机制，通过一组参数集合将一些任务组织成一个或多个子系统。   
cgroups是实现IaaS虚拟化(kvm、lxc等)，PaaS容器沙箱(Docker等)的资源管理控制部分的底层基础。
子系统是根据cgroup对任务的划分功能将任务按照一种指定的属性划分成的一个组，主要用来实现资源的控制。
在cgroup中，划分成的任务组以层次结构的形式组织，多个子系统形成一个数据结构中类似多根树的结构。cgroup包含了多个孤立的子系统，每一个子系统代表单一的资源

docker info | grep cgroup 

如果不是cgroupfs,执行下列语句

cat << EOF > /etc/docker/daemon.json
{
  "exec-opts": ["native.cgroupdriver=cgroupfs"]
}
EOF
systemctl daemon-reload && systemctl restart docker

4. 安装kubeadm集群部署工具
cd /usr/local/k8s-install/k8s-1.14
tar -zxvf kube114-rpm.tar.gz
cd kube114-rpm
yum localinstall -y *.rpm

5. 关闭交换区
swapoff -a
vi /etc/fstab 
#swap一行注释

6. 配置网桥
cat <<EOF >  /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sysctl --system

7. 通过镜像安装k8s
cd /usr/local/k8s-install/k8s-1.14
docker load -i k8s-114-images.tar.gz
docker load -i flannel-dashboard.tar.gz
```
###### 利用Kubeadm部署K8S集群
```
1. master 主节点
kubeadm init --kubernetes-version=v1.14.1 --pod-network-cidr=10.244.0.0/16

mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

kubectl get nodes
#查看存在问题的pod
kubectl get pod --all-namespaces
#设置全局变量
#安装flannel网络组件
kubectl create -f kube-flannel.yml

2. node 节点执行 执行第一步的时候会生成 复制出来
kubeadm join 192.168.3.120:6443 --token uyqodh.g84m211rz7dccoxh \
    --discovery-token-ca-cert-hash sha256:50f3cc7ed187be73cf29e6a25ab2a9d0ca0f201a0eeba4d4aafa057ac0dba23f 
	
如果忘记
在master 上执行kubeadm token list 查看 ，在node上运行
kubeadm join 192.168.163.132:6443 --token aoeout.9k0ybvrfy09q1jf6 --discovery-token-unsafe-skip-ca-verification

kubectl get nodes

3. Master开启仪表盘
kubectl apply -f kubernetes-dashboard.yaml
kubectl apply -f admin-role.yaml
kubectl apply -f kubernetes-dashboard-admin.rbac.yaml
kubectl -n kube-system get svc
http://192.168.163.132:32000 访问

4. 所有节点设置开机启动设置(systemctl list-unit-files)
systemctl enable kubelet
```
###### 安装 nfs
```
1. 安装文件服务器 master
yum install -y nfs-utils rpcbind

vi /etc/exports
/usr/local/k8s-install/www-data 192.168.3.120/24(rw,sync)

systemctl start nfs.service
systemctl start rpcbind.service
systemctl enable nfs.service
systemctl enable rpcbind.service

2. 其他节点 
yum install -y nfs-utils
#如果不成功 重启master rpcbind 再重启nfs
showmount -e 192.168.3.120

mount 192.168.3.120:/usr/local/k8s-install/www-data /mnt
systemctl enable nfs.service
```
###### Rinted
```
cd /usr/local
tar -zxvf rinetd.tar.gz
cd rinetd
sed -i 's/65536/65535/g' rinetd.c
mkdir -p /usr/man/
yum install -y gcc
make && make install

vi /etc/rinetd.conf
0.0.0.0 8000 10.106.100.167 8000
rinetd -c /etc/rinetd.conf
```
#### kubectl 常用命令
| 命令                                                                         | 说明               |
|----------------------------------------------------------------------------|------------------|
| kubectl cluster-info                                                       | 查看集群信息           |
| kubectl version                                                            |                  |
| kubectl api-versions                                                       |                  |
| kubectl top node                                                           |                  |
| kubectl top pod                                                            |                  |
| kubectl get nodes                                                          | 查看节点信息           |
| kubectl get namespace                                                      | 查看命名空间           |
| kubectl api-resources --namespaced=true                                    | 位于命名空间中的资源       |
| kubectl api-resources --namespaced=false                                   | 不在命名空间中的资源       |
| kubectl config set-context --current --namespace=<命名空间名称>                  | 设置名字空间偏好         |
| kubectl get rs --all-namespaces                                            |                  |
| kubectl get rc --all-namespaces                                            |                  |
| kubectl get service --all-namespaces                                       |                  |
| kubectl create -f yml文件                                                    | 创建部署             |
| kubectl apply -f yml文件                                                     | 更新部署配置           |
| kubectl get pod --all-namespaces                                           | 查看已部署的所有命名空间pod  |
| kubectl get pod [-o wide] [-o yaml]                                        | 查看已部署的pod        |
| kubectl get describe pod pod名称                                             | 查看pod详细信息        |
| kubectl logs [-f] pod名称                                                    | 查看pod输出日志        |
| kubectl delete deployment 或 service 名称                                     | 删除部署或服务          |
| kubectl delete po --all                                                    | 删除所有pod          |
| kubectl port-forward --address 0.0.0.0  pod/nginx-6ddbbc47fb-sfdcv 8888:80 | 端口映射本机8888映射容器80 |
| kubectl cp nginx-6ddbbc47fb-sfdcv:/etc/fstab /tmp  | 复制文件             |
| kubectl label namespaces default testing=true  | 为ns设置标签          |

```
kubectl config view | grep namespace:

kubectl scale deploy/bone-front --replicas=1 -n prod
kubectl delete deploy/deploy-name svc/deploy-name -n dev 
kubectl get pod --selector app=bone-app-200 -n test -o wide
kubectl get po -l 'app=gateway'
kubectl get pod -n prod -o wide
kubectl edit deployment/gateway
```
1. 创建一个svc时 Kubernetes 会创建一个相应的 DNS。形式是 <服务名称>.<命名空间>.svc.cluster.local