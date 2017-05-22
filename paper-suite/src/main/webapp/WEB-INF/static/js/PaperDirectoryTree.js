/*-----------------------------------------------------------------------------|
 |                                  CountTree                                  |
 |----------------------------------------------------------------------------*/

 /**
  * 树的节点
  *
  * @param {int} id       节点 id
  * @param {int} parentId 父节点 id
  * @param {int} count    节点的 count 值
  */
function CountTreeNode(id, parentId, count) {
    this.id = id;
    this.parentId = parentId;
    this.count = count;
    this.children = []; // 子节点数组
}

/**
 * 使用节点数据的数组创建树。
 * 树的节点存储在数组中，节点之间的关系通过 children 数组来存储。
 *
 * @param {Array} nodes 节点数据的数组
 */
function CountTree(nodes) {
    this.root = null;    // 根节点
    this.treeNodes = []; // 树所有节点的数组

    this.init(nodes);
}

CountTree.prototype.init = function(nodes) {
    this.root = new CountTreeNode(0, null, 'ROOT', 0); // 树的虚拟根节点

    // 构建树的所有节点的数组
    for (var i=0; i<nodes.length; ++i) {
         var node = nodes[i];
         // this.treeNodes.push(new CountTreeNode(node.id, node.parentId, node.count));
         this.treeNodes.push(new CountTreeNode(node.paperDirectoryId, node.parentPaperDirectoryId, node.count));
    }

    // 根据节点之间的关系建立树节点之间的父子引用
    for (var i=0; i<this.treeNodes.length; ++i) {
        var node = this.treeNodes[i];
        var parentIndex = this.indexOfParent(node); // 查找节点的父节点的下标

        // 如果找不到父节点，说明是一级节点，添加为 root 的子节点
        // 如果找到父节点，则把其加入其父节点的子节点数组
        if (-1 === parentIndex) {
            this.root.children.push(node);
        } else {
            this.treeNodes[parentIndex].children.push(node);
        }
    }
};

/**
 * 查找节点的父节点的下标
 *
 * @param  {Object} treeNode  节点
 * @param  {Object} treeNodes 所有节点的数组
 * @return 返回节点的父节点的下标，如果找不到，则返回 -1
 */
CountTree.prototype.indexOfParent = function(treeNode) {
    for (var i=0; i<this.treeNodes.length; ++i) {
        if (treeNode.parentId === this.treeNodes[i].id) {
            return i;
        }
    }

    return -1;
};

/**
 * 每个节点的 count 值为其所有子节点的 count 值的和加上它的 count 值
 *
 * @param  {Object} treeNode 节点
 * @return 返回计算后节点的 count 值
 */
CountTree.recursiveCount = function(treeNode) {
    var sum = 0;

    for (var i=0; i<treeNode.children.length; ++i) {
        sum += CountTree.recursiveCount(treeNode.children[i]);
    }

    treeNode.sum = treeNode.count + sum; // 用新的属性 sum 记录，这样方便验算，也可以直接用 treeNode.count

    return treeNode.sum;
};

/*-----------------------------------------------------------------------------|
 |                                  TreeUtils                                  |
 |----------------------------------------------------------------------------*/
function TreeUtils() {

}

/**
 * 取得所有的跟节点，ztree 树可以有多个跟节点，其实是一个森林
 *
 * @param  {object} ztree ztree 对象
 * @return 返回树的所有跟节点
 */
TreeUtils.getRoots = function(ztree) {
    return ztree.getNodesByFilter(function(treeNode) {
        return (treeNode.level === 0);
    }, false);
};

/**
 * 取得树的所有节点
 *
 * @param  {object} ztree ztree 对象
 * @return 返回树的所有节点
 */
TreeUtils.getAllNodes = function(ztree) {
    return ztree.getNodesByFilter(function(treeNode) {
        return true;
    }, false);
};

/*-----------------------------------------------------------------------------|
 |                         EditablePaperDirectoryTree                          |
 |----------------------------------------------------------------------------*/
function EditablePaperDirectoryTree(treeElementId) {
    this.treeElementId = treeElementId; // 树的元素 id
    this.tree = null;
    this.setting = null;
    this.paperCounts = []; // 所有目录下试卷的数量

    this.init();
}

/**
 * 初始化目录树
 */
