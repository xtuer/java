package com.xtuer.controller;

/**
 * 集中管理 URI 和 VIEW
 */
public interface UriView {
    // 通用 URI 和 VIEW
    String FORWARD  = "forward:";
    String REDIRECT = "redirect:";
    String URI_404  = "/404";
    String VIEW_404 = "404.fm";

    // 登录注销
    String URI_LOGIN  = "/login";    // 登陆 URL
    String URI_LOGOUT = "/logout";   // 注销 URL
    String URI_DENY   = "/deny";     // 无权访问页面的 URL
    String VIEW_LOGIN = "login.fm"; // 登陆页面

    String URI_DEMO         = "/demo";
    String VIEW_DEMO        = "demo.fm";
    String URI_DEMO_MYBATIS = "/demo/{id}";

    // RESTful 风格获取资源的 URI 格式，变量名以 REST_ 开头，URI 放在 rest 下
    // 获取或修改 topic, question 资源的 RESTful 风格的 URL, 资源都用复数形方便式统一管理 URL
    String REST_TOPICS         = "/rest/topics";
    String REST_TOPICS_WITH_ID = "/rest/topics/{topicId}";
    String REST_QUESTIONS      = "/rest/topics/{topicId}/questions";
}
