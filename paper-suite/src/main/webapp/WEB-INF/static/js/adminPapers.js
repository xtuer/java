/*-----------------------------------------------------------------------------|
 |                                 Class Paper                                 |
 |----------------------------------------------------------------------------*/
function Paper() {

}

/**
 * 使用试卷的 id 查找试卷
 *
 * @param  {string}   paperId  试卷 id
 * @param  {Function} callback 成功的回调函数
 * @return 无返回值
 */
Paper.findById = function(paperId, callback) {
    $.rest.get({url: Urls.REST_PAPERS_BY_ID, urlParams: {paperId: paperId}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        var paper = result.data; // 试卷
        callback(paper);
    }});
};

/**
 * 查找目录下某个知识点的试卷
 *
 * @param  {string} paperDirectoryId 目录 id
 * @param  {string} knowledgePointId 知识点 id
 * @return 无返回值
 */
Paper.findPapersByKnowledgePointIdsInPaperDirectory = function(paperDirectoryId, knowledgePointIds) {
    $.rest.get({url: Urls.REST_PAPERS_SEARCH_IN_DIRECTORY, urlParams: {paperDirectoryId: paperDirectoryId},
    data: {knowledgePointIds: knowledgePointIds}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        window.vuePapers.papers = [];
        window.vuePapers.papers = result.data;
    }});
};

/**
 * 更新试卷
 *
 * @param  {object}   paper    试卷
 * @param  {Function} callback 成功的回调函数
 * @return 无返回值
 */
Paper.update = function(paper, callback) {
    $.rest.update({url: Urls.REST_PAPERS_BY_ID, urlParams: {paperId: paper.paperId}, data: {name: paper.name, publishDate: paper.publishDate},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback(paper);
    }});
};

Paper.removeKnowledgePoint = function(paperId, knowledgePointId, callback) {
    $.rest.remove({url: Urls.REST_PAPERS_KNOWLEDGE_POINTS_BY_ID, urlParams: {paperId: paperId, knowledgePointId: knowledgePointId},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        callback();
    }});
};

/**
 * 根据学科和名字查找试卷
 *
 * @param  {string} subject    学科
 * @param  {string} nameFilter 试卷名字的子字符串
 * @return 无返回值
 */
Paper.search = function(subject, nameFilter) {
    var chooser = window.vueChoosePapers;
    $.rest.get({url: Urls.REST_PAPERS_SEARCH, data: {subject: subject, nameFilter: nameFilter}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        // 1. Vue 更新试卷的数据，Vue 会同时更新试卷的 DOM
        // 2. Vue 更新完试卷的 DOM 后调用 checkbox() 使得新的 checkbox 支持完全的功能
        chooser.papers = result.data;
        chooser.$nextTick(function() {
            $('#vue-choose-papers .checkbox').checkbox();
        });
    }});
};

/**
 * 添加选中的试卷到当前目录
 *
 * @return 无返回值
 */
Paper.addSelectedPapersToCurrentPaperDirectory = function() {
    // 得到所有选中的试卷，从后向前遍历，否则会删除时会出错
    var paperDirectoryId = $('#vue-papers').attr('data-paper-directory-id');
    var papers = window.vueChoosePapers.papers;
    var selectedPapers = [];
    var selectedPapersIds = [];

    // 把选中的试卷放到单独的数组中，并从 papers 里删除
    for (var i=papers.length-1; i>=0; --i) {
        if (papers[i].checked) {
            selectedPapers.push(papers[i]);
            selectedPapersIds.push(papers[i].paperId);
            papers.splice(i, 1);
        }
    }

    // 如果没有选择试卷，则提示
    if (selectedPapersIds.length === 0) {
        layer.msg('没有选择试卷');
        return;
    }

    // 修改选中的试卷的目录
    $.rest.update({url: Urls.REST_PAPERS_REDIRECTORY, data: {paperIds: selectedPapersIds, paperDirectoryId: paperDirectoryId},
    success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        // 成功添加试卷到目录后，更新 Vue 对象 window.vuePapers 的数据，更新界面
        selectedPapers.reverse();
        for (var i=0; i<selectedPapers.length; ++i) {
            window.vuePapers.papers.push(selectedPapers[i]);
        }
    }});
};

/**
 * 添加知识点到试卷
 *
 * @param  {string} paperId        试卷 id
 * @param  {string} knowledgePoint 知识点
 * @return 无返回值
 */
