require(['jquery', 'vue', 'semanticUi', 'layer', 'util', 'rest', 'urls'], function($, Vue) {
    Util.activateSidebarItem(2);

    // 知识点分类数组
    var knowledgePointGroups = [];

    // 选择知识点分类的下拉框的 Vue 对象
    var vueKnowledgePointGroup = new Vue({
        el: '#vue-knowledge-point-group',
        data: {
            knowledgePointGroups: knowledgePointGroups
        }
    });

    // 知识点分类编辑器的 Vue 对象
    var vueKnowledgePointGroupEditor = new Vue({
        el: '#vue-knowledge-point-group-editor',
        data: {
            knowledgePointGroups: knowledgePointGroups,
            currentIndex: -1
        },
        methods: {
            showInput: function(index, event) {
                this.currentIndex = index;
                var $input = this.getInput();
                $input.siblings('.name').show(); // 显示当前 input 所在行的 name
                // 找到要插入的行，隐藏 name，然后插入 input
                $td = $(event.target).parent().prev();
                $('.name', $td).hide();
                $input.val(knowledgePointGroups[index].name).appendTo($td).show().focus();
            },
            hideInput: function() {
                // 隐藏 input，把相应的 name 显示出来，然后把 input 从 item 中移走，防止 item 被删除时 input 也被删除
                var $input = this.getInput();
                $input.hide();
                $input.siblings('.name').show();
                $input.appendTo('#vue-knowledge-point-group-editor');
            },
            createItem: function() {
                var group = {name: '[新知识点分类]'};
                // 发送请求到服务器上创建 item，然后更新到 items
                $.rest.create({url: Urls.REST_KNOWLEDGE_POINT_GROUPS, data: group, success: function(result) {
                    if (!result.success) {
                        layer.msg(result.message);
                        return;
                    }

                    group.knowledgePointGroupId = result.data.knowledgePointGroupId;
                    knowledgePointGroups.push(group);
                }});
            },
            updateItem: function() {
                var self = this;
                var $input = self.getInput();
                var name = $.trim($input.val());
                var knowledgePointGroupId = knowledgePointGroups[self.currentIndex].knowledgePointGroupId;

                // 名字不能为空
                if (!name) {
                    layer.msg('知识点分类不能为空');
                    return;
                }

                // 发送请求到服务器上更新知识点分类的名字，然后更新到 knowledgePointGroups
                $.rest.update({url: Urls.REST_KNOWLEDGE_POINT_GROUPS_BY_ID, urlParams: {knowledgePointGroupId: knowledgePointGroupId},
                    data: {name: name}, success: function(result) {
                    if (!result.success) {
                        layer.msg(result.message);
                        return;
                    }

                    knowledgePointGroups[self.currentIndex].name = name;
                    knowledgePointGroups.splice(self.currentIndex, 1, knowledgePointGroups[self.currentIndex]);
                    $input.siblings('.name').show();
                    self.hideInput();
                }});
            },
            removeItem: function(index) {
                // 删除前最好是询问一下是否确定删除，防止误操作
                var dlg = layer.confirm('您确定删除 ' +  knowledgePointGroups[index].name + ' 吗？', {
                    title: '删除',
                    btn: ['确定', '取消']
                }, function() {
                    layer.close(dlg);

                    // 发送请求到服务器先删除数据，然后再从 knowledgePointGroups 里删除
                    var knowledgePointGroupId = knowledgePointGroups[index].knowledgePointGroupId;

                    $.rest.remove({url: Urls.REST_KNOWLEDGE_POINT_GROUPS_BY_ID,
                        urlParams: {knowledgePointGroupId: knowledgePointGroupId},
                        success: function(result) {
                        if (!result.success) {
                            layer.msg(result.message);
                            return;
                        }

                        knowledgePointGroups.splice(index, 1);
                    }});
                });
            },
            getInput: function() {
                return $('#vue-knowledge-point-group-editor input'); // 返回 input
            }
        }
    });

    // 从服务器加载知识点分类
    $.rest.get({url: Urls.REST_KNOWLEDGE_POINT_GROUPS, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        var groups = result.data;
        for (var i = 0; i < groups.length; ++i) {
            knowledgePointGroups.push(groups[i]);
        }
    }});

    var knowledgePoints = []; // 知识点
    for (var i = 0; i < 100; ++i) {
        knowledgePoints.push({
            id: i,
            name: parseInt(Math.random() * 1000, 10)
        });
    }

    var vueKnowledgePoints = new Vue({
        el: '#vue-knowledge-points',
        data: {
            knowledgePoints: knowledgePoints
        },
        methods: {
            editTag: function(index) {
                layer.msg(index);
            }
        }
    });

    $('#admin-knowledge-point-group-button').click(function(event) {
        layer.open({
            type: 1,
            title: '管理知识点分类',
            area: ['420px', '440px'], //宽高
            content: $('#edit-knowledge-group-dialog')
        });
    });

    $('.ui.dropdown').dropdown();

    $(document).on('click', '.ui.dropdown .menu .item', function(event) {
        layer.msg($(this).text());
    });
});
