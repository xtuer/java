<!--
功能: 选择审批员

属性:
auditor-id: 审批员 ID，可使用 v-model 双向绑定

事件:
on-changed: 选择时触发

案例:
<AuditorSelect v-model="auditorId" type="ORDER" :step="1"/>
-->

<template>
    <Select v-model="auditorId_" class="auditor-select prepend-label" data-prepend-label="审批员" @on-change="auditorChanged">
        <Option v-for="auditor in auditors" :key="auditor.userId" :value="auditor.userId">{{ auditor.nickname }}</Option>
    </Select>
</template>

<script>
import AuditDao from '@/../public/static-p/js/dao/AuditDao';

export default {
    props: {
        auditorId: { type: String, required: true }, // 审批员 ID
        type     : { type: String, required: true }, // 审批类型
        step     : { type: Number, required: true }, // 审批阶段
    },
    model: {
        prop : 'auditorId',
        event: 'on-changed',
    },
    data() {
        return {
            auditorId_: this.auditorId, // 选中的审批员 ID
            auditors  : [], // 所有审批员
        };
    },
    mounted() {
        // 查询 step 阶段的审批员
        AuditDao.findAuditors(this.type, this.step).then(auditors => {
            this.auditors = auditors;
        });
    },
    methods: {
        auditorChanged() {
            if (this.auditorId_) {
                this.$emit('on-changed', this.auditorId_);
            }
        }
    },
    watch: {
        // 调用者改变了 auditorId 的值之后，同步到 auditorId_
        auditorId() {
            this.auditorId_ = this.auditorId;
        }
    }
};
</script>

<style lang="scss">

</style>
