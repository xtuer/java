<!-- 试卷编辑 -->
<template>
    <div class="paper-edit">
        <!-- 试卷的标题 -->
        <div class="paper-title">
            <Poptip transfer width="400">
                <div class="editable-label">{{ paper.title | trim('请输入试卷的标题') }}</div>
                <div slot="content">
                    <Input v-model="paper.title" placeholder="请输入试卷的标题"/>
                </div>
            </Poptip>
        </div>

        <!-- 试卷的介绍 -->
        <div class="paper-info">
            <Poptip transfer width="500" class="poptip-block">
                <div class="editable-label">{{ paper.info | trim('请在此输入本次考试的要点：如考试对象、考核点、建议用时等提示信息，可不填') }}</div>
                <div slot="content">
                    <Input v-model="paper.info" type="textarea" :autosize="{ minRows: 8 }" placeholder="请输入试卷的介绍"/>
                </div>
            </Poptip>
        </div>

        <!-- 题目列表 -->
        <div class="questions">
            <Question v-for="question in questions"
                      :question="question"
                      :key="question.id"
                      score-show
                      score-edit
                      analysis-show
                      readonly
                      toolbar
                      @on-append-question-to-group-click="appendQuestionToGroup"
                      @on-delete-question-click="deleteQuestion"
                      @on-edit-question-click="editQuestion"
                      @on-score-change="updateQuestionStatus"
                      @on-move-up-click="moveQuestionUp"
                      @on-move-down-click="moveQuestionDown"/>
        </div>

        <!-- 工具栏 -->
        <div class="toolbar">
            <!-- 添加题型下拉框 -->
            <Dropdown slot="extra" transfer @on-click="appendGroupQuestion">
                <Button icon="md-add">添加题型</Button>
                <DropdownMenu slot="list">
                    <DropdownItem v-for="type in groupQuestionTypes" :key="type.value" :name="type.value">{{ type.name }}</DropdownItem>
                </DropdownMenu>
            </Dropdown>

            <Button :loading="saving" type="primary" @click="save">保存 (共 {{ paper.totalScore }} 分)</Button>
            <!-- <Tag color="success" style="margin-left: 20px">试卷类型: {{ paper.objective ? '客观题试卷' : '主观题试卷' }}</Tag> -->
        </div>

        <!-- 题目编辑对话框 -->
        <Modal v-model="modal" :title="`编辑-${questionTypeName(editedQuestion.type)}`" width="800" :styles="{ top: '40px', marginBottom: '40px' }"
                :mask-closable="false" class="edit-question-modal">
            <!-- 编辑的时候不要用共享的 Question，否则 RichText 复用时数据会乱 -->
            <Question v-if="modal" ref="editedQuestion"
                      :question="editedQuestion"
                      :key="editedQuestion.id"
                      @on-append-sub-question="updateQuestionStatus"
                      @on-delete-sub-question="updateQuestionStatus"/>

            <div slot="footer">
                <Button type="primary" @click="modal = false">确定</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import Question from '@/components/Question.vue';
import PaperDao from '@/../public/static-p/js/dao/PaperDao';
import QuestionUtils from '@/../public/static-p/js/util/QuestionUtils';

export default {
    components: { Question },
    data() {
        return {
            paper: {
                id        : this.$route.params.paperId, // 试卷 ID
                title     : '', // 试卷的标题
                info      : '', // 试卷的介绍
                questions : [], // 试卷的题目
                totalScore: 0,  // 试卷的满分
            },
            editedQuestion: { stem: '' },
            modal : false,
            saving: false,
        };
    },
    mounted() {
        PaperDao.findPaperById(this.paper.id).then(paper => {
            this.paper = paper;
        });
    },
    methods: {
        // 添加题型题
        appendGroupQuestion(type) {
            // 1. 题目的题干为此组题型的名字
            // 2. purpose 为此组题型的类型
            // 3. 添加题型题到 paper
            // 4. 更新题目状态

            const stem            = this.questionTypeName(type);
            const purpose         = type;
            const groupQuestion   = QuestionUtils.createQuestion(QUESTION_TYPE.DESCRIPTION, stem, purpose);
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
        // 保存试卷
        save() {
            // 1. 克隆试卷
            // 2. 数据校验
            //    2.1 试卷标题不能为空
            //    2.2 普通试卷的客观题必须有正确答案
            // 3. 保存到服务器

            // [1] 克隆试卷
            let paper = JSON.parse(JSON.stringify(this.paper));
            paper.questions = QuestionUtils.cleanQuestions(paper.questions);

            // [2] 数据校验
            // [2.1] 试卷标题不能为空
            if (!paper.title) {
                this.$Notice.warning({ title: '没有输入试卷的标题', desc: '' });
                return;
            }

            // [2.2] 普通试卷的客观题必须有正确答案
            if (this.paperType === 0) {
                const sqs = QuestionUtils.getObjectiveQuestions(paper.questions);

                for (let q of sqs) {
                    const checkedOptionsCount = q.options.filter(o => o.correct).length;

                    if (checkedOptionsCount === 0) {
                        this.$Notice.warning({ title: `题目 ${q.snLabel} 没有选择正确答案`, desc: '' });
                        return;
                    }
                }
            }

            // [3] 保存到服务器
            this.saving = true;
            PaperDao.upsertPaper(paper).then(paperId => {
                this.saving = false;

                if (Utils.isValidId(this.paper.id)) {
                    // 编辑试卷时从服务器加载最新的试卷
                    return PaperDao.findPaper(paperId);
                } else if (this.paperType === 0) {
                    // 创建试卷时跳转到试卷编辑页面
                    this.$router.replace({ name: 'paper-edit', params: { id: paperId } });
                } else if (this.paperType === 1) {
                    // 创建问卷时跳转到问卷编辑页面
                    this.$router.replace({ name: 'questionaire-edit', params: { id: paperId } });
                }

                return null;
            }).then(newPaper => {
                if (newPaper) {
                    this.paper = newPaper;
                }
            });
        },
    },
    computed: {
        // 可用题目，去掉被删除的题目
        questions() {
            return this.paper.questions.filter(q => !q.deleted);
        },
        // 题型题类型
        groupQuestionTypes() {
            return window.QUESTION_TYPES.filter(t => t.value < window.QUESTION_TYPE.DESCRIPTION);
        }
    }
};
</script>

<style lang="scss">
.paper-edit {
    margin: 0 150px;

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
            cursor: pointer;
            min-width: 400px;
            border: 1px dashed rgba(0, 0, 0, 0.1);
            border-radius: 4px;
            padding: 2px 4px;
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

        > button:last-child {
            margin-left: 12px;
            width: 160px;
        }
    }
}
</style>
