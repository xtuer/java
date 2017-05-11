/*-----------------------------------------------------------------------------|
 |                                  Directory                                  |
 |----------------------------------------------------------------------------*/
function Directory(tree) {
}

/**
 * 点击目录时，列出此目录下的所有文件
 */
Directory.getPapers = function(event, treeId, treeNode, clickFlag) {
    var paperDirectoryId = treeNode.paperDirectoryId;

    $('#tool-bar .toggle-buttons').show();
    $('#vue-knowledge-points').slideUp();
    $('#vue-papers').attr('data-paper-directory-id', paperDirectoryId);

    $.rest.get({url: Urls.REST_PAPERS_OF_DIRECTORY, urlParams: {paperDirectoryId: paperDirectoryId}, success: function(result) {
        if (!result.success) {
            layer.msg(result.msg);
            return;
        }

        var papers = result.data;
        for (var i=0; i<papers.length; ++i) {
            // 只保留 publishDate 中的年月日
            if (papers[i].publishDate && papers[i].publishDate.length>10) {
                papers[i].publishDate = papers[i].publishDate.substring(0, 10);
            }
        }
        window.vuePapers.papers = papers;
    }});
};

/**
 * 创建目录
 */
Directory.create = function(parentTreeNode) {
    var name = '新建文件夹';
    var parentPaperDirectoryId = parentTreeNode ? parentTreeNode.paperDirectoryId : 0;

    $.rest.syncCreate({url: Urls.REST_PAPER_DIRECTORIES, data: {name: name, parentPaperDirectoryId: parentPaperDirectoryId}, success: function(result) {
        if (result.success) {
            var paperDirectoryId = result.data.paperDirectoryId;
            var node = {paperDirectoryId: paperDirectoryId, parentPaperDirectoryId: parentPaperDirectoryId, name: name, isParent: true};
            window.tree.addNodes(parentTreeNode, -1, node, false);
        } else {
            layer.msg(result.message);
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
        $.rest.syncUpdate({url: Urls.REST_PAPER_DIRECTORY_NAME, urlParams: {paperDirectoryId: treeNode.paperDirectoryId},
            data: {name: newName}, success: function(result) {
            ok = result.success;

            if (!ok) {
                layer.msg(result.message);
            }
        }});

        return ok;
    }
};

/**
 * 修改父目录的 id
 */
Directory.changeParent = function(event, treeId, treeNodes, targetNode, moveType, isCopy) {
    // [*] 拖拽完成时的回调函数，此时 treeNodes 里的 parentId 已经是新 parent 的 id，
    //     targetNode 有可能是 treeNodes 的新 parent，也有可能是 sibling
    //     beforeDrop 中得不到新的 parentId，如果需要修改服务器上的 parentId 只好在 onDrop 里获取了
    //     Ajax 同步请求服务器更新数据库中节点的 parentId，更像失败则弹出错误信息，提示用户刷新界面保证数据的正确显示
    var node = treeNodes[0];
    var paperDirectoryId = node.paperDirectoryId;
    var parentPaperDirectoryId = node.parentPaperDirectoryId;
    $.rest.syncUpdate({url: Urls.REST_PAPER_DIRECTORY_PARENT_ID, urlParams: {paperDirectoryId: paperDirectoryId},
        data: {parentPaperDirectoryId: parentPaperDirectoryId}, success: function(result) {

        if (!result.success) {
            layer.msg(result.message);

            alert('移动目录失败，页面即将刷新');
            // location.replace();
        }
    }, fail: function() {
        alert('移动目录失败，页面即将刷新');
        // location.replace();
    }});
};

/**
 * 删除目录
 */
Directory.remove = function(treeNode) {
    // Ajax 同步请求服务器删除数据库中的节点，成功则从 tree 中删除此 node
    $.rest.syncRemove({url: Urls.REST_PAPER_DIRECTORIES_BY_ID, urlParams: {paperDirectoryId: treeNode.paperDirectoryId}, success: function(result) {
        if (result.success) {
            window.tree.removeNode(treeNode);
        } else {
            layer.msg(result.message);
        }
    }});
};

/**
 * 拖拽试卷到目录上时移动试卷到此目录中
 *
 * @param  {string} newPaperDirectoryId 目录 id
 * @param  {string} newPaperDirectoryName
 * @return 无返回值
 */
Directory.movePaperToDirectoryOnDrop = function(newPaperDirectoryId, newPaperDirectoryName) {
    var paperName = DnD.$draggedElement.attr('data-name');
    var paperId = DnD.$draggedElement.attr('data-id');
    var paperIndex = DnD.$draggedElement.attr('data-index');
    var paperDirectoryId = DnD.$draggedElement.attr('data-paper-directory-id');

    if (paperDirectoryId === newPaperDirectoryId) {
        layer.msg('已经在当前目录，不需要移动');
        return;
    }

    // 修改选中的试卷的目录
    $.rest.update({url: Urls.REST_PAPERS_REDIRECTORY, data: {paperIds: [paperId], paperDirectoryId: newPaperDirectoryId},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        // 成功移动试卷到目录后，更新 Vue 对象 window.vuePapers 的数据，更新界面
        window.vuePapers.papers.splice(paperIndex, 1);
        layer.msg('{0} 移动到了 {1}'.format(paperName, newPaperDirectoryName));
    }});
};

/*-----------------------------------------------------------------------------|
 |                                    Tree                                     |
 |----------------------------------------------------------------------------*/
function Tree() {
}

Tree.needCreateNewNode = false; // 需要创建新节点时为 true
Tree.newNodeParentNode = null;  // 新节点的父节点

/**
 * 初始化目录树
 */
Tree.init = function() {
    Tree.papersCount = []; // 目录下试卷的数量

    Tree.setting = {
        data: {
            simpleData: {
                enable:  true, // 启用数组结构的数据创建 zTree
                idKey:   'paperDirectoryId',       // 定义 node id 的 key，可自定义
                pIdKey:  'parentPaperDirectoryId', // 定义 parent id 的 key，可自定义
                rootPId: 0 // 根节点的 parentId，这个节点是看不到的，因为不存在
            },
            keep: {
                parent: true
            }
        },
        edit: {
            enable: true, // [*] 启用拖拽，重命名和删除等编辑操作
            showRemoveBtn: false,
            showRenameBtn: false
        },
        view: {
            selectedMulti: false,     // [*] 为 true 时可选择多个节点，为 false 只能选择一个，默认为 true
            showIcon: false,
            addDiyDom: Tree.addDiyDom
        },
        // async: {
        //     enable: true,
        //     type: 'get',
        //     url: function(treeId, treeNode) {
        //         // treeNode 不存在时表示需要加载根节点下的数据
        //         var paperDirectoryId = treeNode ? treeNode.paperDirectoryId : 0;
        //         return Urls.REST_PAPER_SUBDIRECTORIES.format({paperDirectoryId: paperDirectoryId});
        //     },
        //     dataFilter: function(treeId, parentNode, result) {
        //         if (result.success) {
        //             var directories = result.data;
        //             for (var i = 0; i < directories.length; i++) {
        //                 directories[i].isParent = true;
        //             }
        //             return directories;
        //         }
        //
        //         return null;
        //     }
        // },
        callback: {
            beforeRename: Directory.rename,
            onDrop: Directory.changeParent,
            onClick: Directory.getPapers,
            onRightClick: Tree.showMenu,
            onExpand: function(event, treeId, treeNode) {
                Tree.showPapersCount(treeNode.children);

                // 展开后再创建
                if (Tree.needCreateNewNode) {
                    Tree.needCreateNewNode = false;
                    Directory.create(Tree.newNodeParentNode);
                }
            },
            onMouseUp: function(event, treeId, treeNode) {
                // 拖放到节点上
                if (treeNode && DnD.$draggedElement) {
                    Directory.movePaperToDirectoryOnDrop(treeNode.paperDirectoryId, treeNode.name);
                }
            }
        }
    };

    // 加载目录下试卷的数量
    Tree.loadPapersCount();

    // window.tree = $.fn.zTree.init($('#directory-tree'), Tree.setting);
    // 一次性加载所有目录
    $.rest.get({url: Urls.REST_PAPER_DIRECTORIES, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        var paperDirectories = result.data;
        for (var i=0; i<paperDirectories.length; ++i) {
            paperDirectories[i].isParent = true;
        }
        window.tree = $.fn.zTree.init($('#directory-tree'), Tree.setting, paperDirectories);

        setTimeout(function() {
            Tree.showPapersCount(Tree.getRoots());
        }, 50);

    }});

    $('#menu-item-create').click(function(event) {
        Tree.hideMenu();
        var parentNode = window.tree.getSelectedNodes()[0];

        // 如果 parentNode 没有展开，则先展开 parentNode，展开时会先从服务器加载子节点数据，
        // 展开后在回调函数 onExpand 中创建新的 node
        // 如果 parentNode 不存在，或则存在且已经展开，则直接创建新的 node
        //
        // 如果不这么做，parentNode 未展开时创建新 node，会出现重复的新 node
        // createNode 时 window.tree.addNodes() 创建了一个到 tree 中，服务器返回的 node 中还包含了新创建的这个 node
        // 导致看到了 2 个同样新创建的 node，不过在数据库中还是只有一个 node，只是显示上重复
        if (parentNode && !parentNode.open) {
            Tree.needCreateNewNode = true;
            Tree.newNodeParentNode = parentNode;
            window.tree.expandNode(parentNode, true, false, true, true);
        } else {
            Directory.create(parentNode);
        }
    });

    $('#menu-item-delete').click(function(event) {
        Tree.hideMenu();
        var nodes = window.tree.getSelectedNodes();

        if (nodes.length === 0) { return; }
        if (!confirm('确定删除 ' +  nodes[0].name + ' 吗？')) { return; } // 删除前最好是询问一下是否确定删除，防止误操作

        Directory.remove(nodes[0]); // 只删除第一个
    });

    $('#menu-item-rename').click(function(event) {
        Tree.hideMenu();
        var nodes = window.tree.getSelectedNodes();

        if (nodes.length === 0) { return; }

        window.tree.editName(nodes[0]);
    });

    $('#menu-item-expand-all').click(function(event) {
        Tree.hideMenu();
        window.tree.expandAll(true);

        setTimeout(function() {
            Tree.showPapersCount(Tree.getAllNodes());
        }, 300);
    });

    $('#menu-item-refresh-count').click(function(event) {
        Tree.hideMenu();
        Tree.loadPapersCount();
        Tree.showPapersCount(Tree.getAllNodes());
    });
};

/**
 * 右键点击时弹出菜单
 */
Tree.showMenu = function(event, treeId, treeNode) {
    var type = '';
    var x = event.clientX;
    var y = event.clientY;

    if (!treeNode) {
        type = 'root';
        window.tree.cancelSelectedNode();
    } else if (treeNode && !treeNode.noR) { // noR 属性为 true 表示禁止右键菜单
        window.tree.selectNode(treeNode);
    }

    // 不同节点显示的菜单可能不一样
    if ('root' === type) {
        $('#menu-item-delete').hide();
        $('#menu-item-rename').hide();
    } else {
        $('#menu-item-delete').show();
        $('#menu-item-rename').show();
    }

    $('#directory-tree-menu').css({left: x+'px', top: y+'px'}).show();

    $(document).on('mousedown', function(event) {
        if (!(event.target.id == 'directory-tree-menu' || $(event.target).parents('#directory-tree-menu').length>0)) {
            Tree.hideMenu();
        }
    });
};

/**
 * 隐藏菜单
 */
Tree.hideMenu = function() {
    $('#directory-tree-menu').hide();
    $(document).off('mousedown');
};

Tree.addDiyDom = function(treeId, treeNode) {
    var aObj = $('#' + treeNode.tId + '_a');
    aObj.after('<span class="count">[0]</span>');
};

Tree.getRoots = function() {
    return window.tree.getNodesByFilter(function(treeNode) {
        return (treeNode.level === 0);
    }, false);
};

Tree.getAllNodes = function() {
    return window.tree.getNodesByFilter(function(treeNode) {
        return true;
    }, false);
};

Tree.showPapersCount = function(treeNodes) {
    if (!treeNodes) {return;}

    for (var i=0; i<treeNodes.length; ++i) {
        for (var j=0; j<Tree.papersCount.length; ++j) {
            var treeNode = treeNodes[i];
            var paperCount = Tree.papersCount[j];

            if (treeNode.paperDirectoryId == paperCount.paperDirectoryId) {
                $('#' + treeNode.tId + '_a').siblings('.count').replaceWith('<span class="count">[' + paperCount.count + ']</span>');
            }
        }
    }
};

Tree.loadPapersCount = function() {
    $.rest.syncGet({url: Urls.REST_PAPER_DIRECTORIES_PAPERS_COUNT, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        Tree.papersCount = result.data;
    }});
};
