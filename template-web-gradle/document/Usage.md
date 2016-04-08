# 使用模版创建 IDEA Gradle 工程
1. 复制 `Template-Web-Gradle` 到目的文件夹下，重命名，例如为 `Fox`
2. 修改 `settings.gradle` 中 rootProject.name = 'Fox'
3. 在 IDEA 的 Project 下导入 `Gradle Module`
4. 如果项目的 `contextPath` 不是 `/`，例如为 `fox`:
    1. 修改 build.gradle 中的 contextPath

        ```java
        gretty {
            port = 8080
            contextPath = 'fox'
            servletContainer = 'tomcat7'
        }
        ```
    2. 修改 config.groovy 中的 baseUrl

        ```
        baseUrl = '/fox'
        ```
    3. `RequestMapping 的映射不需要加 contextPath`, SpringMVC 会自己加上，即 contextPath 为不为 `/` 映射的 URL 都一样
    4. `Freemarker 模版，JSP 中的链接需要加上 contextPath`