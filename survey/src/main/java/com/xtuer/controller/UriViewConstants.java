package com.xtuer.controller;

public interface UriViewConstants {
    // 通用 URI 和 VIEW
    String FORWARD    = "forward:";
    String REDIRECT   = "redirect:";
    String URI_404    = "/404";
    String VIEW_404   = "404.htm";
    String SUCCESS    = "/success";
    String SUBMIT_SUCCESS = "/submit-success";

    // 登录注销
    String URI_LOGIN  = "/login";    // 登陆 URL
    String URI_LOGOUT = "/logout";   // 注销 URL
    String URI_DENY   = "/deny";     // 无权访问页面的 URL
    String VIEW_LOGIN = "login.htm"; // 登陆页面

    // Admin
    String  URI_ADMIN_TOPICS     = "/admin/topics"; // 显示 topic 的页面
    String VIEW_ADMIN_TOPICS     = "admin/topics.htm";
    String  URI_ADMIN_QUESTIONS  = "/admin/topics/{topicId}/questions"; // 编辑指定 topic 的 question 页面
    String VIEW_ADMIN_QUESTIONS  = "admin/questions.htm";

    // 获取或修改 topic, question 资源的 RESTful 风格的 URL, 资源都用复数形方便式统一管理 URL
    String URI_TOPICS            = "/topics";
    String URI_TOPICS_WITH_ID    = "/topics/{topicId}";
    String URI_QUESTIONS         = "/topics/{topicId}/questions";
    String URI_QUESTIONS_WITH_ID = "/topics/{topicId}/questions/{questionId}";
    String URI_QUESTIONS_ANSWERS = "/topics/{topicId}/questions/{questionId}/answers";
    String URI_QUESTIONS_ORDERS  = "/topics/{topicId}/questions/orders";
    String URI_TOPICS_ORDERS     = "/topics/orders";

    // 客户访问调查文件的 URL
    String  URI_SURVEYS         = "/surveys/{surveyId}";
    String VIEW_SURVEYS         = "survey.htm";
    String  URI_SURVEYS_SUBMIT  = "/surveys/{surveyId}/submit";


    ///////////////////////
    String URI_PARTICIPANT  = "/participant";
    String VIEW_PARTICIPANT = "participant.htm";
    String URI_PARTICIPANT_TELEPHONE_UNUSED = "/participant-telephone-unused";
    String URI_ADMIN_PARTICIPANTS = "/admin/participants";
    String URI_INNER_PARTICIPANTS = "/inner/participants";
    String URI_INNER_PARTICIPANTS_WITH_PAGE = "/inner/participants/{page}";
    String URI_INNER_PARTICIPANTS_PAGES_COUNT = "/inner/participants-pages-count";

    String URI_GIFT = "/gift";
    String URI_INNER_PARTICIPANT_GIFTS = "/inner/participant-gifts";
    String URI_INNER_PARTICIPANT_GIFTS_WITH_PAGE = "/inner/participant-gifts/{page}";
    String URI_INNER_PARTICIPANT_GIFTS_PAGES_COUNT = "/inner/participant-gifts-pages-count";
    String URI_PARTICIPANT_GIFT_DESCRIPTION ="/inner/participant-gift-description";
    String URI_ADMIN_PARTICIPANT_GIFTS = "/admin/participant-gifts";
    String VIEW_ADMIN_PARTICIPANT_GIFTS = "admin/participant-gifts.htm";
}
