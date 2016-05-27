## 目录结构

![](image/Project-Structure.png)

| 目录                  | 说明                  |
| -------------------- | -------------------- | ------------ |
| main/java            | 存放类文件             |
| main/resources       | 其他需要放到 classpath 下的配置文件, 如 logback 的配置 |
| main/resources/config | 配置文件，例如 Spring MVC, Spring Security 等的配置文件 |
| main/resources/mybatis-mapper | MyBatis 的映射文件 |
| /WEB-INF/view/jsp    | JSP 模版              |
| /WEB-INF/view/fm     | Freemarker 模版       |
| /WEB-INF/asset/js    | JavaScript 文件       |
| /WEB-INF/asset/css   | CSS 文件              |
| /WEB-INF/asset/img   | img                  |
| /WEB-INF/asset/lib   | 第三方的 js, css 等，例如 bootstrap, jquery |

## 文件说明
| 文件                  | 说明                  |
| -------------------- | -------------------- | ------------ |
| dao.xml              | 数据库访问的 Bean 的配置文件 |
| datasource.xml       | 数据源配置文件          |
| mybatis.xml          | MyBatis 配置文件       |
| spring-mvc.xml       | Spring MVC 配置文件    |
| web.xml              | Web 项目的配置文件      |

## 如何创建 Controller
1. 在 `com.xtuer.controller.UriViewConstants` 里定义 URI 和 View Name 为常量字符串变量

    > URI 和 View Name 不要直接写在 Controller 里，而是使用常量定义在 UriViewConstants 中，这样便于集中管理和查看，否则项目里提供了多少 URL 都不知道
2. Controller 定义在包 `com.xtuer.controller` 里
3. 如果 Controller 需要模版文件来渲染结果
    * Freemarker 模版文件放在 `/WEB-INF/view/fm`
    * JSP 文件放在 `/WEB-INF/view/jsp`
4. Controller 是用 Service，Service 直接使用 MyBatis 的 Mapper 访问数据库 (相当于 Dao)，


## URL 设计
* 普通 URL 没啥太多要求，但是不要以 `resources` 开头
* RESTful: 如果 URL 中以 `resources` 开头, 则说明访问的是 RESTful 的资源, 这样利于和页面的 URL 等区分开
而且 RESTful 资源的访问，响应的数据格式也应该是 JSON 格式，有利于规范编码，例如 `http://survey.edu-edu.com.cn/resources/topics`，更多例子参考 `UriViewConstants.java` 和 `constants.js`

    > 所有 URL 放在 3 个文件里: 
    > 
    > * UriViewConstants.java (服务器端: Controller 里引用, Controller 中不会直接写 URL 和 View Name)
    > * spring-view-controller.xml (服务器端: 静态页面)
    > * constants.js (html, js 中使用)


























