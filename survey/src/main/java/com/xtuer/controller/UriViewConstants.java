package com.xtuer.controller;

/**
 * 前缀: URI, REST, VIEW
 *
 * URI:  普通 URL 的 URI
 * VIEW: 页面的路径
 * REST: RESTful 资源的 URI
 *
 * 如果 URI 中以 resources 开头, 则说明访问的是 RESTful 的资源, 这样利于和页面的 URI 等区分开
 * 而且 RESTful 资源的访问，响应的数据格式也应该是 JSON 格式，有利于规范编码
 */
public interface UriViewConstants {
    // 通用 URI 和 VIEW
    String FORWARD        = "forward:";
    String REDIRECT       = "redirect:";
    String URI_404        = "/404";
    String VIEW_404       = "404.htm";
    String SUCCESS        = "/success";
    String SUBMIT_SUCCESS = "/submit-success";

    // 登录注销
    String URI_LOGIN  = "/login";    // 登陆 URL
    String URI_LOGOUT = "/logout";   // 注销 URL
    String URI_DENY   = "/deny";     // 无权访问页面的 URL
    String VIEW_LOGIN = "login.htm"; // 登陆页面

    // Admin
    String  URI_ADMIN_TOPICS         = "/admin/topics"; // 显示 topic 的页面
    String VIEW_ADMIN_TOPICS         = "admin/topics.htm";
    String  URI_ADMIN_QUESTIONS      = "/admin/topics/{topicId}/questions"; // 编辑指定 topic 的 question 页面
    String VIEW_ADMIN_QUESTIONS      = "admin/questions.htm";
    String  URI_ADMIN_SURVEYS_RESULT = "/admin/surveys-result/{surveyId}";
    String VIEW_ADMIN_SURVEYS_RESULT = "admin/survey-result.htm";

    // 获取或修改 topic, question 资源的 RESTful 风格的 URL, 资源都用复数形方便式统一管理 URL
    String REST_TOPICS                        = "/resources/topics";
    String REST_TOPICS_WITH_ID                = "/resources/topics/{topicId}";
    String REST_QUESTIONS                     = "/resources/topics/{topicId}/questions";
    String REST_QUESTIONS_WITH_ID             = "/resources/topics/{topicId}/questions/{questionId}";
    String REST_QUESTIONS_ORDERS              = "/resources/topics/{topicId}/questions/orders";
    String REST_TOPICS_ORDERS                 = "/resources/topics/orders";
    String REST_QUESTIONS_ANSWERS_STATISTICS  = "/resources/topics/{topicId}/questions/answers/statistics";
    String REST_QUESTIONS_ANSWERS_SUGGESTIONS = "/resources/topics/{topicId}/questions/{questionId}/question-items/{questionItemId}/suggestions";

    // 客户访问调查问卷的 URL
    String  URI_SURVEYS        = "/surveys/{surveyId}";
    String VIEW_SURVEYS        = "survey.htm";
    String  URI_SURVEYS_SUBMIT = "/surveys/{surveyId}/submit"; // 提交问卷

    // 客户资源的 URL
    String  URI_PARTICIPANT             = "/participant";
    String VIEW_PARTICIPANT             = "participant.htm";
    String REST_PARTICIPANTS            = "/resources/participants";
    String REST_PARTICIPANTS_WITH_ID    = "/resources/participants/{participantId}";
    String REST_PARTICIPANTS_PAGE_COUNT = "/resources/participants/page-count";
    String REST_PARTICIPANT_TELEPHONE_NUMBER_UNUSED = "/resources/participants/telephone-number-unused"; // jquery validate 使用, 不能完全用 RESTful

    // 奖品资源的 URL
    String REST_GIFTS                         = "/resources/gifts";
    String REST_PARTICIPANT_GIFTS             = "/resources/participant-gifts";
    String REST_PARTICIPANT_GIFTS_DESCRIPTION = "/resources/participant-gifts/{participantGiftId}/description";
    String REST_PARTICIPANT_GIFTS_PAGES_COUNT = "/resources/gifts/pages-count";

    // 管理抽奖结果的页面
    String URI_ADMIN_PARTICIPANT_GIFTS  = "/admin/participant-gifts";
    String VIEW_ADMIN_PARTICIPANT_GIFTS = "admin/participant-gifts.htm";
}
