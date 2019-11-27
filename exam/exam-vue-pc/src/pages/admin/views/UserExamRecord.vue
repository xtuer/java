<template>
    <div class="user-exam-record">
        <!-- {{ examRecord }} -->

        <QuestionX v-for="question in examRecord.paper.questions"
                   :key="question.id"
                   :question="question"
                   :answerable="canContinueExam(examRecord)"
                   @on-answer="answerQuestion"/>
        <Button v-if="canContinueExam(examRecord)" type="primary" style="margin-top: 24px" @click="submit">提交</Button>
    </div>
</template>

<script>
import QuestionX from '@/components/QuestionX.vue';

export default {
    components: { QuestionX },
    data() {
        return {
            userId: '',
            examId: '',
            recordId: '',
            examRecord: {
                exam: {},
                paper: {},
            },
            loaded: false,
        };
    },
    mounted() {
        this.userId = this.$route.params.userId;
        this.examId = this.$route.params.examId;
        this.recordId = this.$route.params.recordId;

        ExamDao.findExamRecord(this.userId, this.examId, this.recordId).then(examRecord => {
            this.examRecord = examRecord;
            this.loaded = true;
        });
    },
    methods: {
        // 获取问题的答案
        getOptionAnswers(question) {
            const answers = [];

            if (question.type === QUESTION_TYPE.SINGLE_CHOICE || question.type === QUESTION_TYPE.MULTIPLE_CHOICE || question.type === QUESTION_TYPE.TFNG) {
                // 1. 客观题: 单选题、多选题、判断题
                question.options.filter(o => o.checked).forEach(o => {
                    answers.push({ questionId: question.id, questionOptionId: o.id, content: '' });
                });
            } else if (question.type === QUESTION_TYPE.FITB || question.type === QUESTION_TYPE.ESSAY_QUESTION) {
                // 2. 填空题、问答题
                question.options.forEach(o => {
                    answers.push({ questionId: question.id, questionOptionId: o.id, content: o.answer });
                });
            }

            return answers;
        },
        // 回答单个问题
        answerQuestion(question) {
            const recordAnswer = { submitted: false, answers: this.getOptionAnswers(question) };
            ExamDao.answerExamRecord(this.userId, this.examId, this.recordId, recordAnswer);
        },
        // 提交试卷
        submit() {
            const recordAnswer = { submitted: true, answers: [] };

            this.examRecord.paper.questions.forEach(question => {
                recordAnswer.answers.push(...this.getOptionAnswers(question));

                question.subQuestions.forEach(sub => {
                    recordAnswer.answers.push(...this.getOptionAnswers(sub));
                });
            });

            ExamDao.answerExamRecord(this.userId, this.examId, this.recordId, recordAnswer).then(() => {
                this.$Message.success('提交试卷成功');
            });
        },
        // 是否可以继续考试: 考试期间、未提交、考试记录的时间未用完
        canContinueExam(examRecord) {
            if (!this.loaded) {
                return false;
            } else {
                return examRecord.exam.status === 1 && examRecord.status < 2 && examRecord.elapsedTime < examRecord.exam.duration;
            }
        }
    }
};
</script>

<style lang="scss">

</style>
