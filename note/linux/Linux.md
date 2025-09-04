## Linux

47.103.86.114 node01 
47.122.0.148 node02 
root/!Plijt0828

总核数 = 物理CPU个数 * 每颗物理CPU的核数  
总逻辑CPU数 = 物理CPU个数 * 每颗物理CPU的核数 * 超线程数
```
查看物理CPU个数
cat /proc/cpuinfo | grep "physical id" | sort | uniq | wc -l
查看每个物理CPU中core的个数(即核数)
cat /proc/cpuinfo | grep "cpu cores" | uniq
查看逻辑CPU的个数
cat /proc/cpuinfo | grep "processor" | wc -l
查看CPU信息（型号）
cat /proc/cpuinfo | grep name | cut -f2 -d: | uniq -c
一个文件夹多少个文件
ls -l | grep "^-" | wc -l
查找包含指定key的文件
grep -r key ./
同步时间
yum install ntpdate
ntpdate ntp.aliyum.com
clock -w
hwclock -s

一、防火墙的开启、关闭、禁用命令

（1）设置开机启用防火墙：systemctl enable firewalld.service

（2）设置开机禁用防火墙：systemctl disable firewalld.service

（3）启动防火墙：systemctl start firewalld

（4）关闭防火墙：systemctl stop firewalld

（5）检查防火墙状态：systemctl status firewalld

二、使用firewall-cmd配置端口

（1）查看防火墙状态：ex

（2）重新加载配置：firewall-cmd --reload

（3）查看开放的端口：firewall-cmd --list-ports

firewall-cmd --zone=public --add-port=9200/tcp --permanent
firewall-cmd --zone=public --remove-port=80/tcp --permanent
```

| 命令                                             | 说明                                           |
| ------------------------------------------------ | ---------------------------------------------- |
| uname -a                                         | OS版本                                         |
| ip 4 -a                                          | 查看所有IP                                     |
| cat /proc/version                                | OS                                             |
| runlevel                                         | 运行级别                                       |
| init 3 / systemctl set-default multi-user.target | 指定运行级别                                   |
| ntpdate ntp.api.bz                               | 同步服务器时间                                 |
| df -h                                            | 查看磁盘空间                                   |
| df -h .                                          | 查看当前文件夹下的所有文件大小（包含子文件夹） |
| service network restart                          | 重启网络                                       |
| netstat -nlp                                     |                                                |
| netstat -tunlp \                                 | grep 端口号                                    |
| ss -tanl                                         |                                                |
| lsof -i:端口号                                   | lsof -op $$                                    |
| service iptables status                          | 查看状态                                       |
| service iptables stop                            | 关闭                                           |
| chkconfig iptables --list                        | 查看开机启动状态                               |
| chkconfig iptables off                           | 关闭开机启动                                   |
| chkconfig --list                                 | 开机自动启动的服务                             |
| chkconfig --add 服务名                           | 添加开机自动启动服务                           |
| chkconfig --del 服务名                           | 删除开机自动启动服务                           |
| scp -r ./hadoop/ root@node01:`pwd`               | 下发文件                                       |
| telnet                                           |                                                |
| Tcpdump -i any host                              |                                                |



```shell
useradd hadoop
passwd -S hadoop
passwd hadoop
id hadoop
groups hadoop

groupadd hpGroup
usermod -a -G hpGroup hadoop

cat /etc/group | grep hpGroup
getent group hpGroup

useradd -g hpGroup h1
# 附加用户组
useradd -G hpGroup h2
userdel h1
groupdel hpGroup
```



```
exec 8<> /dev/tcp/wwww.baidu.com/80
echo -e "GET / HTTP/1.0\n" >& 8
cat <& 8
exec 8<& -

nc www.baidu.com 80
GET / HTTP/1.0
    
strace -ff -o out java BIOTest

tcpdump -nn -i eth0 port 80

head -10 | tail -1

route -n
arp -a

type whereis
whereis ifconfig
rpm -qf /usr/sbin/ifconfig
yum install -y pkg

sed -i 's#${user.home}#/var/rocketmq#g' *.xml
```
#### Nginx
```
service nginx reload  
service nginx restart
nginx -t
```
#### 公钥
```
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa  
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys  
chmod 0600 ~/.ssh/authorized_keys
ssh-copy-id
```
## CMD
| 命令 | 说明 |
|---|---|
| start | 新打开一个 cmd 命令窗口 |
| exit | 退出 cmd 命令窗口 |
| cls | 清空命令窗口 |
| cd / | 切换到当前磁盘的根目录 |
| cd .. | 回到上一级目录 |
| cd /* | 切换到*文件夹 |
| D：| 切换到D盘 |
| ipconfig| ip信息 |
| echo %path% | 打印path |
| set path=新的路径;%path%| |
| set | 查看本机的所有环境变量 |
| set 变量名 | 查看一个具体的环境变量 |
| set 变量名= | 清空一个环境变量 |
| set 变量名=具体值 | 给指定变量定义具体值 |
| set 变量名=具体值;%path% | 在原有环境变量基础上添加新值 |
| set 变量名=具体值 | 给指定变量定义具体值 |
> 系统变量公用，用户变量别的用户不能用-专用