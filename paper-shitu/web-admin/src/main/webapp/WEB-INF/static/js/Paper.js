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
 * 加载指定目录下的试卷
 * 服务器响应格式:
 * {
 *     "code": 0,
 *     "data": [{
 *         "knowledgePoints": [{
 *             "knowledgePointId": "e4528ea26d4844b286814a08128db068",
 *             "name": "三角函数、三角恒等变换、解三角形",
 *             "paperId": "0d85cb6f97f14523a97019a40b1fe6f1"
 *         }],
 *         "name": "2011届高考数学强化复习训练题24.doc",
 *         "originalName": "2011届高考数学强化复习训练题24.doc",
 *         "paperDirectoryId": "e7247aaedeca453f9678417f6adfb981",
 *         "paperId": "0d85cb6f97f14523a97019a40b1fe6f1",
 *         "realDirectoryName": "16",
 *         "subject": "高中数学",
 *         "uuidName": "b47738a35f6f42bcb94e80ecd8e94cae.doc"
 *     }],
 *     "message": "",
 *     "success": true
 * }
 *
 * @param  {String}   paperDirectoryId 试卷所在目录的 id
 * @param  {Function} callback         加载试卷成功时的回调函数，参数为试卷的数组
 * @return 无返回值
 */
PaperDao.findPapersInPaperDirectory = function(paperDirectoryId, callback) {
    $.rest.get({url: Urls.REST_PAPERS_OF_DIRECTORY, urlParams: {paperDirectoryId: paperDirectoryId}, success: function(result) {
        if (!result.success) {
            layer.msg(result.msg);
            return;
        }

        callback && callback(result.data); // result.data 为试卷数组
    }});
};

/**
 * 查找目录下带知识点的试卷的页数
 *
 * @param  {String}   paperDirectoryId  目录 id
 * @param  {Array}    knowledgePointIds 知识点 id 的数组，如果为空则查找此目录下的所有试卷
 * @param  {int}      pageSize          每页显示试卷数量
 * @param  {Function} callback          查找成功的回调函数，参数为页数
 * @return 无返回值
 */
PaperDao.pageCountPapersByKnowledgePointIdInPaperDirectory = function(paperDirectoryId, knowledgePointIds, pageSize, callback) {
    $.rest.get({url: Urls.REST_PAPERS_COUNT_SEARCH_IN_DIRECTORY, urlParams: {paperDirectoryId: paperDirectoryId},
    data: {knowledgePointIds: knowledgePointIds, pageSize: pageSize}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(result.data); // result.data 是页数
    }});
};

/**
 * 查找目录下带知识点的试卷
 *
 * @param  {String}   paperDirectoryId  目录 id
 * @param  {Array}    knowledgePointIds 知识点 id 的数组，如果为空则查找此目录下的所有试卷
 * @param  {int}      pageNumber        页码
 * @param  {int}      pageSize          每页显示试卷数量
 * @param  {Function} callback          查找成功的回调函数，参数为试卷的数组
 * @return 无返回值
 */
PaperDao.findPapersInPaperDirectoryWithKnowledgePointIds = function(paperDirectoryId, knowledgePointIds, pageNumber, pageSize, callback) {
    $.rest.get({url: Urls.REST_PAPERS_SEARCH_IN_DIRECTORY, urlParams: {paperDirectoryId: paperDirectoryId},
    data: {knowledgePointIds: knowledgePointIds, pageNumber: pageNumber, pageSize: pageSize}, success: function(result) {
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
    delete paper.knowledgePoints;

    $.rest.update({url: Urls.REST_PAPERS_BY_ID, urlParams: {paperId: paper.paperId},
    // data: {name: paper.name, publishYear: paper.publishYear, status: paper.status, description: paper.description},
    data: paper,
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
 * @param  {String}   paperId         试卷 id
 * @param  {Array}    knowledgePoints 知识点数组
 * @param  {Function} callback        添加知识点成功时的回调函数，参数为新添加的知识点的 ids 数组
 * @return 无返回值
 */

PaperDao.addKnowledgePoints = function(paperId, knowledgePoints, callback) {
    // 知识点的 ids
    var pointIds = knowledgePoints.map(function(p) {
        return p.knowledgePointId;
    });

    $.rest.create({url: Urls.REST_PAPERS_KNOWLEDGE_POINTS, urlParams: {paperId: paperId},
    data: {knowledgePointIds: pointIds}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(result.data);
    }});
};
