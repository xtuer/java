## 初始化数据库：

* 创建新数据库：登录 mysql，执行 `CREATE DATABASE test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci`
* 创建数据库表：命令行进入 `dr-web/sql`，执行 `db-init.bat root root drauto`

## 开发
* 启动服务器端: gradle bootRun

## 线上

* 启动服务: java -jar -Dfile.encoding=UTF-8 demo-job.jar