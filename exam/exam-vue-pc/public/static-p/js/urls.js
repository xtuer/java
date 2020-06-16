const Urls = {
    // JS，CSS URLs
    TINY_MCE: '/static-p/lib/tinymce/tinymce.min.js', // TinyMCE

    // 用户
    API_USERS               : '/api/currentOrg/users',       // 当前机构用户列表
    API_USERS_COUNT         : '/api/currentOrg/users/count', // 当前机构总记录数
    API_USERS_BY_ID         : '/api/users/{userId}',         // 指定 ID 的用户
    API_USERS_CURRENT       : '/api/login/users/current',    // 当前登录的用户
    API_USER_PASSWORDS_RESET: '/api/users/{userId}/passwords/reset', // 重置密码

    FORM_UPLOAD_TEMPORARY_FILE : '/form/upload/temp/file',  // 上传一个临时文件
    FORM_UPLOAD_TEMPORARY_FILES: '/form/upload/temp/files', // 上传多个临时文件
    API_CAN_PREVIEW_FILE_PREFIX: '/api/canPreview',         // 请求是否可预览文件的前缀

    // 消息系统
    MESSAGE_WEBSOCKET_URL: `ws://${window.location.hostname}:3721`,

    // 机构
    API_ORGS       : '/api/orgs',         // 所有机构
    API_ORGS_BY_ID : '/api/orgs/{orgId}', // 指定 ID 的机构
    API_ORGS_ENABLE: '/api/orgs/{orgId}/enabled', // 指定 ID 的机构的启用禁用状态

    // 字典
    API_DICTS_ID : '/api/dicts/{dictId}',   // 单个查询/修改/删除
    API_DICTS    : '/api/currentOrg/dicts', // 查询当前机构字典列表

    // 考试 (考试记录和用户有关)
    API_PAPERS_BY_ID               : '/api/exam/papers/{paperId}',    // 指定 ID 的试卷
    API_PAPERS_OF_CURRENT_ORG      : '/api/exam/papers/ofCurrentOrg', // 当前机构的试卷
    API_EXAMS_BY_ID                : '/api/exam/exams/{examId}',      // 指定 ID 的考试
    API_EXAMS_OF_CURRENT_ORG       : '/api/exam/exams/ofCurrentOrg',  // 当前机构的考试
    API_USER_EXAMS                 : '/api/exam/users/{userId}/exams/{examId}',         // 用户的某次考试 (同时得到此次考试的所有考试记录)
    API_USER_EXAM_RECORDS          : '/api/exam/users/{userId}/exams/{examId}/records', // 用户的某次考试的考试记录
    API_USER_EXAM_RECORDS_BY_ID    : '/api/exam/users/{userId}/exams/{examId}/records/{recordId}',         // 指定 ID 的考试记录
    API_USER_EXAM_ANSWER_QUESTIONS : '/api/exam/users/{userId}/exams/{examId}/records/{recordId}/answer',  // 题目作答
    API_EXAM_RECORDS_TICK          : '/api/exam/users/{userId}/exams/{examId}/records/{recordId}/tick',    // 考试计时
    API_EXAM_RECORDS_BY_EXAM_IDS   : '/api/exam/users/{userId}/exams/records', // 指定用户的多个考试的考试记录
    API_EXAM_RECORDS_BY_EXAM_ID    : '/api/exam/exams/{examId}/examRecords',   // 指定考试的考试记录
    API_EXAM_CORRECT_QUESTIONS     : '/api/exam/exams/{examId}/records/{recordId}/correct', // 主观题批改
    API_EXAM_ANSWER_STATISTICS     : '/api/exam/exams/{examId}/answers/statistics', // 考试的作答统计
    API_EXAM_RECORDS_COUNT         : '/api/exam/exams/records/count', // 统计考试的考试记录数量
    API_EXAM_ESSAY_QUESTION_ANSWERS: '/api/exam/exams/{examId}/essayQuestions/{questionId}/answers', // 查询指定考试的问答题的非空作答内容
};

window.Urls = Urls;
