# 1、服务器部署

## 1.1、前提

`git`、`java`、`maven`、`mysql`、`nginx`、`node.js`

## 1.2、前端

1. 安装包
2. 构建

## 1.3、后端

### 1.3.1、打包

不执行测试，但会编译测试代码。

```bash
mvn clean package -DskipTests
```

完全跳过测试编译和执行。

```bash
mvn clean package -Dmaven.test.skip=true
```

### 1.3.2、启动

- `nohup`：后台执行（退出终端后程序继续运行）
- `-Xms`：初始内存
- `-Xmx`：最大内存
- `--spring.profiles.active=prod`：指定生产配置
- `--server.port=8080`：指定端口
- `2>&1`：将错误输出也重定向到同一个文件（app.log）
- `&`：后台运行

```bash
nohup java -Xms512m -Xmx1024m -jar ucbackend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod  --server.port=8080 > app.log 2>&1 &
```

## 1.4、Nginx

### 1.4.1、配置

```nginx
    server {
        # 监听端口和域名
        listen       80;
        # 不设置域名就写localhost，要设置记得更改hosts文件
        server_name  web.uc.com;
        
        # 前端打包文件的根目录
        root   /usr/share/nginx/dist;
        # 默认首页文件
        index  index.html index.htm;

        # 前端静态文件配置
        location / {
            # 请求http://www.services.com/user的时候，流程：
            # 1、查找/user文件
            # 2、查找/user/目录
            # 3、返回兜底的/index.html（一般为Home组件）
            try_files $uri $uri/ /index.html;
        }

        location /api/ {
            proxy_pass http://localhost:8080/;

            # 保存代理信息
            # 请求头转发。把原始域名带给后端（有些业务需要）。
            proxy_set_header Host $host;
            # 告诉后端：真实客户端IP是谁
            proxy_set_header X-Real-IP $remote_addr;
            # 记录经过了"哪些代理"
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            # 告诉后端是http还是https
            proxy_set_header X-Forwarded-Proto $scheme;

            # 跨域不是服务器限制，是浏览器限制
            # cors（以下配置，让Nginx告诉浏览器：允许什么样的访问，如何访问）
            
            # 预检请求：浏览器在跨域情况下，对"复杂请求"的安全确认（OPTIONS）
            #   什么是"复杂请求"？
            #   1、使用了特殊请求方法：PUT、DELETE、PATCH
            #   2、or 使用了非简单请求头
            #   3、Content-Type不属于简单类型（JSON数据之类）
            #   4、带了自定义Header
            if ($request_method = 'OPTIONS') {
                # CORS头（核心）：允许哪个域访问（谁来请求我，就允许谁访问）
                add_header 'Access-Control-Allow-Origin' $http_origin always;
                # 允许带登录信息（Cookie/Token）来访问我
                add_header 'Access-Control-Allow-Credentials' 'true' always;
                # 允许哪些请求方式
                add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, PATCH, OPTIONS' always;
                # 允许哪些请求头
                add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization' always;

                return 204;
            }

            # 正常请求转发
            add_header 'Access-Control-Allow-Origin' $http_origin always;
            add_header 'Access-Control-Allow-Credentials' 'true' always;
            add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, PATCH, OPTIONS' always;
            add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization' always;
        }

        # 错误页面配置
        # 后端服务器返回以下类服务器端错误时，Nginx不会直接显示默认的报错信息，而是返回一个自定义的/50x.html
        error_page   500 502 503 504  /50x.html;
        # 处理错误页面的/50x.html配置在html目录下
        location = /50x.html {
            root   html;
        }
    }
```

### 1.4.2、允许Nginx访问8080端口

`semanage` 命令属于 `policycoreutils-python-utils` 包，需要先安装。

```bash
sudo dnf install -y policycoreutils-python-utils
```

将`8080`端口添加到`HTTP`端口类型中，这是解决问题的核心命令。它告诉`SELinux`，`httpd`（包括`Nginx`）进程可以通过`TCP`协议访问 `8080`端口。

```bash
sudo semanage port -a -t http_port_t -p tcp 8080
```

