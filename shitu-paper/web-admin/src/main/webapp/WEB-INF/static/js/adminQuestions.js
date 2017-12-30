require(['jquery', 'vue', 'semanticUi', 'ztree', 'layer', 'rest', 'util', 'urls', 'question'], function($, Vue) {
    Util.activateSidebarItem(2);

    // 加载知识点树
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
                // treeNode 不存在时表示需要加载根节点下的数据
                var parentId = (treeNode ? treeNode.id : 0);
                return Urls.REST_QUESTION_KNOWLEDGE_POINTS_BY_PARENT_ID.format({parentId: parentId});
            },
            dataFilter: function(treeId, parentNode, result) {
                var kps = result.data;
                if (kps) {
                    for (var i = 0; i < kps.length; i++) {
                        var kp = kps[i];
                        kp.isParent = true;
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

    // 题目的 vue 对象
    window.vueQuestions = new Vue({
        el: '#vue-questions',
        data: {
            questions: [], // 题目
            knowledgePointId: 0 // 题目的知识点 ID
        },
        methods: {
            toggleQuestionMark: function(question) {
                QuestionDao.toggleQuestionMark(question.id, function() {
                    question.marked = !question.marked;
                });
            }
        }
    });
});