EditablePaperDirectoryTree.prototype.init = function() {
    var self = this;
    self.setting = self.getSettings();

    // 加载目录下试卷的数量
    self.loadPapersCount();

    // 一次性加载所有目录
    PaperDirectoryDao.loadPaperDirectories(function(paperDirectories) {
        // 所有节点都是非叶节点
        for (var i=0; i<paperDirectories.length; ++i) {
            paperDirectories[i].isParent = true;
        }

        // 创建 zTree 对象
        self.tree = $.fn.zTree.init($('#' + self.treeElementId), self.setting, paperDirectories);

        // 下一个事件循环中处理显示第一个目录下的试卷
        setTimeout(function() {
            self.showPapersCount(TreeUtils.getRoots(self.tree));

            // 选中第一个节点
            var firstRoot = TreeUtils.getRoots(self.tree)[0];
            $('#vue-papers').attr('data-paper-directory-id', firstRoot.paperDirectoryId);
            if (firstRoot) {
                self.tree.selectNode(firstRoot);
                PaperDirectoryDao.loadPapers(firstRoot.paperDirectoryId, function(papers) {
                    window.vuePapers.papers = papers;
                });
            }
        }, 0);
    });

    // 创建新节点
    $('#menu-item-create').click(function(event) {
        self.hideMenu();
        var parentNode = self.tree.getSelectedNodes()[0]; // 父目录
        var parentId = parentNode ? parentNode.paperDirectoryId : 0; // 如果 parentNode 为 null，则说明需要创建 ROOT 节点

        PaperDirectoryDao.create(parentId, function(node) {
            node.isParent = true;
            self.tree.addNodes(parentNode, -1, node, false);
        });
    });

    // 删除节点
    $('#menu-item-delete').click(function(event) {
        self.hideMenu();
        var nodes = self.tree.getSelectedNodes();
        if (nodes.length === 0) { return; }

        var node = nodes[0]; // 只删除第一个
        var dlg = layer.confirm('您确定删除<font color="red"> {0} </font>吗？'.format(node.name), {
            title: '删除',
            btn: ['确定', '取消']
        }, function() {
            layer.close(dlg);

            // 从服务器删除目录成功后从树中也删除此目录节点
            PaperDirectoryDao.remove(node, function() {
                self.tree.removeNode(node);
            });
        });
    });

    // 重命名
    $('#menu-item-rename').click(function(event) {
        self.hideMenu();
        var nodes = self.tree.getSelectedNodes();

        if (nodes.length === 0) { return; }

        self.tree.editName(nodes[0]); // 编辑第一个被选中的节点
    });

    // 展开所有节点
    $('#menu-item-expand-all').click(function(event) {
        self.hideMenu();
        self.tree.expandAll(true); // 会创建还不存在的 DOM，因为使用的是 lazy loading

        // 下一个事件循环时显示试卷的数量，因为此次事件循环中 DOM 可能还没有创建好
        setTimeout(function() {
            self.showPapersCount(TreeUtils.getAllNodes(self.tree));
        }, 0);
    });

    // 刷新目录上试卷的数量，从服务器重新加载试卷的数量并更新到 DOM
    $('#menu-item-refresh-count').click(function(event) {
        self.hideMenu();
        self.loadPapersCount();
        self.showPapersCount(TreeUtils.getAllNodes(self.tree));
    });
};

/**
 * 弹出菜单
 */
EditablePaperDirectoryTree.prototype.showMenu = function(x, y, treeNode) {
    var self = this;
    var type = '';

    if (!treeNode) {
        type = 'root';
        self.tree.cancelSelectedNode();
    } else if (treeNode && !treeNode.noR) { // noR 属性为 true 表示禁止右键菜单
        self.tree.selectNode(treeNode);
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
            self.hideMenu();
        }
    });
};

/**
 * 隐藏菜单
 */
EditablePaperDirectoryTree.prototype.hideMenu = function() {
    $('#directory-tree-menu').hide();
    $(document).off('mousedown');
};

/**
 * 在节点的名字后面加上此目录下试卷的数量 DOM
 *
 * @param  {string} treeId   zTree id
 * @param  {object} treeNode zTree node object
 * @return 无返回值
 */
EditablePaperDirectoryTree.prototype.addCounter = function(treeId, treeNode) {
    var $node = $('#' + treeNode.tId + '_a');
    $node.after('<span class="count">[0]</span>');
};

/**
 * 在节点的名字后面显示此目录下试卷的数量
 *
 * @param  {array} treeNodes zTree nodes
 * @return 无返回值
 */
EditablePaperDirectoryTree.prototype.showPapersCount = function(treeNodes) {
    if (!treeNodes) {return;}

    for (var i=0; i<treeNodes.length; ++i) {
        for (var j=0; j<this.paperCounts.length; ++j) {
            var treeNode = treeNodes[i];
            var paperCount = this.paperCounts[j];

            if (treeNode.paperDirectoryId == paperCount.paperDirectoryId) {
                $('#' + treeNode.tId + '_a').siblings('.count').replaceWith('<span class="count">[' + paperCount.count + ']</span>');
            }
        }
    }
};