验证端口是否添加成功。执行下面的命令，确认输出列表中包含 `8080`。

```bash
sudo semanage port -l | grep http_port_t
```

允许`Nginx`发起网络连接。这个布尔值开关控制`Nginx`是否可以作为客户端向外网（或你的后端服务所在的任何网络地址）发起请求。对于反向代理场景，这几乎是必须的。

```bash
sudo setsebool -P httpd_can_network_connect 1
```

重启`Nginx`并验证。`SELinux`策略修改是即时生效的，但重启`Nginx`可以让它干干净净地用新权限重新加载。

```bash
sudo systemctl restart nginx
```

## 1.5、其他

查看正在运行的`Java`进程。

- `a`：显示所有用户进程
- `u`：显示使用该进程的用户和CPU/内存使用情况
- `x`：显示没有控制终端的进程（后台进程）
- `ps`：process status（进程状态）
- `-e`：相当于`-A`：所有进程
- `-f`：full format（完整格式）

```bash
ps aux | grep java
#或者
ps -ef | grep java
#或者
jps
```

杀掉进程。

```bash
kill -9 PID
```

查看哪个程序正在占用80端口。

- `-t`：只看TCP
- `-l`：只看正在监听（等待连接）的端口
- `-n`：显示数字端口号（不解析成服务名）
- `-p`：显示正在占用的进程ID和名字

```bash
netstat -tlnp | grep :80
```

开放端口。

```bash
# 开放 TCP 端口（以 8080 为例）
sudo firewall-cmd --permanent --add-port=8080/tcp
# 重新加载配置（使生效）
sudo firewall-cmd --reload
```

移除端口。

```bash
sudo firewall-cmd --permanent --remove-port=8080/tcp
sudo firewall-cmd --reload
```

查看端口。

```bash
sudo firewall-cmd --list-ports
```

查看所有规则（端口、服务）。

```bash
sudo firewall-cmd --list-all
```

# 2、容器部署

## 2.1、前提

1. 安装Docker；
2. 启动Docker：`sudo systemctl start docker` ；
3. 设置开机自启：`sudo systemctl enable docker` ；
4. 验证：`docker --version` ；
5. 测试：`sudo docker run hello-world` 。

## 2.2、创建网络

```bash
# 创建
docker network create ucnetwork
# 所有网络
docker network ls
# 查看指定网络
docker network inspect ucnetwork
```

## 2.3、前端容器

反向代理配置中的`HOST`需要指定后端容器名。

```nginx
        location /api/ {
            proxy_pass http://ucbackend-container:8080/;
            
            ...
		}
```

前端镜像文件。

```dockerfile
FROM nginx:alpine

# 删除默认配置
RUN rm -rf /usr/share/nginx/dist/*

# 拷贝前端文件
COPY dist/ /usr/share/nginx/dist/

# 可选：自定义 nginx.conf
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
```

编译打包。

具体编译打包命令查看`package.json` 。

```bash
npm run build
```

创建镜像、运行容器。

```bash
# .表示创建镜像所需的上下文环境=当前目录
# ucfrontend/dist
# ucfrontend/nginx.conf
# ucfrontend/Dockerfile
# ucfrontend/...
docker build -t ucfrontend:1.0 .
docker run -d -p 80:80 --name ucfrontend-container --network ucnetwork ucfrontend:1.0
# 重启容器如果出现无法连上Docker网络而启动失败的情况，需要重新指定之后再启动容器
docker network connect ucnetwork ucfrontend-container
docker start ucfrontend-container
```

## 2.4、后端容器

如果连接虚拟机数据库的情况，数据库连接指向虚拟机`IP`。

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    username: root
    password: 12345678
    url: jdbc:mysql://192.168.182.131:3306/uccenter
```

后端镜像文件。

```dockerfile
# 拉取OpenJDK17精简版（体积更小）作为基础运行环境
FROM eclipse-temurin:17-jdk-jammy

# 作者（可选）
# LABEL maintainer="you"

# 工作目录
WORKDIR /app

