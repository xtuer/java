<!-- 考试管理 -->
<template>
    <div class="exams">
        <!-- 顶部工具栏 -->
        <div class="toolbar-1-top">
            <Input search enter-button placeholder="请输入考试名字" @on-search="searchExams"/>
            <Button type="primary" icon="md-add" @click="editExam()">添加考试</Button>
        </div>

        <!-- 考试列表 -->
        <Table :data="exams" :columns="columns" :loading="reloading" border>
            <!-- 试卷套数 -->
            <template slot-scope="{ row: exam }" slot="paperCount">
                {{ exam.paperIds.length }}
            </template>

            <!-- 考试时间 -->
            <template slot-scope="{ row: exam }" slot="duration">
                {{ exam.duration / 60 }} 分钟
            </template>
            <template slot-scope="{ row: exam }" slot="examTime">
                {{ exam.startTime | formatDate('YYYY-MM-DD HH:mm') }} 到 {{ exam.endTime | formatDate('YYYY-MM-DD HH:mm') }}
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: exam }" slot="action">
                <Button type="primary" size="small" style="margin-right: 5px" @click="editExam(exam)">编辑</Button>
                <Button type="error" size="small" @click="deleteExam(exam)">删除</Button>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="toolbar-1-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreExams">更多...</Button>
        </div>

        <!-- 考试编辑对话框 -->
        <Modal v-model="modal" :mask-closable="false" title="考试编辑" class="edit-exam-modal" :styles="{ top: '60px', marginBottom: '40px' }">
            <Form ref="form" :model="examClone" :rules="examRules" :key="examClone.id" :label-width="90">
                <FormItem label="考试名字:" prop="title">
                    <Input v-model="examClone.title" placeholder="请输入考试名字"/>
                </FormItem>
                <FormItem label="选择试卷:" prop="paperIds">
                    <Select v-model="examClone.paperIds" multiple>
                        <Option v-for="paper in papers" :value="paper.id" :key="paper.id">{{ paper.title }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="开始时间:" prop="startTime">
                    <DatePicker v-model="examClone.startTime" type="datetime" :editable="false" format="yyyy-MM-dd HH:mm" placeholder="请选择开始时间"/>
                </FormItem>
                <FormItem label="结束时间:" prop="endTime">
                    <DatePicker v-model="examClone.endTime" type="datetime" :editable="false" format="yyyy-MM-dd HH:mm" placeholder="请选择结束时间"/>
                </FormItem>
                <FormItem label="考试时长:" prop="duration">
                    <InputNumber v-model="examClone.duration" :min="1" :formatter="value => `${value} 分钟`" :parser="value => value.replace(' 分钟', '')"/>
                </FormItem>
                <FormItem label="考试次数:" prop="maxTimes">
                    <InputNumber v-model="examClone.maxTimes" :min="1" :formatter="value => `${value} 次`" :parser="value => value.replace(' 次', '')"/>
                </FormItem>
            </Form>

            <div slot="footer">
                <Button type="text" @click="modal = false">取消</Button>
                <Button type="primary" :loading="saving" @click="saveExam">保存</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import ExamDao from '@/../public/static-p/js/dao/ExamDao';
import PaperDao from '@/../public/static-p/js/dao/PaperDao';

export default {
    data() {
        return {
            exams    : [],
            examClone: {}, // 用于编辑的考试
            filter: {      // 搜索条件
                title     : '',
                pageSize  : 200,
                pageNumber: 1,
            },
            modal    : false,
            saving   : false,
            more     : false, // 是否还有更多考试
            loading  : false, // 加载中
            reloading: false,
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'title',      title: '考试名字', minWidth: 150 },
                { slot: 'paperCount', title: '试卷套数', width: 120, align: 'center' },
                { key : 'maxTimes',   title: '可作答次数', width: 120, align: 'center' },
                { slot: 'duration',   title: '考试时长', width: 120, align: 'center' },
                { slot: 'examTime',   title: '考试时间', width: 280, align: 'center' },
                { slot: 'action',     title: '操作', width: 150, align: 'center' },
            ],
            examRules: {}, // 考试的验证规则
            papers   : [], // 可用试卷
        };
    },
    mounted() {
        this.buildExamRules();
        this.searchExams();
        this.findPapersOfCurrentOrg();
    },
    methods: {
        // 搜索考试
        searchExams() {
            this.exams             = [];
            this.more              = false;
            this.reloading         = true;
            this.filter.pageNumber = 1;

            this.fetchMoreExams();
        },
        // 点击更多按钮加载下一页的考试
        fetchMoreExams() {
            this.loading = true;

            ExamDao.findExamsOfCurrentOrg(this.filter).then(exams => {
                this.exams.push(...exams);

                this.more      = exams.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
        // 查询当前机构的试卷
        findPapersOfCurrentOrg() {
            PaperDao.findPapersOfCurrentOrg({}).then(papers => {
                this.papers.push(...papers);
            });
        },
        // 编辑考试: exam 为 undefined 表示创建，否则表示更新
        editExam(exam) {
            // 1. 重置表单，避免上一次的验证信息影响到本次编辑
            // 2. 生成编辑对象的副本
            // 3. 显示编辑对话框

            this.$refs.form.resetFields();

            if (exam) {
                // 更新
                this.examClone = Utils.clone(exam);           // 重要: 克隆对象
                this.examClone.duration = exam.duration / 60; // 秒变分
            } else {
                // 创建
                this.examClone = this.newExam();
            }

            this.modal = true;
        },
        // 保存编辑后的用户
        saveExam() {
            // 1. 表单验证不通过则返回
            // 2. 克隆被编辑对象
            // 3. 找到被编辑对象的下标
            // 4. 保存成功后如果是更新则替换已有对象，创建则添加到最前面
            // 5. 提示保存成功，隐藏编辑对话框

            this.$refs.form.validate(valid => {
                if (!valid) { return; }

                // [2] 克隆被编辑对象
                // [3] 找到被编辑对象的下标
                this.saving    = true;
                const exam     = Utils.clone(this.examClone); // 重要: 克隆被编辑的对象
                exam.duration *= 60;
                const index    = this.exams.findIndex(e => e.id === exam.id); // 考试下标

                ExamDao.upsertExam(exam).then((newExam) => {
                    // [4] 保存成功后如果是更新则替换已有对象，创建则添加到最前面
                    //     有时服务器保存后会返回 user 对象，例如给 user 分配 ID 等以及修改其他属性，
                    //     这时应该添加服务器返回的对象到 users

                    if (index >= 0) {
                        // 更新: 替换已有对象
                        this.exams.splice(index, 1, newExam);
                    } else {
                        // 创建: 添加到最后面
                        this.exams.push(newExam);
                    }

                    // [5] 提示保存成功，隐藏编辑对话框
                    this.saving = false;
                    this.modal  = false;
                    this.$Message.success('保存成功');
                }).catch(() => {
                    this.saving = false;
                });
            });
        },
        // 删除考试
        deleteExam(exam) {
            // 1. 删除提示
            // 2. 从服务器删除成功后才从本地删除
            // 3. 提示删除成功

            this.$Modal.confirm({
                title: `确定删除 <font color="red">${exam.title}</font> 吗?`,
                loading: true,
                onOk: () => {
                    ExamDao.deleteExam(exam.id).then(() => {
                        const index = this.exams.findIndex(e => e.id === exam.id); // 考试下标
                        this.exams.splice(index, 1); // [2] 从服务器删除成功后才从本地删除
                        this.$Modal.remove();
                        this.$Message.success('删除成功');
                    });
                }
            });
        },
        // 新考试
        newExam() {
            return {
                id       : '0',
                title    : '',
                startTime: '',
                endTime  : '',
                duration : 90,
                maxTimes : 3,
                paperIds : [],
            };
        },
        // 创建考试的验证规则
        buildExamRules() {
            this.examRules = {
                title: [
                    { required: true, whitespace: true, message: '考试的名字不能为空', trigger: 'blur' }
                ],
                startTime: [
                    { required: true, message: '请选择开始时间', trigger: 'blur', validator: (rule, value, callback) => {
                        if (value) {
                            callback();
                        } else {
                            callback(new Error());
                        }
                    } }
                ],
                endTime: [
                    { required: true, trigger: 'blur', validator: (rule, value, callback) => {
                        // 结束时间的校验:
                        // 1. 必须选择结束时间
                        // 2. 必须选择开始时间
                        // 3. 结束时间不能早于开始时间
                        // 4. 考试时间区间必须大于考试持续时间
                        // 5. 满足上面条件才验证通过

                        // [1] 必须选择结束时间
                        if (!value) {
                            callback(new Error('请选择结束时间'));
                            return;
                        }

                        // [2] 必须选择开始时间
                        if (!this.examClone.startTime) {
                            callback(new Error('请选择开始时间'));
                            return;
                        }

                        // 开始时间和结束时间的时间差 (分钟)
                        const delta = (this.examClone.endTime.getTime() - this.examClone.startTime.getTime()) / 1000 / 60;

                        // [3] 结束时间不能早于开始时间
                        if (delta <= 0) {
                            callback(new Error('结束时间不能早于开始时间'));
                            return;
                        }

                        // [4] 考试时间区间必须大于考试持续时间
                        if (delta < this.examClone.duration) {
                            callback(new Error('考试时间区间必须大于考试持续时间'));
                            return;
                        }

                        // [5] 满足上面条件才验证通过
                        callback();
                    } }
                ],
                // 考试时长
                duration: [
                    { required: true, type: 'number', min: 1, message: '请输入考试时长', trigger: 'change' }
                ],
                // 最多考试次数
                maxTimes: [
                    { required: true, type: 'number', min: 1, message: '请输入最大次数', trigger: 'change' }
                ],
                // 试卷 IDs
                paperIds: [
                    { required: true, trigger: 'change', validator: (rule, value, callback) => {
                        if (this.examClone.paperIds.length > 0) {
                            callback();
                        } else {
                            callback(new Error('请选择试卷'));
                        }
                    } }
                ],
            };
        },
    }
};
</script>

<style lang="scss">
.exams {
    display: grid;
    grid-gap: 24px;
}
</style>
