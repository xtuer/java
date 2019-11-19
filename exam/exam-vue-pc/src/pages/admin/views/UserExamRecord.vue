<template>
    <div class="user-exam-record">
        <Button type="primary">提交</Button>
        <div>考试记录</div>
        {{ examRecord }}
    </div>
</template>

<script>
export default {
    data() {
        return {
            userId: '',
            examId: '',
            recordId: '',
            examRecord: {},
        };
    },
    mounted() {
        this.userId = this.$route.params.userId;
        this.examId = this.$route.params.examId;
        this.recordId = this.$route.params.recordId;

        ExamDao.findExamRecord(this.userId, this.examId, this.recordId).then(examRecord => {
            this.examRecord = examRecord;
        });
    },
    methods: {}
};
</script>

<style lang="scss">

</style>
