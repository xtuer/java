require(['jquery', 'vue', 'semanticUi', 'ztree', 'layer', 'rest', 'util', 'urls', 'knowledgePoint', 'knowledgePointGroupTree'], function($, Vue) {
    Util.activateSidebarItem(1);

    window.vueToolbar = new Vue({
        el: '#toolbar',
        data: {
            knowledgePointGroupId: 0
        },
        methods: {
            create: function() {
                layer.prompt({
                    formType: 3,
                    value: '',
                    title: '添加知识点'
                }, function(value, index, elem) {
                    var point = KnowledgePoint.newKnowledgePoint(value, window.vueToolbar.knowledgePointGroupId);

                    KnowledgePointDao.create(point, function(point) {
                        window.knowledgePoints.push(point);
                        layer.close(index);
                    });
                });
            }
        }
    });

    window.knowledgePoints = [];

    window.vueKnowledgePoints = new Vue({
        el: '#vue-knowledge-points',
        data: {
            knowledgePoints: window.knowledgePoints
        },
        watch: {
            knowledgePoints: function() {
                this.$nextTick(function() {
                    $(document).off('mousedown', '#vue-knowledge-points .dnd-point', DnD.mouseDown);
                    $(document).on('mousedown',  '#vue-knowledge-points .dnd-point', DnD.mouseDown);
                });
            }
        },
        methods: {
            edit: function(point) {
                layer.prompt({
                    formType: 3,
                    value: point.name,
                    title: '编辑知识点'
                }, function(value, index){
                    var name = $.trim(value);
                    if (!name) {
                        alert('知识点不能为空');
                        return;
                    }

                    var ok = KnowledgePointDao.rename(point.knowledgePointId, name);

                    if (ok) {
                        point.name = name;
                        layer.close(index);
                    }
                });

            },
            remove: function(index) {
                var self = this;
                var point = this.knowledgePoints[index];

                var dlg = layer.confirm('您确定删除<font color="red"> {0} </font>吗？'.format(point.name), {
                    title: '删除',
                    btn: ['确定', '取消']
                }, function() {
                    layer.close(dlg);

                    // 从服务器删除目录成功后从树中也删除此目录节点
                    KnowledgePointDao.remove(point.knowledgePointId, function() {
                        self.knowledgePoints.remove(index);
                    });
                });
            }
        }
    });

    new EditableKnowledgePointGroupTree('knowledge-point-group-tree');
});