EditablePaperDirectoryTree.prototype.loadPapersCount = function() {
    var self = this;
    PaperDirectoryDao.loadPaperCounts(function(paperCounts) {
        // self.paperCounts = paperCounts;
        for (var j=0; j<paperCounts.length; ++j) {
            // paperId 不存在，则说明此目录下没有试卷，但是服务器端 left join + group by 此时的数字为 1
            if (!paperCounts[j].paperId) {
                paperCounts[j].count = 0;
            }
        }

        // 递归的计算目录上的试卷数量
        var tree = new CountTree(paperCounts);
        CountTree.recursiveCount(tree.root);
        self.paperCounts = [];

        for (var i=0; i<tree.treeNodes.length; ++i) {
            var node = tree.treeNodes[i];
            self.paperCounts.push({paperDirectoryId: node.id, parentPaperDirectoryId: node.parentId, count: node.sum});
        }
    });
};

/**
 * 构建 zTree 的配置
 *
 * @return {Object} JSON 配置对象
 */
EditablePaperDirectoryTree.prototype.getSettings = function() {
    var self = this;
    var setting = {
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
            addDiyDom: self.addCounter
        },
        callback: {
            beforeRename: function(treeId, treeNode, newName, isCancel) {
                // 1. 按下 ESC 时 isCancel 为 true，取消重命名
                // 2. 把名字前后的空格去掉
                // 3. 名字不能为空或则全是空格
                // 4. 请求服务器重命名
                //    4.1. 成功则返回 true 更新目录树上目录的名字
                //    4.2. 失败返回 false 并提示错误信息
                if (isCancel) {
                    return;
                }

                newName = $.trim(newName);

                if (!newName) {
                    layer.msg('目录名不能为空');
                    return false;
                }

                return PaperDirectoryDao.rename(treeNode, newName);
            },
            onDrop: function(event, treeId, treeNodes, targetNode, moveType, isCopy) {
                // [*] 拖拽完成时的回调函数，此时 treeNodes 里的 parentId 已经是新 parent 的 id，
                //     targetNode 有可能是 treeNodes 的新 parent，也有可能是 sibling
                //     beforeDrop 中得不到新的 parentId，如果需要修改服务器上的 parentId 只好在 onDrop 里获取了
                //     Ajax 请求服务器更新数据库中节点的 parentId，更像失败则弹出错误信息，提示用户刷新界面保证数据的正确显示
                var node = treeNodes[0];
                PaperDirectoryDao.updateParent(node, function() {
                    alert('移动目录失败，页面即将刷新');
                    location.replace(); // 移动目录失败刷新页面
                });
            },
            onClick: function(event, treeId, treeNode, clickFlag) {
                var paperDirectoryId = treeNode.paperDirectoryId; // 目录节点的 id

                $('#tool-bar .toggle-buttons').show();
                $('#vue-knowledge-points').slideUp();
                $('#vue-papers').attr('data-paper-directory-id', paperDirectoryId);

                window.vuePapers.papers = [];
                PaperDirectoryDao.loadPapers(paperDirectoryId, function(papers) {
                    window.vuePapers.papers = papers;
                });
            },
            onRightClick: function(event, treeId, treeNode) {
                // 在右键位置出弹出菜单
                var x = event.clientX + 2;
                var y = event.clientY + 2;
                self.showMenu(x, y, treeNode);
            },
            onExpand: function(event, treeId, treeNode) {
                // 展开节点时更新节点上试卷的数量
                self.showPapersCount(treeNode.children);
            },
            onMouseUp: function(event, treeId, treeNode) {
                // 拖拽试卷到目录节点上，则移动试卷到此目录
                if (treeNode && DnD.$draggedElement) {
                    // 获得试卷的信息，保存在 DnD.$draggedElement 的属性中
                    var $src = DnD.$draggedElement;
                    var paperName  = $src.attr('data-name');
                    var paperId    = $src.attr('data-id');
                    var paperIndex = $src.attr('data-index');
                    var paperDirectoryId = $src.attr('data-paper-directory-id');
                    var newPaperDirectoryId = treeNode.paperDirectoryId;
                    var newPaperDirectoryName = treeNode.name;

                    if (paperDirectoryId === newPaperDirectoryId) {
                        layer.msg('已经在当前目录，不需要移动');
                        return;
                    }

                    PaperDirectoryDao.movePaperToPaperDirectory(paperId, newPaperDirectoryId, function() {
                        // 成功移动试卷到目录后，更新 Vue 对象 window.vuePapers 的数据，更新界面
                        window.vuePapers.papers.splice(paperIndex, 1);
                        layer.msg('{0} 移动到了 {1}'.format(paperName, newPaperDirectoryName));
                    });
                }
            }
        }
    };

    return setting;
};
