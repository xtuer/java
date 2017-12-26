有两个子工程 core 和 web:

* core: 定义通用的 Bean, Mapper, Service 等，被其他工程使用，例如 web，导入导出类等，避免重复的类在不同工程中重复定义

* web: 定义 Controller，URL 等和 web 相关的

  * 运行: `gradle :web:clean :web:appStart`
  * 打包: `gradle :web:clean :web:build`

  ​