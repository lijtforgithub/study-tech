## Linux
| 命令 | 说明 |
|---|---|
| runlevel | 运行级别 |
| init 3 | 指定运行级别 |
| ntpdate ntp.api.bz | 同步服务器时间 |
| df -h | 查看磁盘空间 |
| df -h . | 查看当前文件夹下的所有文件大小（包含子文件夹） |
| service network restart | 重启网络 |
| netstat -nlp | |
| netstat -tunlp \| grep 端口号 | |
| ss -tanl | |
| lsof -i:端口号 | lsof -op $$ |
| service iptables status | 查看状态 |
| service iptables stop | 关闭 |
| chkconfig iptables --list | 查看开机启动状态 |
| chkconfig iptables off | 关闭开机启动 |
| chkconfig --list | 开机自动启动的服务 |
| chkconfig --add 服务名 | 添加开机自动启动服务 |
| chkconfig --del 服务名 | 删除开机自动启动服务 |

```azure
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
```
#### Nginx
```
service nginx reload  
service nginx restart
```
#### 公钥
```
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa  
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys  
chmod 0600 ~/.ssh/authorized_keys
```
#### Hadoop
```
sbin/hadoop-daemon.sh start journalnode
bin/hdfs namenode -format
sbin/hadoop-daemon.sh start namenode
hdfs namenode -bootstrapStandby

bin/hdfs zkfc -formatZK
bin/hdfs start zkfc

sbin/start-dfs.sh

bin/hdfs dfs -mkdir -p /usr/file
bin/hdfs dfs -put /home/soft/jdk-8u131-linux-x64.tar.gz /usr/file

要在ResouceManager所在的机器上启动start-yarn.sh
 ./hadoop jar /home/wc.jar com.ljt.study.hadoop.mr.WordCount /lijt/input/wc/ /lijt/output/wc

启动hadfs,注意有的是在多个节点执行的。
hadoop-daemons.sh start journalnode
hadoop-daemon.sh start namenode（每个namenode都要执行）
hadoop-daemon.sh start zkfc（每个namenode都要执行）
hadoop-daemons.sh start datanode

启动yarn  
start-yarn.sh
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