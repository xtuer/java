/**
 * 知识点的类
 *
 * @param {String} knowledgePointId      知识点 id
 * @param {String} name                  知识点名字
 * @param {String} knowledgePointGroupId 知识点分类 id
 */
function KnowledgePoint(knowledgePointId, name, knowledgePointGroupId) {
    this.knowledgePointId = knowledgePointId;
    this.name = name;
    this.knowledgePointGroupId = knowledgePointGroupId;
}

/**
 * 知识点的 Dao
 */
function KnowledgePointDao() {

}

/**
 * 创建一个空的知识点
 *
 * @return {Object} 空的知识点对象
 */
KnowledgePoint.emptyKnowledgePoint = function() {
    return new KnowledgePoint(0, '', 0);
};

/**
 * 创建知识点
 *
 * @param  {Object} knowledgePoint KnowledgePoint 对象
 * @param  {Function} callback     创建知识点成功时的回调函数，参数为新创建的知识点
 * @return 无返回值
 */
KnowledgePointDao.create = function(knowledgePoint, callback) {
    $.rest.create({url: Urls.REST_KNOWLEDGE_POINTS, data: knowledgePoint, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }


        callback && callback(result.data); // result.data 是新创建的知识点
    }});
};

/**
 * 更新知识点
 *
 * @param  {Object}   knowledgePoint KnowledgePoint 对象
 * @param  {Function} callback       删除知识点成功时的回调函数，参数为被删除的知识点
 * @return 无返回值
 */
KnowledgePointDao.update = function(knowledgePoint, callback) {
    $.rest.update({url: Urls.REST_KNOWLEDGE_POINTS_BY_ID, urlParams: {knowledgePointId: knowledgePoint.knowledgePointId},
    data: knowledgePoint, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(knowledgePoint);
    }});
};

/**
 * 删除知识点
 *
 * @param  {String}   knowledgePointId 知识点 id
 * @param  {Function} callback 删除知识点成功时的回调函数
 * @return 无返回值
 */
KnowledgePointDao.remove = function(knowledgePointId, callback) {
    $.rest.remove({url: Urls.REST_KNOWLEDGE_POINTS_BY_ID, urlParams: {knowledgePointId: knowledgePointId},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback();
    }});
};

/**
 * 加载指定知识点分类下的知识点
 *
 * @param  {String}   knowledgePointGroupId 知识点分类的 id
 * @param  {Function} callback              从服务器加载知识点成功时的回调函数，参数为知识点的数组
 * @return 无返回值
 */
KnowledgePointDao.loadKnowledgePoints = function(knowledgePointGroupId, callback) {
    $.rest.get({url: Urls.REST_KNOWLEDGE_POINTS_OF_GROUP, urlParams: {knowledgePointGroupId: knowledgePointGroupId},
        success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(result.data); // result.data 为知识点数组
    }});
};
