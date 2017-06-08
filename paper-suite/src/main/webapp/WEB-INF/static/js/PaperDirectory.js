/**
 * PaperDirectory 的 Dao
 */
function PaperDirectoryDao() {
}

/**
 * 加载所有的目录
 * 服务器响应格式:
 * {
 *     "code": 0,
 *     "data": [{
 *         "name": "01 高中语文",
 *         "paperDirectoryId": "5b5a5676bace45c1a4128b9e6b5b355e",
 *         "parentPaperDirectoryId": "0"
 *     }, {
 *         "name": "2015",
 *         "paperDirectoryId": "dbcac711ea094239b9420fb44d699036",
 *         "parentPaperDirectoryId": "5b5a5676bace45c1a4128b9e6b5b355e"
 *     }, {
 *         "name": "Avatar Foo",
 *         "paperDirectoryId": "11e2cfb84b524328907f3341b99946e5",
 *         "parentPaperDirectoryId": "7fcdea63025743fdb7375298ffdb93ff"
 *     }],
 *     "message": "",
 *     "success": true
 * }
 *
 * @param  {Function} callback 加载成功时的回调函数，参数为目录的数组
 * @return 无返回值
 */
PaperDirectoryDao.loadPaperDirectories = function(callback) {
    // 一次性加载所有目录
    $.rest.get({url: Urls.REST_PAPER_DIRECTORIES, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(result.data); // result.data 是目录的数组
    }});
};

/**
 * 加载目录下试卷的数量
 * 服务器响应格式:
 * {
 *     "code": 0,
 *     "data": [
 *         {"count": 13,"paperDirectoryId": "22f357679f8a46249fa509ae4715e761"},
 *         {"count": 8, "paperDirectoryId": "e486682d7a52471caf8c9b41f2af51f8"}
 *     ],
 *     "message": "",
 *     "success": true
 * }
 *
 * @param  {Function} callback 加载目录下试卷的数量成功时的回调函数，参数为试卷数量的数组
 * @return 无返回值
 */
PaperDirectoryDao.loadPaperCounts = function(callback) {
    $.rest.get({url: Urls.REST_PAPER_DIRECTORIES_PAPER_COUNTS, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(result.data); // result.data 为试卷数量的数组

        self.papersCount = result.data;
    }});
};

/**
 * 创建目录
 * 服务器响应格式:
 * {
 *     "code": 0,
 *     "data": {
 *         "name": "新建文件夹",
 *         "paperDirectoryId": "9d4a1876afad43d69d2052158d3e8b3c",
 *         "parentPaperDirectoryId": "7fcdea63025743fdb7375298ffdb93ff"
 *     },
 *     "message": "",
 *     "success": true
 * }
 *
 * @param  {Object}   parentPaperDirectoryId 目录节点的父节点 id
 * @param  {Function} callback               创建目录成功时的回调函数，参数为新创建的目录
 * @return 无返回值
 */
PaperDirectoryDao.create = function(parentPaperDirectoryId, callback) {
    $.rest.syncCreate({url: Urls.REST_PAPER_DIRECTORIES, data: {name: '新建文件夹', parentPaperDirectoryId: parentPaperDirectoryId},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback(result.data); // result.data 为新创建的节点
    }});
};

/**
 * 重命名目录
 * 服务器响应格式:
 * {"code":0,"message":"重命名目录成功","success":true}
 *
 * @param  {Object} treeNode zTree 的节点
 * @param  {String} newName  新名字
 * @return {Boolean} 更新成功返回 true，否则返回 false
 */
PaperDirectoryDao.rename = function(treeNode, newName) {
    // Ajax 同步请求服务器重命名
    var ok = false;
    $.rest.syncUpdate({url: Urls.REST_PAPER_DIRECTORY_NAME, urlParams: {paperDirectoryId: treeNode.paperDirectoryId},
        data: {name: newName}, success: function(result) {
        ok = result.success;

        if (!ok) {
            layer.msg(result.message);
        }
    }});

    return ok;
};

/**
 * 移动目录
 * 服务器响应格式:
 * {"code":0,"message":"移动目录成功","success":true}
 *
 * @param  {Object}   node     zTree 节点
 * @param  {Function} callback 更新父节点失败时的回调函数
 * @return 无返回值
 */
PaperDirectoryDao.updateParent = function(node, callback) {
    var paperDirectoryId = node.paperDirectoryId;
    var parentPaperDirectoryId = node.parentPaperDirectoryId;
    $.rest.update({url: Urls.REST_PAPER_DIRECTORY_PARENT_ID, urlParams: {paperDirectoryId: paperDirectoryId},
        data: {parentPaperDirectoryId: parentPaperDirectoryId}, success: function(result) {

        if (!result.success) {
            layer.msg(result.message);
            callback && callback();
        }
    }, fail: function() {
        callback && callback();
    }});
};

/**
 * 删除目录
 * 服务器响应格式:
 * {"code":0,"message":"success","success":true}
 *
 * @param  {Object}   treeNode zTree 的节点
 * @param  {Function} callback 删除目录成功时的回调函数
 * @return 无返回值
 */
PaperDirectoryDao.remove = function(treeNode, callback) {
    // Ajax 请求服务器删除数据库中的节点，成功则从 tree 中删除此 node
    $.rest.remove({url: Urls.REST_PAPER_DIRECTORIES_BY_ID, urlParams: {paperDirectoryId: treeNode.paperDirectoryId},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback();
    }});
};

/**
 * 移动试卷到目录
 * 服务器响应格式:
 * {"code":0,"message":"success","success":true}
 *
 * @param  {String}   paperId          试卷 id
 * @param  {String}   paperDirectoryId 目录 id
 * @param  {Function} callback         移动成功时的回调函数
 * @return 无返回值
 */
PaperDirectoryDao.movePaperToPaperDirectory = function(paperId, paperDirectoryId, callback) {
    // 修改选中的试卷的目录
    $.rest.update({url: Urls.REST_PAPERS_REDIRECTORY, data: {paperIds: [paperId], paperDirectoryId: paperDirectoryId},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback && callback();
    }});
};
