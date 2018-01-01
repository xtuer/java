// 变量定义
window.pageSize   = 20; // 每页显示的试卷数量
window.pageNumber = 1;  // 要加载的页码
window.vueQuestions = null;

require(['jquery', 'vue', 'semanticUi', 'ztree', 'layer', 'rest', 'util', 'urls', 'question', 'pagination'], function($, Vue) {
    Util.activateSidebarItem(2);
    new KPTree('question-knowledge-point-tree');

    // 题目的 vue 对象
    window.vueQuestions = new Vue({
        el: '#vue-questions',
        data: {
            questions: [], // 题目
            knowledgePointId: 0, // 题目的知识点 ID
            pageCount: 1 // 知识点的页数
        },
        methods: {
            // 取消或者标记题目
            toggleQuestionMark: function(question) {
                QuestionDao.toggleQuestionMark(question.id, function() {
                    question.marked = !question.marked;
                });
            }
        }
    });

    // 初始化分页插件
    $('#paginator').pagination({
        pages: 50,
        currentPage: 1,
        prevText: '上一页',
        nextText: '下一页',
        cssStyle: 'compact-theme',
        onPageClick: function(pageNumber) {
            // 加载知识点下第 page 页的题目
            QuestionDao.loadQuestionsUnderQuestionKnowledgePoint(window.vueQuestions.knowledgePointId, pageNumber, window.pageSize,
                function(questions) {
                    window.vueQuestions.questions = [];
                    window.vueQuestions.questions = questions;
                }
            );
        }
    });

    // 导出标记过的问题
    $('#export-marked-questions-button').click(function(event) {
        var $form = $('<form method="GET"></form>');
        $form.attr('action', Urls.REST_MARKED_QUESTION_IDS);
        $form.appendTo($('body'));
        $form.submit();
    });
});

function scrollToTop(callback) {
    if ($('html').scrollTop()) {
        $('html').animate({ scrollTop: 0 }, callback);
        return;
    }

    if ($('body').scrollTop()) {
        $('body').animate({ scrollTop: 0 }, callback);
        return;
    }

    callback();
}

// 设置当前页
window.setPageNumber = function(pageNumber) {
    $('#paginator').pagination('drawPage', pageNumber);
};

// 设置总页数
window.setPageCount = function(pageCount) {
    $('#paginator').pagination('updateItems', pageCount);
};

function KPTree(treeElementId) {
    this.treeElementId = treeElementId; // 树的元素 id
    this.tree = null;
    this.settings = null;

    this.init();
}

KPTree.prototype.init = function() {
    var self = this;
    var settings = self.getSettings();

    // 一次性加载知识点树
    QuestionDao.loadQuestionKnowledgePoints(function(kps) {
        for (var i = 0; i < kps.length; ++i) {
            var kp = kps[i];
            kp.name = kp.name + ' [' + kp.count + ']';
        }

        // self.tree = $.fn.zTree.init($("#question-knowledge-point-tree"), setting, kps);
        self.tree = $.fn.zTree.init($('#'+self.treeElementId), settings, kps);
    });

    // 展开知识点下的所有节点
    $('#menu-item-expand-all').click(function(event) {
        self.hideMenu();
        self.tree.expandNode(self.tree.getSelectedNodes()[0], true, true); // 会创建还不存在的 DOM，因为使用的是 lazy loading
    });
};

KPTree.prototype.showMenu = function(x, y, treeNode) {
    var self = this;
    var type = '';

    if (!treeNode) {
        type = 'root';
        self.tree.cancelSelectedNode();
    } else if (treeNode && !treeNode.noR) { // noR 属性为 true 表示禁止右键菜单
        self.tree.selectNode(treeNode);
    }

    $('#question-knowledge-point-tree-menu').css({left: x+'px', top: y+'px'}).show();

    $(document).on('mousedown', function(event) {
        if (!(event.target.id == 'question-knowledge-point-tree-menu'
            || $(event.target).parents('#question-knowledge-point-tree-menu').length>0)) {
            self.hideMenu();
        }
    });
};

