function Directory(tree) {

}

/**
 * 创建目录
 */
Directory.create = function(treeId, parentTreeNode) {
    var name = '请重命名...';
    var parentDirectoryId = parentTreeNode ? parentTreeNode.directoryId : 0;

    $.rest.create({url: Urls.REST_DIRECTORIES, data: {name: name, parentDirectoryId: parentDirectoryId}, async: false, success: function(result) {
        if (result.success) {
            var directoryId = result.data.directoryId;
            var node = {directoryId: 111111, parentDirectoryId: parentDirectoryId, name: name};
            window.tree.addNodes(parentTreeNode, 0, [node], false);
        }
    }});
};

/**
 * 重命名目录
 */
Directory.rename = function(treeId, treeNode, newName, isCancel) {
    // 1. 按下 ESC 时 isCancel 为 true，取消重命名
    // 2. 把名字前后的空格去掉
    // 3. 名字不能为空或则全是空格
    // 4. 请求服务器重命名
    //    4.1. 成功则返回 true 更新目录树上目录的名字
    //    4.2. 失败返回 false 并提示错误信息

    if (!isCancel) {
        newName = $.trim(newName);

        if (!newName) {
            layer.msg('目录名不能为空');
            return false;
        }

        // Ajax 同步请求服务器重命名
        var ok = false;
        $.rest.update({url: Urls.REST_DIRECTORY_NAME, urlParams: {directoryId: treeNode.directoryId},
            data: {name: newName}, async: false, success: function(result) {
            ok = result.success;

            if (!ok) {
                layer.msg(result.message);
            }
        }});

        return ok;
    }
};

/**
 * 删除目录
 */
Directory.remove = function(treeId, treeNode) {
    // 1. 询问用户是否确定删除
    // 2. 请求服务器删除目录
    //    2.1. 成功则返回 true 删除目录树上目录
    //    2.2. 失败返回 false 并提示错误信息
    var ok = confirm('确定删除 ' +  treeNode.name + ' 吗？'); // 删除前最好是询问一下是否确定删除，防止误操作
    if (!ok) { return false; }

    // Ajax 同步请求服务器删除数据库中的节点，删除成功返回 true，删除失败提示错误信息并返回 false
    ok = false;
    $.rest.remove({url: Urls.REST_DIRECTORIES_WITH_ID, urlParams: {directoryId: treeNode.directoryId},
        async: false, success: function(result) {
        ok = result.success;

        if (!ok) {
            layer.msg(result.message);
        }
    }});

    return ok;
};

require(['jquery', 'vue', 'layer', 'semanticUi', 'ztree', 'rest', 'urls'], function($, Vue) {
    /*-----------------------------------------------------------------------------|
    |                                  Directory                                   |
    |-----------------------------------------------------------------------------*/
    var setting = {
        data: {
            simpleData: {
                enable:  true, // 启用数组结构的数据创建 zTree
                idKey:   'directoryId',       // 定义 node id 的 key，可自定义
                pIdKey:  'parentDirectoryId', // 定义 parent id 的 key，可自定义
                rootPId: 0 // 根节点的 parentId，这个节点是看不到的，因为不存在
            }
        },
        async: {
            enable: true,
            type: 'get',
            url: function(treeId, treeNode) {
                // treeNode 不存在时表示需要加载根节点下的数据
                return Urls.REST_DIRECTORIES + '?parentDirectoryId=' + (treeNode ? treeNode.directoryId : 0);
            },
            dataFilter: function(treeId, parentNode, result) {
                if (result.success) {
                    var directories = result.data;
                    for (var i = 0; i < directories.length; i++) {
                        directories[i].isParent = true;
                    }
                    return directories;
                }

                return null;
            }
        },
        edit: {
            enable: true // [*] 启用拖拽，重命名和删除等编辑操作
        },
        view: {
            selectedMulti: false,     // [*] 为 true 时可选择多个节点，为 false 只能选择一个，默认为 true
            addHoverDom: addHoverDom, // [2] 注册函数 addHoverDom 和 removeHoverDom
            removeHoverDom: removeHoverDom
        },
        callback: {
            beforeRename: Directory.rename,
            beforeRemove: Directory.remove
        }
    };
    
    window.tree = $.fn.zTree.init($('#directory-tree'), setting);

    function createDirectory(name, parentDirectoryId) {
        $.rest.create({url: Urls.REST_DIRECTORIES, data: {name: name, parentDirectoryId: parentDirectoryId},
            success: function(result) {

        }});
    }

    // [2.1] 鼠标移动到节点上时创建一个新建子节点的按钮
    var newCount = 10000000;
    function addHoverDom(treeId, treeNode) {
        if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
        var $nodeName = $("#"+treeNode.tId+"_span");
        $nodeName.after('<span class="button add" id="addBtn_'+treeNode.tId+'" title="新建子节点" onfocus="this.blur();"></span>');
        var btn = $("#addBtn_"+treeNode.tId);
        if (btn) {
            btn.bind("click", function() {
                Directory.create(treeId, treeNode);
            });
        }
    }

    // [2.2] 鼠标从节点上离开时删除新建子节点的按钮
    function removeHoverDom(treeId, treeNode) {
        $("#addBtn_"+treeNode.tId).unbind().remove();
    }

    /*-----------------------------------------------------------------------------|
    |                                    File                                      |
    |-----------------------------------------------------------------------------*/
    var vueFiles = new Vue({
        el: '#vue-files',
        data: {
            files: [{
                fileId: 1,
                image: 'http://omqpd0pt4.bkt.clouddn.com/ade.jpg',
                name: 'Kristy'
            },{
                fileId: 2,
                image: 'http://omqpd0pt4.bkt.clouddn.com/ade.jpg',
                name: 'Kristy'
            },{
                fileId: 3,
                image: 'http://omqpd0pt4.bkt.clouddn.com/ade.jpg',
                name: 'Kristy'
            },{
                fileId: 4,
                image: 'http://omqpd0pt4.bkt.clouddn.com/ade.jpg',
                name: 'Kristy'
            },{
                fileId: 5,
                image: 'http://omqpd0pt4.bkt.clouddn.com/ade.jpg',
                name: 'Kristy'
            },{
                fileId: 6,
                image: 'http://omqpd0pt4.bkt.clouddn.com/ade.jpg',
                name: 'Kristy'
            },{
                fileId: 7,
                image: 'http://omqpd0pt4.bkt.clouddn.com/ade.jpg',
                name: 'Kristy'
            },{
                fileId: 8,
                image: 'http://omqpd0pt4.bkt.clouddn.com/ade.jpg',
                name: 'Kristy'
            },{
                fileId: 9,
                image: 'http://omqpd0pt4.bkt.clouddn.com/ade.jpg',
                name: 'Kristy'
            },{
                fileId: 10,
                image: 'http://omqpd0pt4.bkt.clouddn.com/ade.jpg',
                name: 'Kristy'
            },{
                fileId: 11,
                image: 'http://omqpd0pt4.bkt.clouddn.com/ade.jpg',
                name: 'Kristy'
            }]
        }
    });
});
