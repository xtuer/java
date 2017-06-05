function EditableKnowledgePointGroupTree(treeElementId) {
    this.treeElementId = treeElementId; // 树的元素 id
    this.tree = null;
    this.init();
}

/**
 * 初始化知识点分类树
 *
 * @return 无返回值
 */
EditableKnowledgePointGroupTree.prototype.init = function() {
    var self = this;
    var setting = self.getSettings();

    KnowledgePointDao.loadKnowledgePointGroups(function(knowledgePointGroups) {
        for (var i = 0; i < knowledgePointGroups.length; ++i) {
            knowledgePointGroups[i].isParent = true;
            knowledgePointGroups[i].knowledgePointGroupId = knowledgePointGroups[i].knowledgePointId;
            knowledgePointGroups[i].parentKnowledgePointGroupId = knowledgePointGroups[i].parentKnowledgePointId;
        }

        self.tree = $.fn.zTree.init($("#" + self.treeElementId), setting, knowledgePointGroups);

        // 下一个事件循环中处理显示第一个目录下的试卷
        setTimeout(function() {
            // 选中第一个节点
            var firstRoot = TreeUtils.getRoots(self.tree)[0];
            window.vueToolbar.knowledgePointGroupId = firstRoot.knowledgePointGroupId;

            if (firstRoot) {
                self.tree.selectNode(firstRoot);
                KnowledgePointDao.loadKnowledgePoints(window.vueToolbar.knowledgePointGroupId, function(points) {
                    for (var i = 0; i < points.length; ++i) {
                        window.knowledgePoints.push(points[i]);
                    }
                });
            }
        }, 0);
    });

    // 创建分类
    $('#knowledge-point-group-tree-menu .create').click(function(event) {
        self.hideMenu();
        var parentNode = self.tree.getSelectedNodes()[0]; // 父目录
        var parentId = parentNode ? parentNode.knowledgePointGroupId : 0; // 如果 parentNode 为 null，则说明需要创建 ROOT 节点
        var group = KnowledgePoint.newKnowledgePointGroup(parentId);

        KnowledgePointDao.create(group, function(group) {
            group.isParent = true;
            self.tree.addNodes(parentNode, -1, group, false);
        });
    });

    // 删除分类
    $('#knowledge-point-group-tree-menu .delete').click(function(event) {
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
            KnowledgePointDao.remove(node.knowledgePointId, function() {
                self.tree.removeNode(node);
            });
        });
    });

    $('#knowledge-point-group-tree-menu .rename').click(function(event) {
        self.hideMenu();
        var nodes = self.tree.getSelectedNodes();
        if (nodes.length === 0) { return; }

        self.tree.editName(nodes[0]); // 编辑第一个被选中的节点
    });
};

/**
 * 弹出菜单
 */
EditableKnowledgePointGroupTree.prototype.showMenu = function(x, y, treeNode) {
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
        $('#knowledge-point-group-tree-menu .delete').hide();
        $('#knowledge-point-group-tree-menu .rename').hide();
    } else {
        $('#knowledge-point-group-tree-menu .delete').show();
        $('#knowledge-point-group-tree-menu .rename').show();
    }

    $('#knowledge-point-group-tree-menu').css({left: x+'px', top: y+'px'}).show();

    $(document).on('mousedown', function(event) {
        if (!(event.target.id == 'knowledge-point-group-tree-menu' || $(event.target).parents('#knowledge-point-group-tree-menu').length>0)) {
            self.hideMenu();
        }
    });
};

/**
 * 隐藏菜单
 */
EditableKnowledgePointGroupTree.prototype.hideMenu = function() {
    $('#knowledge-point-group-tree-menu').hide();
    $(document).off('mousedown');
};

/**
 * 构建 zTree 的配置
 *
 * @return {Object} JSON 配置对象
 */
EditableKnowledgePointGroupTree.prototype.getSettings = function() {
    var self = this;

    var setting = {
        data: {
            simpleData: {
                enable: true,
                idKey:  'knowledgePointGroupId',
                pIdKey: 'parentKnowledgePointGroupId',
                rootPId: 0
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
            selectedMulti: false, // [*] 为 true 时可选择多个节点，为 false 只能选择一个，默认为 true
            showIcon: false
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
                    layer.msg('分类不能为空');
                    return false;
                }

                return KnowledgePointDao.rename(treeNode.knowledgePointId, newName);
            },
            onDrop: function(event, treeId, treeNodes, targetNode, moveType, isCopy) {
                // [*] 拖拽完成时的回调函数，此时 treeNodes 里的 parentId 已经是新 parent 的 id，
                //     targetNode 有可能是 treeNodes 的新 parent，也有可能是 sibling
                //     beforeDrop 中得不到新的 parentId，如果需要修改服务器上的 parentId 只好在 onDrop 里获取了
                //     Ajax 请求服务器更新数据库中节点的 parentId，更像失败则弹出错误信息，提示用户刷新界面保证数据的正确显示
                var node = treeNodes[0];
                var knowledgePointId = node.knowledgePointGroupId;
                var newParentKnowledgePointId = node.parentKnowledgePointGroupId;

                KnowledgePointDao.updateParent(knowledgePointId, newParentKnowledgePointId, null, function() {
                    alert('移动失败，页面即将刷新');
                    location.reload(); // 移动目录失败刷新页面
                });
            },
            onClick: function(event, treeId, treeNode, clickFlag) {
                var knowledgePointGroupId = treeNode.knowledgePointGroupId; // 目录节点的 id
                window.vueToolbar.knowledgePointGroupId = knowledgePointGroupId;
                window.knowledgePoints.empty();

                KnowledgePointDao.loadKnowledgePoints(knowledgePointGroupId, function(points) {
                    for (var i = 0; i < points.length; ++i) {
                        window.knowledgePoints.push(points[i]);
                    }
                });
            },
            onRightClick: function(event, treeId, treeNode) {
                // 在右键位置出弹出菜单
                var x = event.clientX + 2;
                var y = event.clientY + 2;
                self.showMenu(x, y, treeNode);
            },
            onMouseUp: function(event, treeId, treeNode) {
                // 拖拽试卷到目录节点上，则移动试卷到此目录
                if (treeNode && DnD.$draggedElement) {
                    // 获得试卷的信息，保存在 DnD.$draggedElement 的属性中
                    var $src    = DnD.$draggedElement;
                    var name    = $src.attr('data-name');
                    var id      = $src.attr('data-id');
                    var index   = $src.attr('data-index');
                    var groupId = $src.attr('data-group-id');

                    var newGroupId = treeNode.knowledgePointGroupId;
                    var newGroupName = treeNode.name;

                    if (groupId === newGroupId) {
                        layer.msg('已经在当前分类，不需要移动');
                        return;
                    }

                    // 移动到分类，从当前分类中删除
                    KnowledgePointDao.updateParent(id, newGroupId, function() {
                        window.knowledgePoints.remove(index);
                        layer.msg('{0} 移动到了 {1}'.format(name, newGroupName));
                    });
                }
            }
        }
    };

    return setting;
};
