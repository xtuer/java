/**
 * Question 的 Dao
 */
function QuestionDao() {
}

/**
 * 加载指定单题知识点下的子知识点.
 *
 * @param  {String}   parentKnowledgePointId 父知识点的 ID
 * @param  {Function} callback               成功加载的回调函数，参数是知识点数组
 * @return 无返回值
 */
QuestionDao.loadQuestionKnowledgePoints = function(parentKnowledgePointId, callback) {
    $.rest.get({url: Urls.REST_QUESTION_KNOWLEDGE_POINTS_BY_PARENT_ID, urlParams: {parentId: parentKnowledgePointId},
        success: function(result) {
            if (!result.success) {
                layer.msg(result.message);
                return;
            }

            callback && callback(result.data); // result.data 是知识点的数组
        }
    });
};

/**
 * 加载知识点下的单题
 *
 * @param  {String}   questionKnowledgePointId 知识点的 ID
 * @param  {Function} callback                 成功加载的回调函数，参数是单题的数组
 * @return 无返回值
 */
QuestionDao.loadQuestionsUnderQuestionKnowledgePoint = function(questionKnowledgePointId, callback) {
    $.rest.get({url: Urls.REST_QUESTIONS_UNDER_KNOWLEDGE_POINT, urlParams: {questionKnowledgePointId: questionKnowledgePointId},
        success: function(result) {
            if (!result.success) {
                layer.msg(result.message);
                return;
            }

            var questions = result.data;
            callback && callback(questions); // result.data 是单题的数组
        }
    });
};

/**
 * 标记和取消标记题目
 * @param  {String}   questionId 题目的 ID
 * @param  {Function} callback   请求成功的回调函数，没有参数
 * @return 无返回值
 */
QuestionDao.toggleQuestionMark = function(questionId, callback) {
    $.rest.update({url: Urls.REST_TOGGLE_QUESTION_MARK, urlParams: {questionId: questionId},
        success: function(result) {
            if (!result.success) {
                layer.msg(result.message);
                return;
            }

            callback && callback();
        }
    });
};