# 拷贝jar
COPY target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java","-Xms512m","-Xmx1024m","-jar","app.jar","--spring.profiles.active=prod2mysql"]
```

编译打包。

```bash
mvn clean package -DskipTests
```

创建镜像、运行容器。

```bash
# .表示创建镜像所需的上下文环境=当前目录
# ucbackend/src
# ucbackend/target
# ucbackend/Dockerfile.prod2mysql
# ucbackend/...
docker build -f Dockerfile.prod2mysql -t ucbackend-prod2mysql:1.0 .
docker run -d -p 8080:8080 --name ucbackend-prod2mysql-container --network ucnetwork ucbackend-prod2mysql:1.0
```

后端容器在连接数据库出现问题的时候，首先进入后端容器检查对数据库的网络连接是否正常。

```bash
telnet 192.168.182.131 3306
# 或者
nc -zv 192.168.182.131 3306
```

如果不正常的话，检查数据库的`root`用户是否对所有`IP`开放（%）。

```bash
mysql -uroot -p
select user, host from mysql.user;
mysql> select user, host from mysql.user;
+------------------+-----------+
| user             | host      |
+------------------+-----------+
| root             | %         |
| mysql.infoschema | localhost |
| mysql.session    | localhost |
| mysql.sys        | localhost |
| root             | localhost |
+------------------+-----------+
5 rows in set (0.00 sec)
```

```sql
ALTER USER 'root'@'%' IDENTIFIED BY '新密码';
FLUSH PRIVILEGES;
```

以及数据库的配置文件中是否监听所有`IP`地址（0.0.0.0）。

```ini
bind-address = 0.0.0.0
```

如果正常的话，检查数据库的`root`用户的连接认证插件。

`MySQL 8` 默认使用：`caching_sha2_password`。但很多环境（尤其是容器 + `JDBC`）的情况下会导致认证失败。

```
mysql> select user, host, plugin from mysql.user;
+------------------+-----------+-----------------------+
| user             | host      | plugin                |
+------------------+-----------+-----------------------+
| root             | %         | mysql_native_password |
| mysql.infoschema | localhost | caching_sha2_password |
| mysql.session    | localhost | caching_sha2_password |
| mysql.sys        | localhost | caching_sha2_password |
| root             | localhost | caching_sha2_password |
+------------------+-----------+-----------------------+
5 rows in set (0.00 sec)
```

```sql
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '12345678';
FLUSH PRIVILEGES;
```

## 2.5、MySQL容器

如果不想连接虚拟机数据库的情况，后端容器的数据库连接指向`MySQL`容器。

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    username: root
    password: 12345678
    url: jdbc:mysql://mysql-container:3306/uccenter
```

创建镜像、运行容器。

```bash
# 如果虚拟机的MySQL服务正在运行的话，需要先关停，否则容器创建后会启动失败
systemctl stop mysqld
# 运行容器（如果本地没有mysql:8.0镜像的话会自动拉取）
docker run -d --name mysql-container --network ucnetwork -p 3306:3306 -e MYSQL_ROOT_PASSWORD=12345678 -e MYSQL_DATABASE=uccenter -v /ryualvin/UserCenter/ucbackend/sql/uccenter.sql:/docker-entrypoint-initdb.d/init.sql mysql:8.0
```

## 2.6、其他

### 2.6.1、镜像操作

```bash
# 查看所有镜像
docker images
docker image list
docker rmi [-f] 镜像名
# 查看镜像详细信息
docker inspect 镜像名
```

### 2.6.2、容器操作

```bash
# 查看容器日志
docker logs [-f] 容器名
# 进入容器
# 运行 bash 命令，前提是容器里有 bash
docker exec -it 容器名 bash
# 直接指定 bash 路径
docker exec -it 容器名 /bin/sh
# 使用最基础的 shell
docker exec -it 容器名 /bin/bash
# 查看所有容器
docker ps -a
# 查看容器详细信息
docker inspect 容器名
# 启动、停止、重启容器
docker start 容器名
docker stop 容器名
docker restart 容器名
# 关停所有容器
docker stop $(docker ps -q)
# 删除容器
docker rm [-f] 容器名
# 修改容器网络
docker network connect 网络名 容器名
```
