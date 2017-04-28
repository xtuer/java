/*-----------------------------------------------------------------------------|
 |                               KnowledgePoint                                |
 |----------------------------------------------------------------------------*/
/**
 * 知识点的类
 *
 * @param {int} knowledgePointId      知识点 id
 * @param {string} name               知识点
 * @param {int} knowledgePointGroupId 知识点分类 id
 */
function KnowledgePoint(knowledgePointId, name, knowledgePointGroupId) {
    this.knowledgePointId = knowledgePointId;
    this.name = name;
    this.knowledgePointGroupId = knowledgePointGroupId;
}

/**
 * 创建一个空的知识点
 *
 * @return {object} 空的知识点对象
 */
KnowledgePoint.emptyKnowledgePoint = function() {
    return new KnowledgePoint(0, '', 0);
};

/**
 * 创建知识点
 * @param  {object} knowledgePoint KnowledgePoint 对象
 * @return 无返回值
 */
KnowledgePoint.create = function(knowledgePoint) {
    $.rest.create({url: Urls.REST_KNOWLEDGE_POINTS, data: knowledgePoint, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        // 知识点创建成功后，如果它属于当前的知识点分类则加入到当前的知识点中，显示到界面上
        var currentKnowledgePointGroupId = window.vueToolbar.getCurrentKnowledgePointGroup().knowledgePointGroupId;
        if (knowledgePoint.knowledgePointGroupId === currentKnowledgePointGroupId) {
            knowledgePoints.push(result.data);
        }

        window.vueKnowledgePointEditor.knowledgePoint = KnowledgePoint.emptyKnowledgePoint();
        layer.msg('知识点创建成功');
    }});
};

/**
 * 更新知识点
 *
 * @param  {object} knowledgePoint KnowledgePoint 对象
 * @param  {int} index             需要更新的知识点在 window.knowledgePoints 中的下标
 * @return 无返回值
 */
KnowledgePoint.update = function(knowledgePoint, index) {
    $.rest.update({url: Urls.REST_KNOWLEDGE_POINTS_BY_ID, urlParams: {knowledgePointId: knowledgePoint.knowledgePointId},
    data: knowledgePoint, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        // 更新界面上的知识点，如果不是当前知识点分类的，则删除
        var currentKnowledgePointGroupId = window.vueToolbar.getCurrentKnowledgePointGroup().knowledgePointGroupId;
        if (knowledgePoint.knowledgePointGroupId === currentKnowledgePointGroupId) {
            window.knowledgePoints.splice(index, 1, knowledgePoint);
        } else {
            window.knowledgePoints.splice(index, 1);
        }

        layer.msg('知识点保存成功');
    }});
};

/**
 * 删除知识点
 *
 * @param  {int} knowledgePointId 知识点 id
 * @param  {int} index            需要删除的知识点在 window.knowledgePoints 中的下标
 * @return 无返回值
 */
KnowledgePoint.remove = function(knowledgePointId, index) {
    $.rest.remove({url: Urls.REST_KNOWLEDGE_POINTS_BY_ID, urlParams: {knowledgePointId: knowledgePointId},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        // 删除界面上的知识点
        window.knowledgePoints.splice(index, 1);
        layer.closeAll();
        layer.msg('知识点删除成功');
    }});
};

/**
 * 加载指定知识点分类下的知识点
 *
 * @param  {int} knowledgePointGroupId 知识点分类的 id
 * @return 无返回值
 */
KnowledgePoint.loadKnowledgePoints = function(knowledgePointGroupId) {
    $.rest.get({url: Urls.REST_KNOWLEDGE_POINTS_OF_GROUP, urlParams: {knowledgePointGroupId: knowledgePointGroupId},
        success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        window.knowledgePoints.splice(0, window.knowledgePoints.length);
        var groups = result.data;

        for (var i = 0; i < groups.length; ++i) {
            window.knowledgePoints.push(groups[i]);
        }
    }});
};

/*-----------------------------------------------------------------------------|
 |                             KnowledgePointGroup                             |
 |----------------------------------------------------------------------------*/
/**
 * 知识点分类的类
 */
function KnowledgePointGroup(knowledgePointGroupId, name) {
    this.knowledgePointGroupId = knowledgePointGroupId;
    this.name = name;
}

/**
 * 从服务器加载知识点分类
 *
 * @return 无返回值
 */
KnowledgePointGroup.loadKnowledgePointGroups = function() {
    $.rest.get({url: Urls.REST_KNOWLEDGE_POINT_GROUPS, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        window.knowledgePointGroups.splice(0, window.knowledgePointGroups.length); // 清空已有知识点分类
        var groups = result.data;

        for (var i = 0; i < groups.length; ++i) {
            window.knowledgePointGroups.push(groups[i]);
        }
    }});
};

/**
 * 创建知识点分类
 *
 * @param  {object} knowledgePointGroup KnowledgePointGroup 对象
 * @return 无返回值
 */
KnowledgePointGroup.create = function(knowledgePointGroup) {
    $.rest.create({url: Urls.REST_KNOWLEDGE_POINT_GROUPS, data: knowledgePointGroup, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        knowledgePointGroup.knowledgePointGroupId = result.data.knowledgePointGroupId;
        window.knowledgePointGroups.push(knowledgePointGroup);
    }});
};

/**
 * 更新知识点分类
 *
 * @param  {object} knowledgePointGroup KnowledgePointGroup 对象
 * @param  {int} index 需要编辑的知识点分类在 window.knowledgePointGroups 中的下标
 * @return 无返回值
 */