Paper.addKnowledgePoint = function(paperId, knowledgePoint) {
    $.rest.create({url: Urls.REST_PAPERS_KNOWLEDGE_POINTS, urlParams: {paperId: paperId},
    data: {knowledgePointId: knowledgePoint.knowledgePointId}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        window.vuePaperEditor.paper.knowledgePoints.push(knowledgePoint);
    }});
};

/**
 * 加载试卷的知识点
 *
 * @param  {string}   paperId  试卷 id
 * @param  {Function} callback 成功的回调函数
 * @return 无返回值
 */
Paper.loadKnowledgePoints = function(paperId, callback) {
    $.rest.get({url: Urls.REST_PAPERS_KNOWLEDGE_POINTS, urlParams: {paperId: paperId}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        var knowledgePoints = result.data;
        callback(knowledgePoints);
    }});
};

/*-----------------------------------------------------------------------------|
 |                             KnowledgePointGroup                             |
 |----------------------------------------------------------------------------*/
function KnowledgePointGroup() {

}

/**
 * 从服务器加载知识点分类
 *
 * @return 无返回值
 */
KnowledgePointGroup.loadKnowledgePointGroups = function(callback) {
    $.rest.get({url: Urls.REST_KNOWLEDGE_POINT_GROUPS, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        var knowledgePointGroups = result.data;
        callback(knowledgePointGroups);
    }});
};

/**
 * 加载指定知识点分类下的知识点
 *
 * @param  {int} knowledgePointGroupId 知识点分类的 id
 * @return 无返回值
 */
KnowledgePointGroup.loadKnowledgePoints = function(knowledgePointGroupId) {
    $.rest.get({url: Urls.REST_KNOWLEDGE_POINTS_OF_GROUP, urlParams: {knowledgePointGroupId: knowledgePointGroupId},
        success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        window.vuePaperEditor.knowledgePoints = [];
        window.vuePaperEditor.knowledgePoints = result.data;
    }});
};

/*-----------------------------------------------------------------------------|
 |                                Drag and Drop                                |
 |----------------------------------------------------------------------------*/
// DnD(Drag and Drop) 是拖拽实现的核心
DnD = {
    $draggedElement: null, // 正在拖拽的 element
    // 按下鼠标
    mouseDown: function(e) {
        var $doc = $(document);
        var x = $doc.scrollLeft();
        var y = $doc.scrollTop();

        // 创建并保存拖拽的 element
        DnD.$draggedElement = $(e.target).clone();
        DnD.$draggedElement.addClass('dragged-item');
        DnD.$draggedElement.css({
            'position': 'absolute',
            'left': (e.clientX + x + 2) + 'px',
            'top' : (e.clientY + y + 2) + 'px'
        });
        DnD.$draggedElement.appendTo('body');

        // 绑定鼠标移动，松开和选择文本事件
        $doc.on('mousemove',   DnD.mouseMove);
        $doc.on('mouseup',     DnD.mouseUp);
        $doc.on('selectstart', DnD.noSelect);

        return false;
    },
    // 移动鼠标
    mouseMove: function(e) {
        // 移动拖拽的 element
        var $doc = $(document);
        var x = $doc.scrollLeft();
        var y = $doc.scrollTop();

        DnD.$draggedElement.css({
            'left': (e.clientX + x + 3) + 'px',
            'top' : (e.clientY + y + 3) + 'px'
        });

        return false;
    },
    // 松开鼠标
    mouseUp: function(e) {
        // 松开鼠标时取消 document 绑定的事件
        var $doc = $(document);
        $doc.off('mousemove',   DnD.mouseMove);
        $doc.off('mouseup',     DnD.mouseUp);
        $doc.off('selectstart', DnD.noSelect);

        // 删除拖拽的 element
        DnD.$draggedElement.remove();
        DnD.$draggedElement = null;
    },
    // 取消选择，为了实现效果好一些，所以在拖拽时不允许选择文本
    noSelect: function() {
        return false;
    }
};

/*-----------------------------------------------------------------------------|
 |                             require entry point                             |
 |----------------------------------------------------------------------------*/
