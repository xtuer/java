<!--
功能: 试卷编辑，可显示和隐藏分数，显示分数时可编辑分数，也可以隐藏和显示参考答案、解析

属性:
paper-id  : [必要] 试卷 ID
paper-type: [可选] 试卷类型: 0 (普通试卷)、1 (调查问卷)
score-show: [可选] 是否显示分数
score-edit: [可选] 是否可修改分数 (score-show 时才会生效)
key-hide  : [可选] 隐藏参考答案
analysis-hide: [可选] 隐藏解析

事件: 无

Slot: 无

示例:
<PaperEdit :paper-id="paperId" :paper-type="0" score-show score-edit/>
<PaperEdit :paper-id="paperId" :paper-type="1" key-hide analysis-hide/>
-->

<template>
    <div class="paper-edit-x">
        <!-- 试卷的标题 -->
        <div class="paper-title">
            <Poptip trigger="hover" width="400">
                <div class="editable-label">{{ paper.title | trim('请输入试卷的标题') }}</div>
                <div slot="content">
                    <Input v-model="paper.title" placeholder="请输入试卷的标题"/>
                </div>
            </Poptip>
            <Button type="text" icon="md-arrow-back" class="back-button" @click="goBack()">返回</Button>
        </div>

        <!-- 试卷的介绍 -->
        <div class="paper-info">
            <Poptip trigger="hover" transfer width="500" class="poptip-block">
                <div class="editable-label">{{ paper.info | trim('请在此输入本次考试的要点：如考试对象、考核点、建议用时等提示信息，可不填') }}</div>
                <div slot="content">
                    <Input v-model="paper.info" type="textarea" autosize placeholder="请输入试卷的介绍"/>
                </div>
            </Poptip>
        </div>

        <!-- 题目列表 -->
        <div class="questions">
            <Question v-for="question in questions"
                      :question="question"
                      :score-show="scoreShow"
                      :score-edit="scoreEdit"
                      :key-hide="keyHide"
                      :analysis-hide="analysisHide"
                      :key="question.id"
                      readonly toolbar
                      @on-append-question-to-group-click="appendQuestionToGroup"
                      @on-delete-question-click="deleteQuestion"
                      @on-edit-question-click="editQuestion"
                      @on-score-change="updateQuestionStatus"
                      @on-move-up-click="moveQuestionUp"
                      @on-move-down-click="moveQuestionDown"/>
        </div>

        <!-- 工具栏 -->
        <!-- 普通试卷的工具栏 -->
        <div v-if="paperType === 0" class="toolbar">
            <!-- 添加题型下拉框 -->
            <Dropdown slot="extra" transfer @on-click="appendGroupQuestion">
                <Button icon="md-add">添加题型</Button>
                <DropdownMenu slot="list">
                    <DropdownItem v-for="type in questionTypes" :key="type.value" :name="type.value">{{ type.name }}</DropdownItem>
                </DropdownMenu>
            </Dropdown>

            <Button type="primary" style="margin-left: 12px" @click="save">保存 (共 {{ paper.totalScore }} 分)</Button>
            <Tag color="success" style="margin-left: 20px">试卷类型: {{ paper.objective ? '客观题试卷' : '主观题试卷' }}</Tag>
        </div>
        <!-- 调查问卷的工具栏 -->
        <div v-else class="toolbar">
            <!-- 添加题型下拉框 -->
            <Dropdown slot="extra" transfer @on-click="appendQuestion">
                <Button icon="md-add">添加题型</Button>
                <DropdownMenu slot="list">
                    <DropdownItem v-for="type in questionTypes" :key="type.value" :name="type.value">{{ type.name }}</DropdownItem>
                </DropdownMenu>
            </Dropdown>

            <Button type="primary" style="margin-left: 12px; width: 100px" @click="save">保存</Button>
        </div>

        <!-- 编辑题目对话框 -->
        <Modal v-model="modal" :title="`编辑-${questionTypeName(editedQuestion.type)}`" width="800" :styles="{top: '40px', marginBottom: '40px'}"
                :mask-closable="false" class="edit-question-modal">
            <!-- 编辑的时候不要用共享的 Question，否则 RichText 复用时数据会乱 -->
            <Question v-if="modal" ref="editedQuestion"
                      :question="editedQuestion"
                      :key-hide="keyHide"
                      :analysis-hide="analysisHide"
                      @on-append-sub-question="updateQuestionStatus"
                      @on-delete-sub-question="updateQuestionStatus"/>

            <div slot="footer">
                <Button type="primary" @click="modal = false">确定</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import PaperDao from '@/../public/static/js/dao/PaperDao';

