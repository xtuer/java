<!--
功能: 审批项

参数:
audit-item: 审批项

示例:
<AuditItem :audit-item="item"/>
-->
<template>
    <div class="audit-item">
        <div class="color-gray">{{ auditItem.desc }}:</div>
        <div>{{ auditItem.comment }}</div>
        <div class="sign">
            {{ auditItem.auditorNickname }} / {{ auditItem.processedAt | formatDate }}
        </div>

        <!-- 审批工具栏 -->
        <div v-if="needAudit" class="toolbar">
            <Input v-model="auditComment" size="small" placeholder="请输入审批意见"/>
            <Button type="error" size="small" @click="reject">拒绝</Button>
            <Button type="primary" size="small" @click="accept">同意</Button>
        </div>

        <!-- 状态的图标 -->
        <Icon :type="statusIcon()" :style="statusStyle()" class="status-icon"/>
    </div>
</template>

<script>
import AuditDao from '@/../public/static-p/js/dao/AuditDao';

export default {
    props: {
        auditItem: { type: Object, required: true }, // 审批项
        auditable: { type: Boolean, default: false }, // 是否能进行审批
    },
    computed: {
        // 当前登陆用户
        me() {
            return this.$store.state.user;
        },
        // 需要审批返回 true，否则返回 false
        needAudit() {
            // 待审批且审批人是当前登陆用于
            if (this.auditItem.status === 1 && this.auditItem.auditorId === this.me.userId) {
                return true;
            } else {
                return false;
            }
        }
    },
    data() {
        return {
            auditComment: '', // 审批意见
        };
    },
    methods: {
        // 通过审批
        accept() {
            this.audit(true);
        },
        // 拒绝审批
        reject() {
            this.audit(false);
        },
        // 审批
        audit(accepted) {
            this.auditItem.comment = this.auditComment.trim();

            AuditDao.acceptAuditItem(this.auditItem.auditItemId, accepted, this.auditComment).then(() => {
                this.auditItem.status = accepted ? 3 : 2;
                this.$Message.success('审批完成');
            });
        },
        // 审批状态的图标
        statusIcon() {
            const icons = ['md-bicycle', 'md-bicycle', 'md-close', 'md-checkmark'];

            return icons[this.auditItem.status];
        },
        // 审批状态的样式
        statusStyle() {
            const colors = ['#e8eaec', '#808695', '#ed4014', '#19be6b'];

            return {
                color: colors[this.auditItem.status]
            };
        }
    }
};
</script>

<style lang="scss">
.audit-item {
    position: relative;

    .sign {
        text-align: right;
        margin-top: 40px;
    }

    .toolbar {
        display: flex;
        margin-top: 10px;

        button {
            margin-left: 10px;
        }
    }

    .status-icon {
        position: absolute;
        top: 1px;
        right: 1px;
        font-size: 40px;
    }
}
</style>
