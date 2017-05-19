/**
 * Paper 的 Dao
 */
function PaperDao() {

}

/**
 * 使用试卷的 id 查找试卷
 *
 * @param  {String}   paperId  试卷 id
 * @param  {Function} callback 成功的回调函数，参数为查询到的试卷
 * @return 无返回值
 */
PaperDao.findById = function(paperId, callback) {
    $.rest.get({url: Urls.REST_PAPERS_BY_ID, urlParams: {paperId: paperId}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(result.data); // result.data 是查询到的试卷
    }});
};

/**
 * 查找目录下某个知识点的试卷
 *
 * @param  {String} paperDirectoryId 目录 id
 * @param  {String} knowledgePointId 知识点 id
 * @param  {Function} callback       查找成功的回调函数，参数为试卷的数组
 * @return 无返回值
 */
PaperDao.findPapersByKnowledgePointIdsInPaperDirectory = function(paperDirectoryId, knowledgePointIds, callback) {
    $.rest.get({url: Urls.REST_PAPERS_SEARCH_IN_DIRECTORY, urlParams: {paperDirectoryId: paperDirectoryId},
    data: {knowledgePointIds: knowledgePointIds}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(result.data); // result.data 是试卷的数组
    }});
};

/**
 * 更新试卷
 *
 * @param  {Object}   paper    试卷
 * @param  {Function} callback 成功的回调函数，参数为更新的试卷
 * @return 无返回值
 */
PaperDao.update = function(paper, callback) {
    $.rest.update({url: Urls.REST_PAPERS_BY_ID, urlParams: {paperId: paper.paperId},
    data: {name: paper.name, publishYear: paper.publishYear, exportable: paper.exportable},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(paper);
    }});
};

/**
 * 删除试卷的知识点
 *
 * @param  {String}   paperId          试卷 id
 * @param  {String}   knowledgePointId 知识点 id
 * @param  {Function} callback         删除知识点成功时的回调函数
 * @return 无返回值
 */
PaperDao.removeKnowledgePoint = function(paperId, knowledgePointId, callback) {
    $.rest.remove({url: Urls.REST_PAPERS_KNOWLEDGE_POINTS_BY_ID, urlParams: {paperId: paperId, knowledgePointId: knowledgePointId},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback();
    }});
};

/**
 * 根据学科和名字查找试卷
 *
 * @param  {String}   subject    学科
 * @param  {String}   nameFilter 试卷名字的子字符串
 * @param  {Function} callback   查找试卷成功时的回调函数，参数是试卷的数组
 * @return 无返回值
 */
PaperDao.search = function(subject, nameFilter, callback) {
    $.rest.get({url: Urls.REST_PAPERS_SEARCH, data: {subject: subject, nameFilter: nameFilter}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(result.data); // result.data 是试卷的数组
    }});
};

/**
 * 添加试卷到目录
 *
 * @param  {Array}    papers           试卷数组
 * @param  {String}   paperDirectoryId 目录 id
 * @param  {Function} callback         添加成功时的回调函数，参数为被添加的试卷
 * @return 无返回值
 */
PaperDao.addPapersToPaperDirectory = function(papers, paperDirectoryId, callback) {
    var paperIds = [];

    for (var i=0; i<papers.length; ++i) {
        paperIds.push(papers[i].paperId);
    }

    // 如果没有试卷，则提示
    if (paperIds.length === 0) {
        layer.msg('没有试卷');
        return;
    }

    $.rest.update({url: Urls.REST_PAPERS_REDIRECTORY, data: {paperIds: paperIds, paperDirectoryId: paperDirectoryId},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(papers);
    }});
};

/**
 * 添加知识点到试卷
 *
 * @param  {String}   paperId        试卷 id
 * @param  {String}   knowledgePoint 知识点
 * @param  {Function} callback       添加知识点成功时的回调函数，参数为被添加的知识点
 * @return 无返回值
 */

PaperDao.addKnowledgePoint = function(paperId, knowledgePoint, callback) {
    $.rest.create({url: Urls.REST_PAPERS_KNOWLEDGE_POINTS, urlParams: {paperId: paperId},
    data: {knowledgePointId: knowledgePoint.knowledgePointId}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(knowledgePoint);
    }});
};
