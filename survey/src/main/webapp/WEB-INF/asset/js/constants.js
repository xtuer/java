
// 如果 URL 中以 resources 开头, 则说明访问的是 RESTful 的资源, 这样利于和页面的 URL 等区分开
var HOST = 'http://survey.edu-edu.com.cn';

var Urls = {
    ERROR_404:              HOST + '/404', // 找不到资源的页面
    SUBMIT_SUCCESS:         HOST + '/submit-success', // 提交成功的页面

    ADMIN_QUESTIONS:        HOST + '/admin/topics/{topicId}/questions', // 管理 topic 问题的页面
    TOPICS:                 HOST + '/resources/topics',           // 访问所有的 topic
    REST_TOPICS_WITH_ID:    HOST + '/resources/topics/{topicId}', // 访问指定 id 的 topic
    REST_TOPICS_ORDERS:     HOST + '/resources/topics/orders',    // topic 的顺序
    REST_QUESTIONS:         HOST + '/resources/topics/{topicId}/questions', // 访问 topic 下所有的问题
    REST_QUESTIONS_WITH_ID: HOST + '/resources/topics/{topicId}/questions/{questionId}', // 访问指定 id 的问题
    REST_QUESTIONS_ORDERS:  HOST + '/resources/topics/{topicId}/questions/orders',       // topic 下问题的顺序

    SURVEYS:                HOST + '/surveys/{surveyId}',        // 调查问卷页面
    SURVEYS_SUBMIT:         HOST + '/surveys/{surveyId}/submit', // 提交调查问卷回答的结果
    SURVEYS_RESULT:         HOST + '/admin/surveys-result/{surveyId}',  // topic 结果的统计页面
    REST_QUESTIONS_ANSWERS_STATISTICS:  HOST + '/resources/topics/{topicId}/questions/answers/statistics', // topic 的所有问题回答的统计
    REST_QUESTIONS_ANSWERS_SUGGESTIONS: HOST + '/resources/topics/{topicId}/questions/{questionId}/question-items/{questionItemId}/suggestions', // 问题选项的输入

    REST_PARTICIPANTS:      HOST + '/resources/participants',
    REST_PARTICIPANTS_TELEPHONE_NUMBER_UNUSED: HOST + '/resources/participants/telephone-number-unused',

    FORTUNE_WHEEL:          HOST + '/fortune-wheel',
    REST_GIFTS:             HOST + '/resources/gifts',
    REST_PARTICIPANT_GIFTS: HOST + '/resources/participant-gifts',
    REST_PARTICIPANT_GIFTS_DESCRIPTION: HOST + '/resources/participant-gifts/{participantGiftId}/description',
    REST_PARTICIPANT_GIFTS_PAGES_COUNT: HOST + '/resources/gifts/pages-count',
};

// 正则表达式
var RegExs = {
    MAIL: /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/, // 邮件地址
    TELEPHONE: /^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$|(^(13|15|18)\d{9}$)/ // 电话号码
};