export default {
    props: {
        paperId  : { type: String, required: true  }, // 试卷 ID
        paperType: { type: Number,  default : 0    }, // 试卷类型
        scoreShow: { type: Boolean, default: false }, // 显示分数
        scoreEdit: { type: Boolean, default: false }, // 编辑分数
        keyHide  : { type: Boolean, default: false }, // 隐藏参考答案
        analysisHide: { type: Boolean, default: false }, // 隐藏解析
    },
    data() {
        return {
            paper: {
                id        : '0',
                title     : '', // 试卷的标题
                info      : '', // 试卷的介绍
                type      : this.paperType, // 试卷类型
                questions : [], // 试卷的题目
                totalScore: 0,  // 试卷的满分
            },
            editedQuestion: { stem: '' },
            modal: false,
        };
    },
    created() {
        this.init();
    },
    methods: {
        // 添加题型
        appendGroupQuestion(type) {
            // 1. 题目的题干为此组题型的名字
            // 2. purpose 为此组题型的类型
            // 3. 添加题型题到 paper
            // 4. 更新题目状态

            const stem = this.questionTypeName(type);
            const purpose = type;
            const groupQuestion = QuestionUtils.createQuestion(QUESTION_TYPE.DESCRIPTION, stem, purpose);
            groupQuestion.groupSn = QuestionUtils.nextGroupSn(this.paper.questions);
            this.paper.questions.push(groupQuestion);
            this.updateQuestionStatus();
        },
        // 添加题目到题型题 groupQuestion 所属题目组
        appendQuestionToGroup(groupQuestion) {
            QuestionUtils.appendQuestionToGroup(this.paper.questions, groupQuestion);
            this.updateQuestionStatus();
        },
        // 添加普通题目，调查问卷使用
        appendQuestion(type) {
            const stem = this.questionTypeName(type);
            const question = QuestionUtils.createQuestion(type, stem, 1);
            this.paper.questions.push(question);
            this.updateQuestionStatus();
        },
        // 删除题目或者题型
        deleteQuestion(question) {
            // 1. 如果 question 是题型题，则删除整个题型的题目
            // 2. 如果 question 是普通题，则删除这个题目
            const group = question.type === QUESTION_TYPE.DESCRIPTION; // 是否题型题

            this.$Modal.confirm({
                title: `确定要删除${group ? '整个题型' : '题目'}吗？`,
                onOk: () => {
                    if (group) {
                        QuestionUtils.deleteGroupQuestions(this.paper.questions, question);
                    } else {
                        QuestionUtils.deleteQuestion(question);
                    }

                    this.updateQuestionStatus();
                }
            });
        },
        // 弹出编辑题目对话框，编辑题目
        editQuestion(question) {
            // 1. 恢复得到焦点的 Tab 为默认值，编辑复合题时会用到
            // 2. 赋值 question 为正在编辑的题目
            // 3. 弹出编辑对话框

            this.modal = true;
            this.editedQuestion = question;
        },
        // 更新题目的状态
        updateQuestionStatus() {
            // 1. 更新题目的序号
            // 2. 更新题目的得分
            QuestionUtils.updateQuestionSnLabels(this.paper.questions);
            this.paper.totalScore = QuestionUtils.updateQuestionScores(this.paper.questions);
        },
        // 保存试卷
        save() {
            let paper = JSON.parse(JSON.stringify(this.paper));
            paper.questions = QuestionUtils.cleanQuestions(paper.questions);

            if (!paper.title) {
                this.$Notice.error({ title: '错误', desc: '没有输入试卷的标题' });
                return;
            }

            PaperDao.upsertPaper(paper).then(paperId => {
                if (Utils.isValidId(this.paper.id)) {
                    // 编辑试卷时从服务器加载最新的试卷
                    return PaperDao.findPaper(paperId);
                } else {
                    // 创建试卷时跳转到试卷编辑页面
                    this.$router.push({ name: 'paper-edit', params: { id: paperId } });
                    return null;
                }
            }).then(newPaper => {
                this.paper = newPaper;
            });
        },
        // 向上移动题目
        moveQuestionUp(question) {
            // 1. 找到题目的下标
            // 2. 找到前一个 groupSn 相同的题目: 非题型题、未被删除的题目
            // 3. 把题目从 questions 中删除，然后插入到找到的位置
            // 4. 更新题目的 snLabel

            // [1] 找到题目的下标
            const questions = this.paper.questions;
            const position  = questions.findIndex(q => q.id === question.id);

            // [2] 找到前一个 groupSn 相同的题目: 非题型题、未被删除的题目
            for (let i = position - 1; i >= 0; i--) {
                const prev = questions[i];

                if (prev.deleted) {
                    continue;
                }

                if (prev.groupSn !== question.groupSn) {
                    break;
                }

                if (prev.type === QUESTION_TYPE.DESCRIPTION) {
                    break;
                }

                // [3] 把题目从 questions 中删除，然后插入到找到的位置
                // 向上移动
                questions.remove(position);
                questions.insert(i, question);

                // [4] 更新题目的 snLabel
                this.updateQuestionStatus();

                break;
            }
        },
        // 向下移动题目
        moveQuestionDown(question) {
            // 1. 找到题目的下标
            // 2. 找到下一个 groupSn 相同的题目: 非题型题、未被删除的题目
            // 3. 把题目从 questions 中删除，然后插入到找到的位置
            // 4. 更新题目的 snLabel

            // [1] 找到题目的下标
            const questions = this.paper.questions;
            const position = questions.findIndex(q => q.id === question.id);

            // [2] 找到下一个 groupSn 相同的题目: 非题型题、未被删除的题目
            for (let i = position + 1; i < questions.length; i++) {
                const next = questions[i];

                if (next.deleted) {
                    continue;
                }

                if (next.groupSn !== question.groupSn) {
                    break;
                }

                if (next.type === QUESTION_TYPE.DESCRIPTION) {
                    break;
                }

                // [3] 把题目从 questions 中删除，然后插入到找到的位置
                // 向下移动
                questions.insert(i+1, question);
                questions.remove(position);

                // [4] 更新题目的 snLabel
                this.updateQuestionStatus();

                break;
            }
        },
        // 初始化
        init() {
            // 1. paperId 有效时从服务器获取此 ID 的试卷
            // 2. paperId 无效时创建一个默认的试卷
            if (Utils.isValidId(this.paperId)) {
                PaperDao.findPaper(this.paperId).then(paper => {
                    this.paper = paper;
                });
            } else if (this.paperType === 0) {
                // 普通试卷则添加一个默认的题型
                this.appendGroupQuestion(1);
            }
        }
    },
    computed: {
        // 可用题目，去掉被删除的题目
        questions() {
            return this.paper.questions.filter(q => !q.deleted);
        },
        // 题目的类型，由试卷的类型决定
        questionTypes() {
            if (this.paperType === 0) {
                // 单选题、多选题、判断题、填空题、问答题、复合题
                return QUESTION_TYPES.slice(0, 6);
            } else {
                // 星级评价题、单选题、多选题、问答题
                return [QUESTION_TYPES[0], QUESTION_TYPES[1], QUESTION_TYPES[4], QUESTION_TYPES[7]];
            }
        },
    },
    watch: {
        // 如果路由有变化，会再次执行该方法
        $route: 'init'
    },
};
</script>

<style lang="scss">
.paper-edit-x {
    width: 800px;
    margin: 20px auto;
    padding: 20px;
    border: 1px solid #eee;
    border-radius: 4px;

    .questions {
        > .question {
            margin-bottom: 12px;
        }
    }

    .paper-title, .paper-info {
        text-align: center;
        font-size: 18px;
        font-weight: bold;
        margin-bottom: 24px;

        .editable-label {
            color: $primaryColor;
            cursor: default;
            min-width: 400px;
            border: 1px dashed rgba(0, 0, 0, 0.1);
            border-radius: 4px;
            padding: 2px 4px;
        }
    }

    .paper-title {
        position: relative;

        .back-button {
            position: absolute;
            left: 0;
            padding: 0;
        }
    }

    .paper-info {
        text-align: left;
        font-weight: normal;
        font-size: 14px;
    }

    > .toolbar {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 24px;
    }
}
</style>
