require(['jquery', 'vue', 'layer', 'semanticUi', 'semanticUiCalendar', 'ztree', 'pagination', 'rest', 'urls', 'util',
    'paper', 'knowledgePointGroup', 'paperDirectoryTree'], function($, Vue) {
    Util.activateSidebarItem(1);
    new EditablePaperDirectoryTree('directory-tree'); // 初始化目录树

    /*-----------------------------------------------------------------------------|
     |                                    所有知识点                                 |
     |----------------------------------------------------------------------------*/
    window.vueKnowledgePoints = new Vue({
        el: '#vue-knowledge-points',
        data: {
            knowledgePoints: []
        },
        methods: {
            showPapers: function(knowledgePoint, e) {
                var paperDirectoryId = $('#vue-papers').attr('data-paper-directory-id');
                knowledgePoint.checked = knowledgePoint.checked ? false : true; // 切换选中状态
                window.vuePapers.papers = []; // 先清空当前显示的试卷

                // 选中时的样式
                if (knowledgePoint.checked) {
                    $(e.target).addClass('checked');
                } else {
                    $(e.target).removeClass('checked');
                }

                var pointIds = []; // 选中的知识点的 id

                for (var i=0; i<this.knowledgePoints.length; ++i) {
                    if (this.knowledgePoints[i].checked) {
                        pointIds.push(this.knowledgePoints[i].knowledgePointId);
                    }
                }

                PaperDao.findPapersByKnowledgePointIdsInPaperDirectory(paperDirectoryId, pointIds, function(papers) {
                    window.vuePapers.papers = [];
                    window.vuePapers.papers = papers;
                });
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
                PaperDao.search(this.subject, this.nameFilter, function(papers) {
                    // 1. Vue 更新试卷的数据，Vue 会同时更新试卷的 DOM
                    // 2. Vue 更新完试卷的 DOM 后调用 checkbox() 使得新的 checkbox 支持完全的功能
                    window.vueChoosePapers.papers = papers;
                    window.vueChoosePapers.$nextTick(function() {
                        $('#vue-choose-papers .checkbox').checkbox();
                    });
                });
            },
            // 得到选中的试卷
            getSelectedPapers: function() {
                var papers = window.vueChoosePapers.papers;
                var selectedPapers = [];

                // 把选中的试卷放到单独的数组中，并从 papers 里删除
                for (var i=papers.length-1; i>=0; --i) {
                    if (papers[i].checked) {
                        selectedPapers.push(papers[i]);
                    }
                }

                return selectedPapers;
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
                    $('#vue-papers .icon.tags, #vue-papers .icon.move').popup({position: 'left center', offset: -12}); // 初始化 popup
                    $(document).off('mousedown', '#vue-papers .icon.move', DnD.mouseDown);
                    $(document).on('mousedown',  '#vue-papers .icon.move', DnD.mouseDown);
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
                        paper.publishYear = $('#vue-paper-editor #publish-year').val();

                        if (!paper.name) {
                            layer.msg('试卷名不能为空');
                        } else {
                            PaperDao.update(paper, function(updatedPaper) {
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
                        $('#year-chooser').calendar({type: 'year'});

                        // 加载知识点分类
                        KnowledgePointGroupDao.loadKnowledgePointGroups(function(knowledgePointGroups) {
                            window.vuePaperEditor.knowledgePointGroups = [];
                            window.vuePaperEditor.knowledgePointGroups = knowledgePointGroups;
                        });

                        // 加载试卷
                        PaperDao.findById(paperId, function(paper) {
                            paper.publishYear = paper.publishYear || '';
                            if (paper.publishYear.length > 10) {
                                paper.publishYear = paper.publishYear.substring(0, 10); // 只显示年月日
                            }
                            window.vuePaperEditor.paper = paper;
                        });
                    }
                });
            },
            // 知识点的字符串
            knowledgePointsString: function(knowledgePoints) {
                var names = [];

                for (var i=0; i<knowledgePoints.length; ++i) {
                    names.push(knowledgePoints[i].name);
                }

                return names.length>0 ? names.join('、') : '没有知识点';
            },
            // 预览试卷
            previewPaper: function(paper) {
                $.rest.get({url: Urls.REST_PAPERS_PREVIEW, urlParams: {paperId: paper.paperId}, success: function(result) {
                    console.log(result);
                    if (!result.success) {
                        layer.msg(result.message);
                        return;
                    }

                    layer.open({
                        type: 2,
                        title: paper.name,
                        shadeClose: true,
                        shade: 0.8,
                        area: ['70%', '90%'],
                        content: result.data // 预览文件的 url
                    });
                }});
            },
            // 下载试卷
            downloadPaper: function(paper) {
                var url = Urls.REST_PAPERS_DOWNLOAD.format({paperId: paper.paperId});
                window.open(url);
            }
        }
    });

    window.vuePaperEditor = new Vue({
        el: '#vue-paper-editor',
        data: {
            paper: {originalName: '', name: '', publishYear: '', knowledgePoints: []},
            knowledgePoints: [], // 知识点
            knowledgePointGroups: [] // 知识点分类
        },
        methods: {
            // 加载知识点
            loadKnowledgePoints: function(knowledgePointGroup) {
                $('#vue-paper-editor .knowledge-points-dropdown').dropdown('restore defaults');
                KnowledgePointGroupDao.loadKnowledgePoints(knowledgePointGroup.knowledgePointGroupId, function(groups) {
                    window.vuePaperEditor.knowledgePoints = [];
                    window.vuePaperEditor.knowledgePoints = groups;
                });
            },
            // 添加知识点到试卷
            addKnowledgePointToPaper: function(knowledgePoint) {
                PaperDao.addKnowledgePoint(this.paper.paperId, knowledgePoint, function() {
                    window.vuePaperEditor.paper.knowledgePoints.push(knowledgePoint);
                });
            },
            // 删除试卷的知识点
            removeKnowledgePointFromPaper: function(knowledgePoint) {
                var paperId = this.paper.paperId;
                var knowledgePointId = knowledgePoint.knowledgePointId;

                PaperDao.removeKnowledgePoint(paperId, knowledgePointId, function() {
                    var index = window.vuePaperEditor.paper.knowledgePoints.indexOf(knowledgePoint);
                    window.vuePaperEditor.paper.knowledgePoints.splice(index, 1);
                });
            }
        }
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
                var papers = window.vueChoosePapers.getSelectedPapers();
                var paperDirectoryId = $('#vue-papers').attr('data-paper-directory-id');

                PaperDao.addPapersToPaperDirectory(papers, paperDirectoryId, function() {
                    for (var i=papers.length-1; i>=0; --i) {
                        window.vuePapers.papers.push(papers[i]);

                        // 从选择试卷列表中删除
                        var index = window.vueChoosePapers.papers.indexOf(papers[i]);
                        window.vueChoosePapers.papers.splice(index, 1);
                    }
                });
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

    $('.pagination').jqPagination({
        max_page: 30, // 总页数
        page_string: '{max_page} 页之 {current_page}', // 页数显示样式
        paged: function(page) {
            alert(page);
        }
    });

    $('.dropdown').dropdown();
});
