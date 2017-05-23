require(['jquery', 'vue', 'semanticUi', 'layer', 'rest', 'util', 'urls', 'knowledgePoint', 'knowledgePointGroup'], function($, Vue) {
    Util.activateSidebarItem(1);

    // 知识点数组
    window.knowledgePoints = []; // {knowledgePoint: 0, name: '', knowledgePointGroupId: 0}

    // 知识点分类数组
    window.knowledgePointGroups = []; // {knowledgePointGroupId: 0, name: ''}

    // 工具栏，选择和编辑知识点分类，创建知识点
    window.vueToolbar = new Vue({
        el: '#vue-toolbar',
        data: {
            knowledgePointGroups: window.knowledgePointGroups, // 知识点分类
            currentIndex: -1 // 选中的知识点分类的下标
        },
        methods: {
            // 弹出编辑知识点分类对话框
            popupEditKnowledgePointGroupsDialog: function() {
                layer.open({
                    type: 1,
                    // title: '知识点分类管理',
                    title: false,
                    closeBtn: 0,
                    area: ['420px', '440px'], //宽高
                    content: $('#knowledge-point-group-editor-dialog'),
                    btn: ['取消']
                });
            },
            // 弹出创建知识点对话框
            popupCreateKnowledgePointDialig: function() {
                var knowledgePoint = KnowledgePoint.emptyKnowledgePoint();
                var index = Math.max(0, this.currentIndex); // 如果没有选择则选第一个
                knowledgePoint.knowledgePointGroupId = this.knowledgePointGroups[index].knowledgePointGroupId;

                // 弹出知识点编辑对话框，-1 表示创建
                popupEditKnowledgePointsDialog(knowledgePoint, -1);
            },
            // 显示此分类下的所有知识点
            showKnowledgePoints: function(index) {
                this.currentIndex = index;
                KnowledgePointDao.loadKnowledgePoints(this.getCurrentKnowledgePointGroupId(), function(points) {
                    window.knowledgePoints.splice(0, window.knowledgePoints.length);

                    for (var i = 0; i < points.length; ++i) {
                        window.knowledgePoints.push(points[i]);
                    }
                });
            },
            // 取得下拉框中当前选中的知识点分类
            getCurrentKnowledgePointGroup: function() {
                return this.currentIndex === -1 ? {knowledgePointGroupId: 0, name: ''} : this.knowledgePointGroups[this.currentIndex];
            },
            getCurrentKnowledgePointGroupId: function() {
                return this.getCurrentKnowledgePointGroup().knowledgePointGroupId;
            }
        }
    });

    // 知识点分类编辑器的 Vue 对象
    window.vueKnowledgePointGroupEditor = new Vue({
        el: '#vue-knowledge-point-group-editor',
        data: {
            knowledgePointGroups: window.knowledgePointGroups,
            editedKnowledgePointGroup: null,
            cachedKnowledgePointGroupName: ''
        },
        methods: {
            // 进入编辑知识点分类模式
            editGroup: function(group) {
                // 保存正在编辑的知识点分类
                this.editedKnowledgePointGroup = group;
                this.cachedKnowledgePointGroupName = group.name;
            },
            // 取消编辑
            cancelEdit: function() {
                // 有时候会调用 2 次，为了不让在控制台输出错误信息，非 null 时才操作
                if (this.editedKnowledgePointGroup) {
                    this.editedKnowledgePointGroup.name = this.cachedKnowledgePointGroupName;
                    this.editedKnowledgePointGroup = null;
                }
            },
            // 创建知识点分类
            createGroup: function() {
                // 发送请求到服务器上创建知识点分类
                KnowledgePointGroupDao.create({name: '[新知识点分类]'}, function(knowledgePointGroup) {
                    window.knowledgePointGroups.push(knowledgePointGroup);
                });
            },
            // 更新知识点分类
            updateGroup: function(todo) {
                // 名字不能为空
                if (!this.editedKnowledgePointGroup.name) {
                    layer.msg('知识点分类不能为空');
                    return;
                }

                // 发送请求到服务器上更新知识点分类
                KnowledgePointGroupDao.update(this.editedKnowledgePointGroup);
                this.editedKnowledgePointGroup = null;
            },
            removeGroup: function(knowledgePointGroup) {
                var name = knowledgePointGroup.name;
                var dlg = layer.confirm('您确定删除<font color="red"> {0} </font>吗？'.format(name), {
                    title: '删除',
                    btn: ['确定', '取消']
                }, function() {
                    layer.close(dlg);
                    KnowledgePointGroupDao.remove(knowledgePointGroup, function(knowledgePointGroup) {
                        // 从知识点分类数组中删除
                        var index = window.knowledgePointGroups.indexOf(knowledgePointGroup);
                        window.knowledgePointGroups.splice(index, 1);
                    });
                });
            }
        },
        directives: {
            'group-focus': function(el, binding) {
                if (binding.value) {
                    el.focus();
                }
            }
        }
    });

    // 知识点编辑器的 Vue 对象
    window.vueKnowledgePointEditor = new Vue({
        el: '#vue-knowledge-point-editor',
        data: {
            knowledgePointGroups: window.knowledgePointGroups, // 知识点分类
            knowledgePoint: KnowledgePoint.emptyKnowledgePoint()
        },
        watch: {
            knowledgePoint: function(oldValue, newValue) {
                // console.log(JSON.stringify(this.knowledgePoint));
            }
        }
    });

    // 知识点列表的 Vue 对象
    window.vueKnowledgePoints = new Vue({
        el: '#vue-knowledge-points',
        data: {
            knowledgePoints: window.knowledgePoints
        },
        methods: {
            editknowledgePoint: function(index) {
                var knowledgePoint = $.extend(true, {}, this.knowledgePoints[index]);
                popupEditKnowledgePointsDialog(knowledgePoint, index);
            }
        }
    });

    /**
     * 弹出编辑知识点窗口，index 为要编辑的知识点 window.knowledgePoints 的下标，index 为 -1 时表示创建，其它时表示更新。
     *
     * @param  {object} knowledgePoint 要编辑的知识点
     * @param  {int}    index          要编辑的知识点 knowledgePoints 的下标
     * @return 无返回值
     */
    function popupEditKnowledgePointsDialog(knowledgePoint, index) {
        window.vueKnowledgePointEditor.knowledgePoint = knowledgePoint; // 先更新知识点编辑器中的知识点

        // 选择当前的知识点
        $('#vue-knowledge-point-editor .dropdown').dropdown('set selected', knowledgePoint.knowledgePointGroupId);

        layer.open({
            type: 1,
            // title: '知识点管理',
            title: false,
            closeBtn: 0,
            area: ['420px', '240px'], //宽高
            content: $('#knowledge-point-editor-dialog'),
            btn: ['保存', '删除', '取消'],
            // 创建和更新
            btn1: function() {
                // input 是通过 val() 来设置值的，v-model 无效
                var kpgId = $('#vue-knowledge-point-editor input[name="knowledgePointGroupId"]').val();
                var name  = vueKnowledgePointEditor.knowledgePoint.name; // 知识点的名字

                knowledgePoint.knowledgePointGroupId = kpgId;

                if (!name) {
                    layer.msg('知识点不能为空');
                    return;
                }

                // 如果 index 等于 -1 则为创建，否则为更新
                if (index === -1) {
                    // 创建知识点
                    KnowledgePointDao.create(new KnowledgePoint(0, name, kpgId), function(newKnowledgePoint) {
                        // 知识点创建成功后，如果它属于当前的知识点分类则加入到当前的知识点中，显示到界面上
                        if (newKnowledgePoint.knowledgePointGroupId === window.vueToolbar.getCurrentKnowledgePointGroupId()) {
                            window.knowledgePoints.push(newKnowledgePoint);
                        }

                        window.vueKnowledgePointEditor.knowledgePoint = KnowledgePoint.emptyKnowledgePoint();
                        layer.msg('知识点创建成功');
                    });
                } else {
                    // 更新知识点
                    KnowledgePointDao.update(knowledgePoint, function() {
                        // 更新界面上的知识点，如果不是当前知识点分类的，则删除
                        if (knowledgePoint.knowledgePointGroupId === window.vueToolbar.getCurrentKnowledgePointGroupId()) {
                            window.knowledgePoints.splice(index, 1, knowledgePoint);
                        } else {
                            window.knowledgePoints.splice(index, 1);
                        }

                        layer.msg('知识点保存成功');
                    });
                }
            },
            // 删除
            btn2: function() {
                // 知识点不存在则不能删除
                if (knowledgePoint.knowledgePointId === 0) {
                    layer.msg('还没有创建知识点，不需要删除');
                    return false;
                }

                var dlg = layer.confirm('您确定删除<font color="red"> {0} </font>吗？'.format(knowledgePoint.name), {
                    title: '删除',
                    btn: ['确定', '取消']
                }, function() {
                    layer.close(dlg);

                    KnowledgePointDao.remove(knowledgePoint.knowledgePointId, function() {
                        window.knowledgePoints.splice(index, 1); // 删除界面上的知识点
                        layer.closeAll();
                        layer.msg('知识点删除成功');
                    });
                });

                return false;
            }
        });
    }

    // 加载所有的知识点分类
    KnowledgePointGroupDao.loadKnowledgePointGroups(function(groups) {
        window.knowledgePointGroups.splice(0, window.knowledgePointGroups.length);

        for (var i=0; i<groups.length; i++) {
            window.knowledgePointGroups.push(groups[i]);
        }
    });

    $('.ui.dropdown').dropdown();
});
