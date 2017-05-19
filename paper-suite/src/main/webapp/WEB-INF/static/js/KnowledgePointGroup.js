/**
 * 知识点分类
 *
 * @param {String} knowledgePointGroupId 知识点分类 id
 * @param {String} name                  知识点分类名字
 */
function KnowledgePointGroup(knowledgePointGroupId, name) {
    this.knowledgePointGroupId = knowledgePointGroupId;
    this.name = name;
}

/**
 * 知识点分类的 Dao
 */
function KnowledgePointGroupDao() {

}

/**
 * 从服务器加载知识点分类
 *
 * @param  {Function} callback 加载知识点分类成功时的回调函数，其参数为知识点分类数组
 * @return 无返回值
 */
KnowledgePointGroupDao.loadKnowledgePointGroups = function(callback) {
    $.rest.get({url: Urls.REST_KNOWLEDGE_POINT_GROUPS, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        var groups = result.data; // 知识点分类数组
        callback && callback(groups);
    }});
};

/**
 * 加载指定知识点分类下的知识点
 *
 * @param  {String}   knowledgePointGroupId 知识点分类的 id
 * @param  {Function} callback              从服务器加载知识点成功时的回调函数，参数为知识点的数组
 * @return 无返回值
 */
KnowledgePointGroupDao.loadKnowledgePoints = function(knowledgePointGroupId, callback) {
    $.rest.get({url: Urls.REST_KNOWLEDGE_POINTS_OF_GROUP, urlParams: {knowledgePointGroupId: knowledgePointGroupId},
        success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(result.data); // result.data 为知识点数组
    }});
};

/**
 * 创建知识点分类
 *
 * @param  {Object}   knowledgePointGroup KnowledgePointGroup 对象
 * @param  {Function} callback 创建知识分类成功时的回调函数，参数为新创建的知识点分类
 * @return 无返回值
 */
KnowledgePointGroupDao.create = function(knowledgePointGroup, callback) {
    $.rest.create({url: Urls.REST_KNOWLEDGE_POINT_GROUPS, data: knowledgePointGroup, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        knowledgePointGroup = result.data; // 新创建的知识点分类
        callback && callback(knowledgePointGroup);
    }});
};

/**
 * 更新知识点分类
 *
 * @param  {Object}   knowledgePointGroup KnowledgePointGroup 对象
 * @param  {Function} callback 更新知识分类成功时的回调函数
 * @return 无返回值
 */
KnowledgePointGroupDao.update = function(knowledgePointGroup, callback) {
    $.rest.update({url: Urls.REST_KNOWLEDGE_POINT_GROUPS_BY_ID,
        urlParams: {knowledgePointGroupId: knowledgePointGroup.knowledgePointGroupId},
        data: {name: knowledgePointGroup.name}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback();
    }});
};

/**
 * 删除知识点分类
 *
 * @param  {Object}   knowledgePointGroup 知识点分类
 * @param  {Function} callback 删除知识分类成功时的回调函数，参数为被删除的知识点分类
 * @return 无返回值
 */
KnowledgePointGroupDao.remove = function(knowledgePointGroup, callback) {
    $.rest.remove({url: Urls.REST_KNOWLEDGE_POINT_GROUPS_BY_ID,
        urlParams: {knowledgePointGroupId: knowledgePointGroup.knowledgePointGroupId},
        success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(knowledgePointGroup);
    }});
};
