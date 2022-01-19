#### 安装基础软件
- 配置Master和Node节点的域名
```
vi /etc/hosts

192.168.99.101 master
192.168.99.102 node1
192.168.99.103 node2
```
- CPU设置为2核
- 下载阿里云的yum源repo文件 
`curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo`
- 安装基本软件包
`yum install wget net-tools ntp git ‐y`
- 同步系统时间 `napdate 0.asia.pool.ntp.org`
- 配置Docker k8s的阿里云yum源
```
cat >>/etc/yum.repos.d/kubernetes.repo <<EOF
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF
```
- 确保 br_netfilter 模块被加载。 通过运行 lsmod | grep br_netfilter 来确认br_netfilter已经加载。若要加载该模块，可执行 sudo modprobe br_netfilter。
```
cat <<EOF | sudo tee /etc/modules-load.d/k8s.conf
br_netfilter
EOF
```
- 将桥接的IPv4流量传递到iptables 为了让你的 Linux 节点上的 iptables 能够正确地查看桥接流量，你需要确保在你的 sysctl 配置中将 net.bridge.bridge-nf-call-iptables 设置为 1
```
cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sudo sysctl --system
```
- 关闭防火墙
```
systemctl stop firewalld
systemctl disable firewalld
```
- 关闭SeLinux
```
setenforce 0
sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config
```
- 关闭swap
```
swapoff -a
yes | cp /etc/fstab /etc/fstab_bak

vi /etc/fstab 注释下面这行
#/dev/mapper/centos-swap swap                    swap    defaults        0 0
```
#### 安装Docker
- 添加基础软件
```
curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
yum install -y yum-utils device-mapper-persistent-data lvm2 wget
```
- 设置阿里云 Docker 的yum源,在课程代码根目录执行 `cp docs/Chapter2/docker-ce.repo /etc/yum.repos.d/docker-ce.repo`
- 查看仓库所有Docker版本 `yum list docker-ce --showduplicates | sort -r`
- 安装 `yum install docker-ce-19.03.11 -y`
- 启动并加入开机启动
```
systemctl start docker
systemctl enable docker
```
#### Master安装kubeadm, kubelet, kubectl
- 首先确保虚拟机的 CPU 为 2 核（Kubeadm init 要求 2 核）
- 修改docker配置文件，使用 systemd 作为 cgroup 的驱动（Kubeadm init 推荐）本课程基于Kubernetes1.19.3 版本进行搭建，大家在安装kubectl和kubeadm的时候一定指定这个版本，否则后续的实践可能出错。
```
mkdir /etc/docker


cat > /etc/docker/daemon.json <<EOF
{
  "exec-opts": ["native.cgroupdriver=systemd"],
  "registry-mirrors": ["https://registry.cn-hangzhou.aliyuncs.com"]
}
EOF


# Restart Docker
systemctl daemon-reload
systemctl restart docker
systemctl enable docker
```
- 安装kubelet kubeadm kubectl `yum install -y kubelet-1.19.3 kubeadm-1.19.3 kubectl-1.19.3`
- 配置开机启动 `systemctl enable kubelet`
#### 初始化Master节点
- 设置主机名 `hostnamectl set-hostname master`
- 初始化主节点
```
kubeadm init --kubernetes-version=1.19.2 \
--apiserver-advertise-address=192.168.99.101 \
--image-repository registry.aliyuncs.com/google_containers \
--service-cidr=10.1.0.0/16 \
--pod-network-cidr=10.244.0.0/16

此时会等待镜像拉取，网络 5M 的情况下 1 分钟能完成。
如果你用的是云环境，需要把apiserver-advertise-address改成主节点的内网 ip 地址。
如果启动失败提示：kubelet 没有启动，则可以启动 kubelet ，再执行kubeadm reset， init。

如果出现错误
[root@bogon kubeblog]# kubectl get nodes
The connection to the server localhost:8080 was refused - did you specify the right host or port?
执行命令
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```
- 配置KUBECONFIG 环境变量 `echo "export KUBECONFIG=/etc/kubernetes/admin.conf" >> ~/.bash_profile`
- 安装网络插件 Flannel（只需要在 Master 安装 flannel） `kubectl apply -f kubeblog/docs/Chapter4/flannel.yaml`
- 查看是否成功创建flannel网络
```
ifconfig |grep flan
flannel.1: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1450
```
#### Node节点加入
安装 kubeadm kubectl, kubelet 在 kubectl get node 显示 master 节点 ready 之后
```
kubeadm token create --print-join-command
kubeadm join 192.168.99.101:6443 --token mpukvb.xbo0ejl71yp44k5h     --discovery-token-ca-cert-hash sha256:39b7001fcbddacdee7e104e7d77c3f0b0f39436cc254c1c6d3cf9644f4842641
```