KnowledgePointGroup.update = function(knowledgePointGroup, index) {
    $.rest.update({url: Urls.REST_KNOWLEDGE_POINT_GROUPS_BY_ID,
        urlParams: {knowledgePointGroupId: knowledgePointGroup.knowledgePointGroupId},
        data: {name: knowledgePointGroup.name}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        // 更新下拉框中的知识点分类
        window.knowledgePointGroups[index].name = knowledgePointGroup.name;
        window.knowledgePointGroups.splice(index, 1, window.knowledgePointGroups[index]);
        window.vueKnowledgePointGroupEditor.hideInput();
    }});
};

/**
 * 删除知识点分类
 *
 * @param {int} knowledgePointGroupId 知识点分类 id
 * @param {int} index 知识点分类下拉框中的下标
 * @return 无返回值
 */
KnowledgePointGroup.remove = function(knowledgePointGroupId, index) {
    $.rest.remove({url: Urls.REST_KNOWLEDGE_POINT_GROUPS_BY_ID,
        urlParams: {knowledgePointGroupId: knowledgePointGroupId},
        success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        window.knowledgePointGroups.splice(index, 1);
    }});
};
/*-----------------------------------------------------------------------------|
 |                                 Main entry                                  |
 |----------------------------------------------------------------------------*/
require(['jquery', 'vue', 'semanticUi', 'layer', 'util', 'rest', 'urls'], function($, Vue) {
    Util.activateSidebarItem(2);

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
                var knowledgePointGroupId = this.getCurrentKnowledgePointGroup().knowledgePointGroupId;
                KnowledgePoint.loadKnowledgePoints(knowledgePointGroupId);
            },
            // 取得下拉框中当前选中的知识点分类
            getCurrentKnowledgePointGroup: function() {
                return this.currentIndex === -1 ? {knowledgePointGroupId: 0, name: ''} : this.knowledgePointGroups[this.currentIndex];
            }
        }
    });

    // 知识点分类编辑器的 Vue 对象
    window.vueKnowledgePointGroupEditor = new Vue({
        el: '#vue-knowledge-point-group-editor',
        data: {
            knowledgePointGroups: window.knowledgePointGroups,
            currentIndex: -1
        },
        methods: {
            // 显示输入框
            showInput: function(index, event) {
                this.currentIndex = index;
                var $input = this.getInput();
                var name = window.knowledgePointGroups[index].name; // 要编辑的知识点分类的名字

                // 1. 找到要插入的行
                // 2. 隐藏 name
                // 3. 插入 input
                $td = $(event.target).parent().prev();
                $('.name', $td).hide();
                $input.val(name).appendTo($td).show().focus();
            },
            // 隐藏输入框
            hideInput: function() {
                // 1. 隐藏 input
                // 2. 把相应的 name 显示出来
                // 3. 然后把 input 从 item 中移走，防止 item 被删除时 input 也被删除
                var $input = this.getInput();
                $input.hide();
                $input.siblings('.name').show();
                $input.appendTo('#vue-knowledge-point-group-editor');
            },
            // 创建知识点分类
            createKnowledgePointGroup: function() {
                // 创建默认的知识点
                KnowledgePointGroup.create({name: '[新知识点分类]'});
            },
            // 按下回车时更新知识点分类
            updateKnowledgePointGroup: function() {
                // 1. 获取 input 中知识点分类的名字
                // 2. 更新到数据库
                // 3. 更新成功后隐藏输入框，显示知识点分类的名字 (update() 中给处理了)
                var $input = this.getInput();
                var name = $.trim($input.val());
                var knowledgePointGroupId = window.knowledgePointGroups[this.currentIndex].knowledgePointGroupId;

                // 名字不能为空
                if (!name) {
                    layer.msg('知识点分类不能为空');
                    return;
                }

                KnowledgePointGroup.update(new KnowledgePointGroup(knowledgePointGroupId, name), this.currentIndex);
            },
            // 删除知识点分类
            removeKnowledgePointGroup: function(index) {
                // 删除前最好是询问一下是否确定删除，防止误操作
                var name = window.knowledgePointGroups[index].name;
                var dlg = layer.confirm('您确定删除<font color="red"> {0} </font>吗？'.format(name), {
                    title: '删除',
                    btn: ['确定', '取消']
                }, function() {
                    layer.close(dlg);
                    var knowledgePointGroupId = window.knowledgePointGroups[index].knowledgePointGroupId;
                    KnowledgePointGroup.remove(knowledgePointGroupId, index);
                });
            },
            getInput: function() {
                return $('#vue-knowledge-point-group-editor input'); // 返回 input
            }
        }
    });

    // 知识点编辑器的 Vue 对象
    window.vueKnowledgePointEditor = new Vue({
        el: '#vue-knowledge-point-editor',
        data: {
            knowledgePointGroups: window.knowledgePointGroups, // 知识点分类
            knowledgePoint: KnowledgePoint.emptyKnowledgePoint()
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
                var knowledgePoint = $.extend({}, window.knowledgePoints[index]);
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
        vueKnowledgePointEditor.knowledgePoint = knowledgePoint; // 先更新知识点编辑器中的知识点

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
                var kpgId = vueKnowledgePointEditor.knowledgePoint.knowledgePointGroupId; // 正编辑的知识点分类的 id
                var name  = $.trim(vueKnowledgePointEditor.knowledgePoint.name);

                if (!name) {
                    layer.msg('知识点不能为空');
                    return;
                }

                // 如果 index 等于 -1 则为创建，否则为更新
                if (index === -1) {
                    KnowledgePoint.create(new KnowledgePoint(0, name, kpgId)); // 创建
                } else {
                    KnowledgePoint.update(knowledgePoint, index); // 更新
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
                    KnowledgePoint.remove(knowledgePoint.knowledgePointId, index);
                });

                return false;
            }
        });
    }

    KnowledgePointGroup.loadKnowledgePointGroups();
    $('.ui.dropdown').dropdown();
});
