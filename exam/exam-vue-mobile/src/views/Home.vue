<template>
    <div class="home">
        <Navigator title="所有考试"/>

        <van-skeleton title :row="3" :loading="loading">
            <div class="exams">
                <van-button v-for="exam in exams" :key="exam.id" block class="exam">
                    {{ exam.title }}
                </van-button>
            </div>
        </van-skeleton>
    </div>
</template>

<script>
import ExamDao from '@/../public/static-m/js/dao/ExamDao';

export default {
    data() {
        return {
            exams: [], // 考试
            loading: false,
        };
    },
    mounted() {
        this.loading = true;
        ExamDao.findExamsOfCurrentOrg().then(exams => {
            this.exams = exams;
            this.loading = false;
        });
    },
    methods: {
        toExam(exam) {
            this.$router.push({ name: 'exam' });
        }
    }
};
</script>

<style lang="scss">
.home {
    width: 100%;
    height: 100%;

    .exams {
        padding: 20px;

        .exam {
            margin-bottom: 10px;
        }
    }
}
</style>
