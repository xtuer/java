require(['jquery', 'vue', 'layer', 'semanticUi', 'semanticUiCalendar', 'ztree', 'pagination', 'rest', 'urls', 'util',
    'paper', 'paperDirectoryTree', 'knowledgePointGroupTree'], function($, Vue) {
    Util.activateSidebarItem(0);
    new EditablePaperDirectoryTree('directory-tree'); // 左侧的目录树

    window.pageSize   = 20; // 每页显示的试卷数量
    window.pageNumber = 1;  // 要加载的页码

    // 设置当前页
    window.setPageNumber = function(pageNumber) {
        $('#paginator').pagination('drawPage', pageNumber);
    };

    // 设置总页数
    window.setPageCount = function(pageCount) {
        $('#paginator').pagination('updateItems', pageCount);
    };

    // 初始化时加载试卷: 点击目录，点击知识点时调用
    window.loadPapersAsInit = function(paperDirectoryId, knowledgePointIds) {
        // 请求页数
        PaperDao.pageCountPapersByKnowledgePointIdInPaperDirectory(paperDirectoryId, knowledgePointIds, window.pageSize, function(pageCount) {
            window.vuePapers.pageCount = pageCount;
            window.setPageCount(pageCount);
        });

        // 加载试卷
        window.vuePapers.papers = [];
        PaperDao.findPapersInPaperDirectoryWithKnowledgePointIds(paperDirectoryId, knowledgePointIds, 1, window.pageSize, function(papers) {
            window.vuePapers.papers = papers;
            window.setPageNumber(1); // 重新加载后显示为第一页
        });
    };

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

                var pointIds = this.checkedKnowledgePointIds(); // 选中的知识点的 id
                window.loadPapersAsInit(paperDirectoryId, pointIds);
            },
            // 选中的知识点的 id 数组
            checkedKnowledgePointIds: function() {
                var pointIds = []; // 选中的知识点的 id

                for (var i=0; i<this.knowledgePoints.length; ++i) {
                    if (this.knowledgePoints[i].checked) {
                        pointIds.push(this.knowledgePoints[i].knowledgePointId);
                    }
                }

                return pointIds;
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
            // 选中或则取消选中试卷
            selectPaper: function(paper) {
                paper.checked = !paper.checked;
            },
            // 使用学科和试卷名字的部分查找试卷
            findPapers: function() {
                PaperDao.search(this.subject, this.nameFilter, function(papers) {
                    for (var i=0; i < papers.length; ++i) {
                        papers[i].checked = false;
                    }
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
            editedIndex: 0,
            pageCount: 1
        },
        watch: {
            papers: function(newValue, oldValue) {
                // papers 的数据变化后，DOM 更新完成时调用下面的函数
                this.$nextTick(function() {
                    $('#vue-papers .dimmable').dimmer({on: 'hover'}); // 鼠标放到 dimmable 上时显示 dimmer
                    $('#vue-papers .icon.tags, #vue-papers .icon.move').popup({position: 'left center', offset: -12}); // 初始化 popup
                    // $(document).off('mousedown', '#vue-papers .icon.move', DnD.mouseDown);
                    // $(document).on('mousedown',  '#vue-papers .icon.move', DnD.mouseDown);

                    $(document).off('mousedown', '#vue-papers .dnd-paper-name', DnD.mouseDown);
                    $(document).on('mousedown',  '#vue-papers .dnd-paper-name', DnD.mouseDown);
                });
            }
        },
        filters: {
            statusText: function(value) {
                value = parseInt(value);

                switch(value) {
                    case 0: return '未处理';
                    case 1: return '已通过';
                    case 2: return '未通过';
                }
            }
        },
        methods: {
            editPaper: function(paper) {
                var self = this;
                this.editedIndex = this.papers.indexOf(paper);

                var dlg = layer.open({
                    type: 1,
                    title: paper.originalName,
                    skin: 'layui-layer-rim', //加上边框
                    shade: 0.8,
                    closeBtn: 0,
                    area: ['750px'], //宽高
                    content: $('#paper-editor-dialog'),
                    btn: ['保存', '预览', '下载', '取消'],
                    btn1: function() {
                        // 保存更新
                        var finalPaper = window.vuePaperEditor.paper;
                        finalPaper.publishYear = $('#publish-year').val();
                        finalPaper.status = $('#paper-status').dropdown('get value');

                        console.log(paper.publishYear);

                        if (!finalPaper.name) {
                            layer.msg('试卷名不能为空');
                        } else {
                            PaperDao.update(finalPaper, function(updatedPaper) {
                                window.vuePapers.papers.replace(self.editedIndex, updatedPaper);
                                layer.close(dlg);
                            });
                        }
                    },
                    btn2: function() {
                        self.previewPaper(paper);
                        return false;
                    },
                    btn3: function() {
                        self.downloadPaper(paper);
                        return false;
                    },
                    btn4: function() {
                        return true; // 点击取消按钮，关闭对话框
                    }, success: function() {
                        window.vuePaperEditor.paper = $.extend({}, paper); // 复制一份试卷进行编辑
                        $('#paper-status').dropdown('set selected', paper.status+'');
                        $('#year-chooser').calendar({type: 'year'});
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
                // window.open(url);
                var $form = $('<form method="GET"></form>');
                $form.attr('action', url);
                $form.appendTo($('body'));
                $form.submit();
            }
        }
    });

    /*-----------------------------------------------------------------------------|
     |                                    编辑试卷                                  |
     |----------------------------------------------------------------------------*/
    window.vuePaperEditor = new Vue({
        el: '#vue-paper-editor',
        data: {
            paper: {originalName: '', name: '', publishYear: '', knowledgePoints: []}
        },
        methods: {
            // 加载知识点
            loadKnowledgePoints: function(knowledgePointGroup) {
                var self = this;
                $('#vue-paper-editor .knowledge-points-dropdown').dropdown('restore defaults');
                KnowledgePointGroupDao.loadKnowledgePoints(knowledgePointGroup.knowledgePointGroupId, function(groups) {
                    self.knowledgePoints = [];
                    self.knowledgePoints = groups;
                });
            },
            // 删除试卷的知识点
            removeKnowledgePointFromPaper: function(knowledgePoint) {
                var self = this;
                var paperId = this.paper.paperId;
                var knowledgePointId = knowledgePoint.knowledgePointId;

                PaperDao.removeKnowledgePoint(paperId, knowledgePointId, function() {
                    var index = self.paper.knowledgePoints.indexOf(knowledgePoint);
                    self.paper.knowledgePoints.remove(index);
                });
            },
            // 选择知识点对话框
            openKnowledgePointsChooser: function() {
                var self = this;
                var dlg = layer.open({
                    type: 1,
                    title: '选择知识点',
                    skin: 'layui-layer-rim',
                    shade: 0.8,
                    closeBtn: 0,
                    area: ['550px'],
                    content: $('#knowledge-points-dialog'),
                    btn: ['确定', '取消'],
                    btn1: function() {
                        // 取得选中的知识点
                        var selectedPoints = window.vueKnowledgePointsChooser.knowledgePoints.filter(function(point) {
                            return point.checked;
                        });

                        // 添加知识点到试卷
                        if (selectedPoints.length > 0) {
                            PaperDao.addKnowledgePoints(self.paper.paperId, selectedPoints, function(addedPointIds) {
                                var addedPoints = selectedPoints.filter(function(p) {
                                    for (var i = 0; i < addedPointIds.length; ++i) {
                                        if (addedPointIds[i] == p.knowledgePointId) {
                                            return true;
                                        }
                                    }

                                    return false;
                                });

                                for (var j = 0; j < addedPoints.length; ++j) {
                                    self.paper.knowledgePoints.push($.extend({}, selectedPoints[j]));
                                }

                                layer.close(dlg);
                            });
                        }
                    },
                    success: function() {
                        window.vueKnowledgePointsChooser.deselectAll();
                    }
                });
            }
        }
    });

    /*-----------------------------------------------------------------------------|
     |                                    知识点选择                                 |
     |----------------------------------------------------------------------------*/
    window.vueKnowledgePointsChooser = new Vue({
        el: '#vue-knowledge-points-chooser',
        data: {
            knowledgePoints: []
        },
        methods: {
            choose: function(point, event) {
                point.checked = point.checked ? false : true;
                if (point.checked) {
                    $(event.target).addClass('checked');
                } else {
                    $(event.target).removeClass('checked');
                }
            },
            deselectAll: function() {
                for (var i = 0; i < this.knowledgePoints.length; ++i) {
                    this.knowledgePoints[i].checked = false;
                }
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

    // 只读的知识点树，编辑试卷，在选择知识点的对话框中显示
    new ReadOnlyKnowledgePointGroupTree('read-only-knowledge-point-group-tree');

    // 点击按钮 '所有知识点' 显示当前目录下的所有知识点
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

    // 弹出试卷导出对话框
    $('#open-export-dialog-button').click(function(event) {
        var dlg = layer.open({
            type: 1,
            title: '试卷导出 -- 仅导出审核通过的试卷',
            skin: 'layui-layer-rim', //加上边框
            closeBtn: 0,
            shade: 0.8,
            area: ['450px', '400px'], //宽高
            content: $('#export-papers-dialog'),
            btn: ['导出', '全选', '展开', '收拢', '取消'],
            btn1: function() {
                var nodes = window.exportTree.tree.getCheckedNodes();
                var paperDirectoryIds = nodes.map(function(n) {
                    return n.paperDirectoryId;
                });

                exportPapers(paperDirectoryIds);
            },
            btn2: function() {
                window.exportTree.tree.checkAllNodes(true);
                return false;
            },
            btn3: function() {
                window.exportTree.tree.expandAll(true);
                return false;
            },
            btn4: function() {
                window.exportTree.tree.expandAll(false);
                return false;
            },
            success: function() {
                // 如果导出的目录树存在则销毁
                if (window.exportTree) {
                    $.fn.zTree.destroy("export-paper-directories-tree");
                }

                window.exportTree = new ReadOnlyPaperDirectoryTree('export-paper-directories-tree'); // 导出试卷的目录树
            }
        });
    });

    // 初始化分页插件
    $('#paginator').pagination({
        pages: 50,
        currentPage: 1,
        prevText: '上一页',
        nextText: '下一页',
        cssStyle: 'compact-theme',
        onPageClick: function(page) {
            // 加载试卷
            window.vuePapers.papers = [];
            var paperDirectoryId = $('#vue-papers').attr('data-paper-directory-id');
            var knowledgePointIds = window.vueKnowledgePoints.checkedKnowledgePointIds();
            PaperDao.findPapersInPaperDirectoryWithKnowledgePointIds(paperDirectoryId, knowledgePointIds, page, window.pageSize, function(papers) {
                window.vuePapers.papers = papers;
            });
        }
    });

    $('.dropdown').dropdown();

    /**
     * 导出试卷
     *
     * @param {Array} paperDirectoryIds 目录的 id 数组
     * @return 无返回值
     */
    function exportPapers(paperDirectoryIds) {
        if (paperDirectoryIds.isEmpty()) {
            layer.msg('请选择目录');
            return;
        }

        $.rest.create({url: Urls.REST_EXPORT_PAPERS, data: {paperDirectoryIds: paperDirectoryIds}, success: function(result) {
            // 后台正在执行导出操作
            if (!result.success) {
                layer.msg(result.message);
                return;
            }

            // 执行导出，显示 loading 状态
            if ('paper_export_running' === result.data) {
                $('#export-papers-dialog .dimmer').addClass('active');
                checkExportPapersStatus();
            }
        }});
    }

    /**
     * 检查导出状态
     *
     * @return 无返回值
     */
    function checkExportPapersStatus() {
        var checker = setInterval(function() {
            $.rest.get({url: Urls.REST_EXPORT_PAPERS_STATUS, success: function(result) {
                if ('paper_export_finished' === result.data) {
                    $('#export-papers-dialog .dimmer').removeClass('active');
                    clearInterval(checker);
                }
            }});
        }, 1000);
    }
});
