<template>
    <div class="user-exam">
        <div>
            {{ exam }}
            <Tag :color="statusColor">{{ exam.statusLabel }}</Tag>
        </div>
        <Button v-if="canStartExam" @click="startExam">开始考试</Button>
        <Button v-for="record in exam.examRecords" :key="record.id">{{ canContinueExam(record) ? '继续考试' : '查看作答' }}</Button>
    </div>
</template>

<script>
export default {
    props: {},
    data() {
        return {
            userId: '',
            examId: '',
            exam: {},
        };
    },
    mounted() {
        this.userId = this.$route.params.userId;
        this.examId = this.$route.params.examId;

        ExamDao.findUserExam(this.userId, this.examId).then(exam => {
            this.exam = exam;
        });
    },
    methods: {
        // 开始考试 (创建考试记录)
        startExam() {
            // 创建考试记录成功，进入考试页面
        },
        // 是否可以继续考试: exam.status 为 1，考试记录未提交，使用时长小于考试允许时长
        canContinueExam(examRecord) {
            return this.exam.status === 1 && !examRecord.submitted && examRecord.elapsedTime < this.exam.duration;
        }
    },
    computed: {
        // 开始考试: exam.status 为 1 并且考试记录数小于 maxTimes
        canStartExam() {
            return this.exam.status === 1 && this.exam.maxTimes > this.exam.examRecords.length;
        },
        statusColor() {
            const colors = ['warning', 'success', 'error'];
            return colors[this.exam.status];
        },
    }
};
</script>

<style lang="scss">
.user-exam {
    padding: 20px;
    button, a {
        margin-right: 12px;
    }
}
</style>
