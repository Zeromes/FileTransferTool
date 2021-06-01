# FileTransferTool

先运行接收端，再运行发送端

### 接收端

----

#### 参数

- 要存放文件的路径



### 发送端

----

#### 参数

- 接收方的ip地址或计算机名
- 要发送文件的路径



### 示例

---

在编译出的class文件目录下执行以下命令（注意：需要事先安装java环境）：

java FileReceiver c:/test

c:/test表示要储存文件的路径。

然后运行发送端：

java FileSender localhost c:/dir/file.txt

localhost为接收方地址（这里指本机），c:/dir/file.txt为要发送的文件路径。 

