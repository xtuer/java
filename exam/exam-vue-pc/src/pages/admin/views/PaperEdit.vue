<template>
    <div class="paper-edit">
        <div class="questions">
            <Question v-for="question in questions" :key="question.id" :question="question"
                    paper-edit toolbar
                    @on-append-question-to-group-click="appendQuestionToGroup"
                    @on-delete-question-click="deleteQuestion"
                    @on-edit-question-click="editQuestion"
                    @on-score-change="updateQuestionStatus"/>
        </div>

        <Dropdown slot="extra" transfer @on-click="appendGroupQuestion">
            <Button icon="md-add">添加题型</Button>
            <DropdownMenu slot="list">
                <DropdownItem v-for="type in questionTypes" :key="type.value" :name="type.value">{{ type.name }}</DropdownItem>
            </DropdownMenu>
        </Dropdown>

        <Button type="primary" style="margin-left: 12px" @click="save">保存 (共 {{ paper.totalScore }} 分)</Button>

        <!-- 编辑题目对话框 -->
        <Modal v-model="modal" title="编辑题目" :styles="{top: '40px', marginBottom: '40px'}" class="edit-question-modal" footer-hide>
            <!-- 编辑的时候不要用共享的 Question，否则 RichText 复用时数据会乱 -->
            <Question v-if="modal" ref="editingQuestion" :question="editingQuestion" editable
                    @on-append-sub-question="updateQuestionStatus"
                    @on-delete-sub-question="updateQuestionStatus"/>
        </Modal>
    </div>
</template>

<script>
export default {
    data() {
        return {
            paper: {
                id: '0',
                questions : [], // 试卷的题目
                totalScore: 0,  // 试卷的满分
            },
            editingQuestion: { stem: '' },
            modal: false,
        };
    },
    mounted() {
        const paperId = this.$route.params.id;

        if (Utils.isIdValid(paperId)) {
            // Fetch from server
        } else {
            this.appendGroupQuestion(4);
            // this.appendGroupQuestion(6);
        }
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
            groupQuestion.groupSn = Utils.nextSn();
            this.paper.questions.push(groupQuestion);
            this.updateQuestionStatus();
        },
        // 添加题目到题型题 groupQuestion 所属题目组
        appendQuestionToGroup(groupQuestion) {
            QuestionUtils.appendQuestionToGroup(this.paper.questions, groupQuestion);
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
            this.editingQuestion = question;
        },
        // 更新题目的状态
        updateQuestionStatus() {
            // 1. 更新题目的序号
            // 2. 更新题目的得分
            QuestionUtils.updateQuestionSnLabels(this.paper.questions);
            this.paper.totalScore = QuestionUtils.updateQuestionScores(this.paper.questions);
        },
        // 保存
        save() {
            let questions = JSON.parse(JSON.stringify(this.paper.questions));
            // console.log(JSON.stringify(questions));
            questions = QuestionUtils.cleanQuestions(questions);

            console.log(JSON.stringify(questions));
            this.$Message.success('保存成功');
        }
    },
    computed: {
        // 可用题目，去掉被删除的题目
        questions() {
            return this.paper.questions.filter(q => !q.deleted);
        },
        // 题目的类型
        questionTypes() {
            return QUESTION_TYPES;
        },
    }
};
</script>

<style lang="scss">
.paper-edit {
    width: 700px;
    margin: 20px auto;
    padding: 20px;
    border: 1px solid #eee;
    border-radius: 4px;

    .questions {
        > .question {
            margin-bottom: 12px;
        }
    }
}
</style>
