package com.xtuer.controller;

/**
 * 集中管理 URL。
 *
 * 其实类名叫 Urls 不是很合适，基本都是 URI，但是对于大多数人来说 URL 更熟悉好记忆一些。
 * 还有少量变量不是 URI，例如 JSONP_CONTENT_TYPE，FORWARD 等，但不多，为了减少类，故就放在这里吧，约定好了就行。
 *
 * 1. 访问页面的 URI 的变量名以 PAGE_ 开头，表明这个 URI 是访问页面
 * 2. 页面对应的模版文件变量名以 FILE_ 开头，表明这个 URI 是文件的路径
 * 3. 操作资源 api 的变量名以 API_ 开头，使用 RESTful 风格
 */
public interface Urls {
    String JSONP_CONTENT_TYPE = "application/javascript;charset=UTF-8"; // JSONP 响应的 header

    // 通用
    String FORWARD    = "forward:";
    String REDIRECT   = "redirect:";
    String PAGE_404   = "/404";
    String FILE_404   = "404.fm";
    String FILE_ERROR = "error.fm";

    // 案例展示
    String PAGE_DEMO = "/demo";
    String FILE_DEMO = "demo/demo.fm";
    String PAGE_DEMO_MYBATIS  = "/demo/{id}";
    String PAGE_DEMO_UPLOAD   = "/demo/upload";
    String FILE_DEMO_UPLOAD   = "demo/upload.fm";
    String PAGE_DEMO_VALIDATE = "/demo/validate";

    // 登录注销
    String PAGE_LOGIN  = "/login";   // 登陆 URL
    String PAGE_LOGOUT = "/logout";  // 注销 URL
    String PAGE_DENY   = "/deny";    // 无权访问页面的 URL
    String FILE_LOGIN  = "login.fm"; // 登陆页面

    // API 使用 RESTful 风格，变量名以 API_ 开头，URI 以 /api 开头, 资源都用复数形式便于统一管理 URL。
    // 下面以操作 subject, question 资源的 RESTful 风格的 URL 为例:
    // 列出 question 有 2 个相关的 URL，一是列出所有的 questions 用 API_QUESTIONS，
    // 另一个是列出主题下的所有 questions 用 API_QUESTIONS_IN_SUBJECT。
    String API_SUBJECTS        = "/api/subjects";
    String API_SUBJECTS_BY_ID  = "/api/subjects/{subjectId}";
    String API_QUESTIONS       = "/api/questions";
    String API_QUESTIONS_BY_ID = "/api/questions/{questionId}";
    String API_QUESTIONS_IN_SUBJECT = "/api/subjects/{subjectId}/questions";
}