require(['jquery', 'vue', 'layer', 'semanticUi', 'ztree', 'pagination', 'rest', 'urls', 'util'], function($, Vue) {
    Util.activateSidebarItem(1);
    Tree.init(); // 初始化目录树

    window.vueKnowledgePoints = new Vue({
        el: '#vue-knowledge-points',
        data: {
            knowledgePoints: []
        },
        methods: {
            showPapers: function(knowledgePoint, e) {
                var paperDirectoryId = $('#vue-papers').attr('data-paper-directory-id');
                knowledgePoint.checked = knowledgePoint.checked ? false : true; // 切换选中状态

                if (knowledgePoint.checked) {
                    $(e.target).addClass('checked');
                } else {
                    $(e.target).removeClass('checked');
                }

                var pointIds = [];

                for (var i=0; i<this.knowledgePoints.length; ++i) {
                    if (this.knowledgePoints[i].checked) {
                        pointIds.push(this.knowledgePoints[i].knowledgePointId);
                    }
                }

                Paper.findPapersByKnowledgePointIdsInPaperDirectory(paperDirectoryId, pointIds);
            }
        }
    });

    /*-----------------------------------------------------------------------------|
    |                                    选择试卷                                   |
    |-----------------------------------------------------------------------------*/
    window.vueChoosePapers = new Vue({
        el: '#vue-choose-papers',
        data: {
            nameFilter: '',
            subject: '高中语文', // 默认选中高中语文
            papers: []
        },
        methods: {
            // 动态计算 attached segment 的 class
            segmentClass: function(index) {
                if (index === 0) {
                    return 'ui top attached segment';
                } else if (index === this.papers.length - 1) {
                    return 'ui bottom attached segment';
                } else {
                    return 'ui attached segment';
                }
            },
            // 使用学科和试卷名字的部分查找试卷
            findPapers: function() {
                Paper.search(this.subject, $.trim(this.nameFilter));
            }
        }
    });

    /*-----------------------------------------------------------------------------|
    |                                   试卷列表                                     |
    |-----------------------------------------------------------------------------*/
    window.vuePapers = new Vue({
        el: '#vue-papers',
        data: {
            papers: [],
            editedIndex: 0
        },
        watch: {
            papers: function(newValue, oldValue) {
                // papers 的数据变化后，DOM 更新完成时调用下面的函数
                this.$nextTick(function() {
                    $('#vue-papers .dimmable').dimmer({on: 'hover'}); // 鼠标放到 dimmable 上时显示 dimmer
                    $('#vue-papers .icon.tags, #vue-papers .icon.move').popup({position: 'bottom left', offset: -12}); // 初始化 popup
                    $(document).off('mousedown', '#vue-papers .ui.card .icon.move', DnD.mouseDown);
                    $(document).on('mousedown', '#vue-papers .ui.card .icon.move', DnD.mouseDown);
                });
            }
        },
        methods: {
            editPaper: function(index) {
                this.editedIndex = index;
                var paperId = this.papers[index].paperId;

                layer.open({
                    type: 1,
                    title: false,
                    skin: 'layui-layer-rim', //加上边框
                    closeBtn: 0,
                    area: ['550px'], //宽高
                    content: $('#paper-editor-dialog'),
                    btn: ['保存', '取消'],
                    btn1: function() {
                        // 保存更新
                        var paper = window.vuePaperEditor.paper;
                        // paper.publishDate = $('#vue-paper-editor .publish-date').val();

                        if (!paper.name) {
                            layer.msg('试卷名不能为空');
                        } else {
                            Paper.update(paper, function(updatedPaper) {
                                window.vuePapers.papers.splice(window.vuePapers.editedIndex, 1, updatedPaper);
                                layer.closeAll();
                            });
                        }
                    },
                    btn2: function() {
                        return true; // 点击取消按钮，关闭对话框
                    }, success: function() {
                        // 知识点和知识点分类下拉框恢复默认选项
                        $('#vue-paper-editor .knowledge-points-dropdown').dropdown('restore defaults');
                        $('#vue-paper-editor .knowledge-point-groups-dropdown').dropdown('restore defaults');

                        // 加载知识点分类
                        KnowledgePointGroup.loadKnowledgePointGroups(function(knowledgePointGroups) {
                            window.vuePaperEditor.knowledgePointGroups = [];
                            window.vuePaperEditor.knowledgePointGroups = knowledgePointGroups;
                        });

                        // 加载试卷
                        Paper.findById(paperId, function(paper) {
                            paper.publishDate = paper.publishDate || '';
                            if (paper.publishDate.length > 10) {
                                paper.publishDate = paper.publishDate.substring(0, 10); // 只显示年月日
                            }
                            window.vuePaperEditor.paper = paper;
                        });
                    }
                });
            },
            knowledgePointsString: function(knowledgePoints) {
                var names = [];

                for (var i=0; i<knowledgePoints.length; ++i) {
                    names.push(knowledgePoints[i].name);
                }

                return names.length>0 ? names.join('、') : '没有知识点';
            }
        }
    });

    window.vuePaperEditor = new Vue({
        el: '#vue-paper-editor',
        data: {
            paper: {originalName: '', name: '', publishDate: '', knowledgePoints: []},
            knowledgePoints: [], // 知识点
            knowledgePointGroups: [] // 知识点分类
        },
        methods: {
            // 加载知识点
            loadKnowledgePoints: function(knowledgePointGroup) {
                $('#vue-paper-editor .knowledge-points-dropdown').dropdown('restore defaults');
                KnowledgePointGroup.loadKnowledgePoints(knowledgePointGroup.knowledgePointGroupId);
            },
            // 添加知识点到试卷
            addKnowledgePointToPaper: function(knowledgePoint) {
                Paper.addKnowledgePoint(this.paper.paperId, knowledgePoint);
            },
            // 删除试卷的知识点
            removeKnowledgePointFromPaper: function(knowledgePoint) {
                var paperId = this.paper.paperId;
                var knowledgePointId = knowledgePoint.knowledgePointId;

                Paper.removeKnowledgePoint(paperId, knowledgePointId, function() {
                    var index = window.vuePaperEditor.paper.knowledgePoints.indexOf(knowledgePoint);
                    window.vuePaperEditor.paper.knowledgePoints.splice(index, 1);
                });
            }
        }
    });

    // 修改日期的时候更新 model 里的数据
    $('#publish-date').click(function(event) {
        laydate({
            elem: '#publish-date',
            format: 'YYYY-MM-DD', // 分隔符可以任意定义，该例子表示只显示年月
            choose: function(datas){ //选择日期完毕的回调
              window.vuePaperEditor.paper.publishDate = $('#publish-date').val();
            }
        });
    });

    // 弹出选择试卷对话框
    $('#popup-choose-papers-dialog-button').click(function(event) {
        layer.open({
            type: 1,
            title: false,
            skin: 'layui-layer-rim', //加上边框
            closeBtn: 0,
            area: ['620px', '550px'], //宽高
            content: $('#choose-papers-dialog'),
            btn: ['添加', '全选', '取消'],
            btn1: function() {
                // 取得选中的试卷，添加到当前目录
                Paper.addSelectedPapersToCurrentPaperDirectory();
            },
            btn2: function() {
                var papers = window.vueChoosePapers.papers;

                for (var i=0; i<papers.length; ++i) {
                    papers[i].checked = true;
                }

                // 先设置为空数组，然后再设置成新的数组才会生效
                window.vueChoosePapers.papers = [];
                window.vueChoosePapers.papers = papers;

                return false;
            }
        });
    });

    $('.pagination').jqPagination({
        max_page: 30, // 总页数
        page_string: '{max_page} 页之 {current_page}', // 页数显示样式
        paged: function(page) {
            alert(page);
        }
    });

    $('.dropdown').dropdown();

    $('#show-konwledge-points').click(function(event) {
        // 如果所有知识点显示的，则隐藏
        if (!$('#vue-knowledge-points').is(':hidden')) {
            $('#vue-knowledge-points').slideUp();
            return;
        }

        // 如果所有知识点是隐藏的，则加载知识点然后显示
        var paperDirectoryId = $('#vue-papers').attr('data-paper-directory-id');

        // 加载目录下试卷的知识点
        $.rest.get({url: Urls.REST_PAPER_DIRECTORIES_KNOWLEDGE_POINTS, urlParams: {paperDirectoryId: paperDirectoryId},
        success: function(result) {
            if (!result.success) {
                layer.msg(result.msg);
                return;
            }

            window.vueKnowledgePoints.knowledgePoints = [];
            window.vueKnowledgePoints.knowledgePoints = result.data;
            $('#vue-knowledge-points').slideDown();
        }});
    });
});
