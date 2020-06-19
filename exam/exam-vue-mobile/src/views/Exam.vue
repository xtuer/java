<!-- 考试 -->
<template>
    <div class="exam">
        <Navigator title="考试" :height="200"/>

        <div class="content">
            <img src="/static-m/img/exam/person.png" class="person">

            <van-skeleton :loading="loading" title :row="6">
                <!-- 试卷信息 -->
                <div class="box exam">
                    <div class="title">{{ exam.title }}</div>
                    <div class="label">开始时间: {{ exam.startTime | formatDate('YYYY-MM-DD HH:mm') }}</div>
                    <div class="label">结束时间: {{ exam.endTime | formatDate('YYYY-MM-DD HH:mm') }}</div>
                    <div class="label">考试限时: {{ parseInt(exam.duration / 60) }} 分钟</div>
                    <div class="label">作答次数: 最多 {{ exam.maxTimes }} 次</div>

                    <!-- 状态: 0 (未开始)、1 (考试中)、2 (已结束) -->
                    <div class="label">
                        考试状态:
                        <van-tag v-if="exam.status === 0" plain>{{ exam.statusLabel }}</van-tag>
                        <van-tag v-else-if="exam.status === 1" type="success" plain>{{ exam.statusLabel }}</van-tag>
                        <van-tag v-else-if="exam.status === 2" type="warning" plain>{{ exam.statusLabel }}</van-tag>
                    </div>
                </div>

                <!-- 考试记录 -->
                <div v-for="(record, index) in exam.examRecords" :key="record.id" class="box exam-record">
                    <div class="title">第{{ index+1 | toCnNumber }}次作答</div>

                    <!-- 状态: 0 (已作答)、1 (已提交)、2 (自动批改)、3 (手动批改)、4 (批改结束) -->
                    <div class="extra">
                        <!-- 未提交且考试中，继续作答 -->
                        <van-button v-if="exam.status === 1 && record.status === 0"
                                    plain round type="info" size="small"
                                    @click="toExamDo(record.id)">
                            继续作答
                        </van-button>

                        <!-- 批改结束，显示分数 -->
                        <Score v-else-if="record.status === 4" :score="record.score" @click.native="toExamCorrected(record.id)"/>

                        <!-- 其他状态未批改中 -->
                        <van-button v-else plain round type="warning" size="small" @click="toExamCorrectWaiting(record.id)">
                            等待批改
                        </van-button>
                    </div>

                    <!-- 时间 -->
                    <div class="time">
                        <div class="elapsed">用时: {{ parseInt(record.elapsedTime / 60) }} 分</div>
                        <div class="start-at">{{ record.startAt | formatDate('MM-DD HH:mm')}}</div>
                    </div>
                </div>

                <van-button v-if="canStartNewExam"
                        :loading="creating" loading-text="准备中..." type="info"
                        @click="createExamRecord">
                    开始考试
                </van-button>
            </van-skeleton>
        </div>
    </div>
</template>

<script>
import ExamDao from '@/../public/static-m/js/dao/ExamDao';
import Score from '@/components/Score';

export default {
    components: { Score },
    data() {
        return {
            examId: this.$route.params.examId,
            exam: {
                status     : 0,
                maxTimes   : 0,
                examRecords: [],
            },
            loading : false,
            creating: false,
        };
    },
    mounted() {
        // 查找考试信息, 如果在此考试中进行过作答，同时查找出所有相关的考试记录
        this.loading = true;
        ExamDao.findExam(this.userId, this.examId).then(exam => {
            this.exam = exam;
            this.loading = false;
        });
    },
    methods: {
        // 创建考试记录
        createExamRecord() {
            this.creating = true;
            ExamDao.createExamRecord(this.userId, this.examId).then(examRecordId => {
                // 创建成功，跳转到考试作答
                this.toExamDo(examRecordId);
                this.creating = false;
            });
        },
        // 跳转到考试作答
        toExamDo(examRecordId) {
            this.$router.push({ name: 'exam-do', params: { examId: this.examId, examRecordId } });
        },
        // 跳转到等待批改
        toExamCorrectWaiting(examRecordId) {
            this.$router.push({ name: 'exam-correct-waiting', params: { examId: this.examId, examRecordId } });
        },
        // 跳转到已批改
        toExamCorrected(examRecordId) {
            this.$router.push({ name: 'exam-corrected', params: { examId: this.examId, examRecordId } });
        }
    },
    computed: {
        userId() {
            return this.$store.state.user.id;
        },
        // 是否可以开始新的考试 (考试中且未达到最大考试次数)
        canStartNewExam() {
            return this.exam.status === 1 && this.exam.examRecords.length < this.exam.maxTimes;
        },
    }
};
</script>

<style lang="scss">
.exam {
    .navigator {
        margin-bottom: -140px;
    }

    .content {
        display: grid;
        grid-template-columns: 1fr;
        padding: 20px;
        font-size: $normalFontSize;
        color: $iconColor;
    }

    .box {
        box-shadow: 0px 0px 30px 0px rgba(146, 150, 198, 0.38);
        border-radius: 8px;
        padding: 20px;
        background-color: white;
        margin-bottom: 20px;
    }

    .person {
        width: 40%;
        object-fit: contain;
        justify-self: center;
    }

    .title {
        color: $normalColor;
        font-size: $bigFontSize;
        font-weight: bold;
    }

    .exam {
        display: grid;
        grid-template-columns: 1fr;
        grid-gap: 6px;
        align-items: center;

        .title {
            text-align: center;
            margin-bottom: 10px;
        }
    }

    .exam-record {
        display: grid;
        grid-template-columns: 1fr max-content;
        grid-gap: 10px;

        .time {
            display: grid;
            grid-template-columns: max-content 1fr;
            grid-gap: 30px;
            align-items: center;
            align-content: center;
            height: 20px;
            line-height: 20px;
        }

        .extra {
            display: grid;
            align-items: center;
            grid-row: span 2;

            .score {
                width : 80px;
                height: 50px;
                font-size: 34px;
            }
        }
    }
}
</style>
