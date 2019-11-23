<template>
    <div class="exams">
        <Button type="primary" @click="popupModal">创建考试</Button>
        <div class="exam-list">
            <Button v-for="exam in exams"
                    :key="exam.id"
                    :to="{ name: 'user-exam', params: { userId: 1, examId: exam.id }}">
                <Tag :color="statusColor(exam)">{{ exam.statusLabel }}</Tag> {{ exam.title}}
            </Button>
        </div>

        <!-- 考试编辑弹窗 -->
        <Modal v-model="modal" title="创建考试" :mask-closable="false" class="create-exam-modal">
            <Form ref="examForm" :model="editedExam" :rules="rules" :label-width="90">
                <FormItem label="考试标题:" prop="title">
                    <Input v-model="editedExam.title" placeholder="请输入考试标题"/>
                </FormItem>

                <FormItem label="选择试卷:" prop="paperIds">
                    <Input v-model="editedExam.paperIds" placeholder="请输入试卷 ID"/>
                </FormItem>

                <FormItem label="考试时间:" prop="timeRange">
                    <DatePicker v-model="editedExam.timeRange"
                                :editable="false"
                                type="datetimerange"
                                format="yyyy-MM-dd HH:mm"
                                separator=" 到 "
                                placement="bottom-end"
                                placeholder="请选择考试时间"
                                style="width: 100%">
                    </DatePicker>
                </FormItem>

                <FormItem label="考试时长:" prop="duration">
                    <InputNumber v-model="editedExam.duration" :formatter="value => `${value} 分钟`" :min="10"/>
                </FormItem>

                <FormItem label="考试次数:" prop="maxTimes">
                    <InputNumber v-model="editedExam.maxTimes" :formatter="value => `${value} 次`" :min="1"/>
                </FormItem>
            </Form>

            <div slot="footer">
                <Button type="primary" :loading="loading" @click="createExam">保存</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
export default {
    data() {
        return {
            exams: [],
            editedExam: {},
            modal: false,
            loading: false,

            // 表单验证规则
            rules: {
                // 字符串验证
                title: [
                    { required: true, whitespace: true, message: '试卷标题不能为空', trigger: 'blur' }
                ],
                // 正则验证
                paperIds: [
                    { required: true, pattern: /^\d+$/, message: '请输入正确的试卷 ID (数字)', trigger: 'blur' }
                ],
                // 时间范围验证
                timeRange: [
                    { required: true, message: '请选择考试时间', trigger: 'blur', validator: (rule, value, callback) => {
                        if (value.length !== 2) {
                            // 时间范围有 2 个元素，开始时间和结束时间
                            callback(new Error());
                        } else if (value.some(e => !e)) {
                            // 时间值不能为空
                            callback(new Error());
                        } else {
                            callback();
                        }
                    } }
                ],
                // 逻辑验证，validator 使用 Lambda，可以访问 Vue.prototype 中的数据
                duration: [
                    { message: '考试时长不能超过考试时间范围', trigger: 'change', validator: (rule, value, callback) => {
                        const startTime = this.editedExam.timeRange[0];
                        const endTime   = this.editedExam.timeRange[1];

                        if (!startTime || !endTime) {
                            callback(new Error());
                            return;
                        }

                        // 把时间段的毫秒转为分
                        const minutes = (endTime.getTime() - startTime.getTime()) / 1000 / 60;
                        if (value > minutes) { // value 即为 duration
                            callback(new Error());
                            return;
                        }

                        callback();
                    } }
                ],
            },
        };
    },
    mounted() {
        ExamDao.findExamsOfCurrentOrg().then(exams => {
            this.exams = exams;
        });
    },
    methods: {
        // 弹出创建考试对话框
        popupModal() {
            this.$refs.examForm.resetFields();
            this.modal = true;

            this.editedExam = {
                id       : '0', // 考试 ID
                title    : '',  // 考试标题
                paperIds : '',  // 试卷 ID
                startTime: '',  // 开始时间
                endTime  : '',  // 结束时间
                duration : 60,  // 考试时长
                maxTimes : 1,   // 允许考试次数
                timeRange: [],  // 时间选择器使用
            };
        },
        // 创建考试
        createExam() {
            this.$refs.examForm.validate(valid => {
                if (!valid) { return; }

                // 从时间选择器中获取开始和结束时间，表单提交使用
                this.loading = true;
                this.editedExam.startTime = this.editedExam.timeRange[0];
                this.editedExam.endTime   = this.editedExam.timeRange[1];

                // 试卷转为字符串对象
                ExamDao.upsertExam(this.editedExam).then(examId => {
                    this.editedExam.id = examId;
                    this.modal = false;
                    this.loading = false;
                    this.exams.push(this.editedExam);
                }).catch(() => {
                    this.loading = false;
                });
            });
        },
        statusColor(exam) {
            const colors = ['warning', 'success', 'error'];
            return colors[exam.status];
        },
    }
};
</script>

<style lang="scss">
.exams {
    .exam-list {
        padding: 20px 0;

        a {
            margin-right: 12px;
        }
    }
}
</style>
