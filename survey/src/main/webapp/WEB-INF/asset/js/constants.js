
var Urls = {
    ERROR_404:           'http://survey.edu-edu.com.cn/404',
    SUBMIT_SUCCESS:      'http://survey.edu-edu.com.cn/submit-success',

    ADMIN_QUESTIONS:    'http://survey.edu-edu.com.cn/admin/topics/{topicId}/questions',
    TOPICS:             'http://survey.edu-edu.com.cn/topics',
    TOPICS_WITH_ID:     'http://survey.edu-edu.com.cn/topics/{topicId}',
    TOPICS_ORDERS:      'http://survey.edu-edu.com.cn/topics/orders',
    QUESTIONS:          'http://survey.edu-edu.com.cn/topics/{topicId}/questions',
    QUESTIONS_WITH_ID:  'http://survey.edu-edu.com.cn/topics/{topicId}/questions/{questionId}',
    QUESTIONS_ORDERS:   'http://survey.edu-edu.com.cn/topics/{topicId}/questions/orders',

    SURVEYS:            'http://survey.edu-edu.com.cn/surveys/{surveyId}',
    SURVEYS_SUBMIT:     'http://survey.edu-edu.com.cn/surveys/{surveyId}/submit',
    SURVEYS_RESULT:     'http://survey.edu-edu.com.cn/surveys-result/surveys/{surveyId}',


    topic:  'http://survey.edu-edu.com.cn/topic/{id}',


    adminTopics: 'http://survey.edu-edu.com.cn/admin/topics',
    question: 'http://survey.edu-edu.com.cn/question/{id}',
    orders: 'http://survey.edu-edu.com.cn/question/orders',

    participant: 'http://survey.edu-edu.com.cn/participant',
    participantTelephoneUnused: 'http://survey.edu-edu.com.cn/participant-telephone-unused',

    fortuneWheel: 'http://survey.edu-edu.com.cn/fortune-wheel',
    gift: 'http://survey.edu-edu.com.cn/gift',
    participantGifts: 'http://survey.edu-edu.com.cn/inner/participant-gifts/{page}',
    participantGiftsPagesCount: 'http://survey.edu-edu.com.cn/inner/participant-gifts-pages-count',
    participantGiftDescription: 'http://survey.edu-edu.com.cn/inner/participant-gift-description'
};

// 正则表达式
var RegExs = {
    // 电话号码
    telephone: /^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$|(^(13|15|18)\d{9}$)/,
    mail: /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/
};
