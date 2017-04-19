package com.xtuer.controller;

/**
 * 集中管理 URI 和 VIEW
 */
public interface UriView {
    // 案例展示
    String URI_DEMO          = "/demo";
    String VIEW_DEMO         = "demo/demo.fm";
    String URI_DEMO_MYBATIS  = "/demo/{id}";
    String URI_DEMO_UPLOAD   = "/demo/upload";
    String VIEW_DEMO_UPLOAD  = "demo/upload.fm";
    String URI_DEMO_VALIDATE = "/demo/validate";

    // 通用 URI 和 VIEW
    String FORWARD    = "forward:";
    String REDIRECT   = "redirect:";
    String URI_404    = "/404";
    String VIEW_404   = "404.fm";
    String VIEW_ERROR = "error.fm";

    // 登录注销
    String URI_LOGIN  = "/login";    // 登陆 URL
    String URI_LOGOUT = "/logout";   // 注销 URL
    String URI_DENY   = "/deny";     // 无权访问页面的 URL
    String VIEW_LOGIN = "login.fm"; // 登陆页面

    // RESTful 风格获取资源的 URI 格式，变量名以 REST_ 开头，URI 放在 rest 下
    // 获取或修改 topic, question 资源的 RESTful 风格的 URL, 资源都用复数形式方便式统一管理 URL

    // 目录文件相关
    String REST_DIRECTORIES         = "/rest/directories"; // 目录, 过滤条件是父目录的 ID: parentDirectoryId
    String REST_DIRECTORIES_WITH_ID = "/rest/directories/{directoryId}";
    String REST_DIRECTORY_NAME      = "/rest/directories/{directoryId}/name";
    String REST_DIRECTORY_PARENT_ID = "/rest/directories/{directoryId}/parentId";
}
