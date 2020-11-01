<!--
功能: 审批项

参数:
audit-item: 审批项

示例:
<AuditItem :audit-item="item"/>
-->
<template>
    <div class="audit-item">
        <div>{{ auditItem.desc }}:</div>
        <div>{{ auditItem.comment }}</div>
        <div class="sign">{{ auditItem.auditorNickname }} / {{ auditItem.processedAt | formatDate }}</div>
    </div>
</template>

<script>
export default {
    props: {
        auditItem: { type: Object, required: true }, // 审批项
    },
    data() {
        return {};
    },
    methods: {}
};
</script>

<style lang="scss">
.audit-item {
    .sign {
        text-align: right;
        margin-top: 40px;
    }
}
</style>
