/**
 * 知识点的类
 * 知识点分类也是知识点，只是它包含了其他知识点
 * 知识点的 type 为 1，分类的 type 为 0
 *
 * @param {String} knowledgePointId       知识点的 id
 * @param {String} parentKnowledgePointId 分类的 id
 * @param {String} name                   知识点
 * @param {int}    type                   知识点为 1，分类为 0
 */
function KnowledgePoint(knowledgePointId, parentKnowledgePointId, name, type) {
    this.knowledgePointId = knowledgePointId;
    this.parentKnowledgePointId = parentKnowledgePointId;
    this.name = name;
    this.type = type;
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
    return new KnowledgePoint(0, 0, '', 1);
};

/**
 * 创建一个新的知识点
 *
 * @param  {String} name                  知识点名字
 * @param  {String} knowledgePointGroupId 所在分类的 id
 * @return {Object} 新的知识点
 */
KnowledgePoint.newKnowledgePoint = function(name, knowledgePointGroupId) {
    return new KnowledgePoint(0, knowledgePointGroupId, name, 1);
};

/**
 * 创建一个新的知识点分类
 *
 * @param  {String} parentKnowledgePointId 所在分类的 id
 * @return {Object} 知识点分类
 */
KnowledgePoint.newKnowledgePointGroup = function(parentKnowledgePointId) {
    return new KnowledgePoint(0, parentKnowledgePointId, '新建分类', 0);
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
 * @param  {Function} callback 删除知识点成功时的回调函数，无参数
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
 * 重命名知识点
 *
 * @param  {String}   knowledgePointId 知识点的 id
 * @param  {String}   name             新的知识点名字
 * @return {Boolean}  重命名成功返回 true，否则返回 false
 */
KnowledgePointDao.rename = function(knowledgePointId, name) {
    var ok = false;

    $.rest.syncUpdate({url: Urls.REST_KNOWLEDGE_POINTS_NAME,
    urlParams: {knowledgePointId: knowledgePointId}, data: {name: name}, success: function(result) {
        ok = result.success;

        if (!ok) {
            layer.msg(result.message);
        }
    }});

    return ok;
};

/**
 * 移动知识点到其他分类下
 *
 * @param  {String}   knowledgePointId          知识点的 id
 * @param  {String}   newParentKnowledgePointId 新分类的 id
 * @param  {Function} successCallback           成功时候的回调函数，无参数
 * @param  {Function} failCallback              失败时候的回调函数，无参数
 * @return 无返回值
 */
KnowledgePointDao.updateParent = function(knowledgePointId, newParentKnowledgePointId, successCallback, failCallback) {
    $.rest.update({url: Urls.REST_KNOWLEDGE_POINTS_PARENT_ID,
    urlParams: {knowledgePointId: knowledgePointId}, data: {newParentKnowledgePointId: newParentKnowledgePointId},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            failCallback && failCallback();
        } else {
            successCallback && successCallback();
        }
    }, fail: function() {
        failCallback && failCallback();
    }});
};

/**
 * 加载指定知识点分类下的知识点
 *
 * @param  {String}   knowledgePointGroupId 知识点分类的 parent id
 * @param  {Function} callback              从服务器加载知识点成功时的回调函数，参数为知识点的数组
 * @return 无返回值
 */
KnowledgePointDao.loadKnowledgePoints = function(knowledgePointGroupId, callback) {
    $.rest.get({url: Urls.REST_KNOWLEDGE_POINTS_IN_GROUP, urlParams: {knowledgePointGroupId: knowledgePointGroupId},
        success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(result.data); // result.data 为知识点数组
    }});
};

/**
 * 加载知识点分类
 *
 * @param  {Function} callback 成功加载时的回调函数
 * @return 无返回值
 */
KnowledgePointDao.loadKnowledgePointGroups = function(callback) {
    $.rest.get({url: Urls.REST_KNOWLEDGE_POINT_GROUPS, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(result.data); // result.data 为知识点分类数组分类
    }});
};
