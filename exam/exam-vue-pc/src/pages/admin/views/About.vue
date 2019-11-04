<template>
    <div class="about">
        <Question v-for="question in questions" :key="question.id" :question="question" :editable="question.editable" border score-show/>
        <Button @click="toggleEdit">Toggle 编辑</Button>
    </div>
</template>

<script>
import Question from '@/components/Question.vue';

export default {
    components: { Question, },
    data() {
        return {
            questions: [],
        };
    },
    mounted() {
        this.questions.push(QuestionUtils.createQuestion(7, '题型题'));
        this.questions.push(QuestionUtils.createQuestion(1, '单选题'));
        // this.questions.push(QuestionUtils.createQuestion(3, '判断题'));
        // this.questions.push(QuestionUtils.createQuestion(4, '问答题'));

        // this.questions[0].options[1].correct = true;
        // this.questions[1].options[1].correct = true;

        // 复合题
        const complexQuestion = QuestionUtils.createQuestion(6, '复合题');
        QuestionUtils.appendSubQuestion(complexQuestion, 1);
        QuestionUtils.appendSubQuestion(complexQuestion, 2);
        this.questions.push(complexQuestion);

        QuestionUtils.updateQuestionSnLabels(this.questions);
    },
    methods: {
        toggleEdit() {
            this.questions.forEach(q => {
                this.$set(q, 'editable', !q.editable);
                // q.editable = !q.editable;
            });
        }
    }
};
</script>

<style lang="scss">
.about {
    padding: 40px;

    .question:not(.question-sub) {
        margin-bottom: 24px;
    }
}
</style>
