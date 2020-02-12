## MySQL

1. 安装 MySQL
2. 创建数据库
   * 数据库名: jzm，编码 utf8mb4
   * 用户: root
   * 密码: root

3. 初始化数据库表: 命令行进入文件夹 sql，执行 `db-init.bat root root jzm`
4. 启动 MySQL

## Redis

1. 安装 Redis
2. 启动 Redis

## 启动程序

命令行执行 `java -jar -Dfile.encoding=UTF-8 jzm.jar --spring.profiles.active=win`

## 访问

1. 浏览器访问 <http://localhost:8080>
2. 登录:
   * 用户: admin
   * 密码: admin