/**
 * 隐藏菜单
 */
KPTree.prototype.hideMenu = function() {
    $('#question-knowledge-point-tree-menu').hide();
    $(document).off('mousedown');
};

KPTree.prototype.getSettings = function() {
    var self = this;
    var settings = {
        data: {
            simpleData: {
                enable : true,       // 启用数组结构的数据创建 zTree
                idKey  : 'id',       // 定义 node id 的 key，可自定义
                pIdKey : 'parentId', // 定义 parent id 的 key，可自定义
                rootPId: 0           // 根节点的 parentId，这个节点是看不到的，因为不存在
            }, view: {
                selectedMulti: false, // [*] 为 true 时可选择多个节点，为 false 只能选择一个，默认为 true
                showIcon: false
            }
        }, callback: {
            onClick: function(event, treeId, treeNode) {
                console.log(treeNode.id, treeNode.code, treeNode.subjectCode);
                var questionKnowledgePointId = treeNode.id;
                window.vueQuestions.knowledgePointId = questionKnowledgePointId; // 保存点击的知识点 ID

                // 加载知识点下第一页的题目
                QuestionDao.loadQuestionsUnderQuestionKnowledgePoint(questionKnowledgePointId, 1, window.pageSize, function(questions) {
                    window.vueQuestions.questions = [];
                    window.vueQuestions.questions = questions;
                });

                // 加载知识点下题目的页数
                QuestionDao.questionPageCountOfQuestionKnowledgePoint(questionKnowledgePointId, window.pageSize, function(pageCount) {
                    window.setPageCount(pageCount);
                    window.setPageNumber(1);
                    window.vueQuestions.pageCount = pageCount;
                });
            },
            onRightClick: function(event, treeId, treeNode) {
                // 在右键位置出弹出菜单
                var x = event.clientX + 2;
                var y = event.clientY + 2;
                self.showMenu(x, y, treeNode);
            }
        }
    };

    return settings;
};

/*
// 动态加载知识点树
var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey:  'id',
            pIdKey: 'parentId'
        }
    }, view: {
        selectedMulti: false, // [*] 为 true 时可选择多个节点，为 false 只能选择一个，默认为 true
        showIcon: false
    },
    async: {
        enable: true,
        type: 'get',
        url: function(treeId, treeNode) {
            // 1. treeNode 存在时一次性加载对应科目的所有知识点
            // 2. treeNode 不存在时表示需要加载根节点下的数据
            if (treeNode) {
                // 如果父节点已经存在，不需要再加载了(Hack)
                if (treeNode.parentId) { return Urls.REST_EMPTY; }

                return Urls.REST_QUESTION_KNOWLEDGE_POINTS_BY_SUBJECT_CODE.format({subjectCode: treeNode.subjectCode});
            } else {
                return Urls.REST_QUESTION_KNOWLEDGE_POINTS_BY_PARENT_ID.format({parentId: 0});
            }
        },
        dataFilter: function(treeId, parentNode, result) {
            var kps = result.data;
            if (kps) {
                for (var i = 0; i < kps.length; i++) {
                    var kp = kps[i];
                    kp.isParent = true;
                    kp.name = kp.name + ' - (' + kp.count + ')';
                }
            }
            return kps;
        }
    },
    callback: {
        onClick: function(event, treeId, treeNode) {
            console.log(treeNode.id, treeNode.code);
            var questionKnowledgePointId = treeNode.id;
            window.vueQuestions.knowledgePointId = questionKnowledgePointId; // 保存点击的知识点 ID

            QuestionDao.loadQuestionsUnderQuestionKnowledgePoint(questionKnowledgePointId, function(questions) {
                window.vueQuestions.questions = [];
                window.vueQuestions.questions = questions;
            });
        }
    }
};

var tree = $.fn.zTree.init($("#question-knowledge-point-tree"), setting);
*/
