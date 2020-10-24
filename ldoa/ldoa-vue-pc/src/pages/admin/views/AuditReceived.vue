<!--
搜索审批项、分页加载 (加载下一页的审批项)
-->
<template>
    <div class="audit-received list-page">
        <!-- 顶部工具栏 -->
        <div class="list-page-toolbar-top">
            <RadioGroup v-model="status" type="button" @on-change="searchAuditItems">
                <Radio v-for="s in window.AUDIT_ITEM_STATUS" :label="s.value">{{ s.label }}</Radio>
            </RadioGroup>
        </div>

        <!-- 审批项列表 -->
        <Table :data="auditItems" :columns="columns" :loading="reloading" border>
            <!-- 类型 -->
            <template slot-scope="{ row: item }" slot="type">
                {{ item.type | auditTypeName }}
            </template>

            <!-- 申请时间 -->
            <template slot-scope="{ row: item }" slot="createdAt">
                {{ item.createdAt | formatDate('YYYY-MM-DD HH:mm') }}
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: auditItem }" slot="action">
                <Button type="primary" size="small">审批</Button>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreAuditItems">更多...</Button>
        </div>
    </div>
</template>

<script>
import AuditDao from '@/../public/static-p/js/dao/AuditDao';

export default {
    data() {
        return {
            auditItems : [],
            status: 1,
            filter: { // 搜索条件
                name      : '',
                pageSize  : 2,
                pageNumber: 1,
            },
            more     : false, // 是否还有更多审批项
            loading  : false, // 加载中
            reloading: false,
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'applicantNickname', title: '申请人', width: 150 },
                { slot: 'type',   title: '类型', width: 150 },
                { slot: 'createdAt', title: '申请时间', width: 150, align: 'center' },
                { key : 'statusLabel', title: '状态', width: 150 , align: 'center' },
                { slot: 'action', title: '操作', width: 150, align: 'center', className: 'table-action' },
            ]
        };
    },
    mounted() {
        this.searchAuditItems();
    },
    computed: {
        // 当前审批员 ID
        currentAuditorId() {
            return this.$store.state.user.userId;
        }
    },
    methods: {
        // 搜索审批项
        searchAuditItems() {
            this.auditItems        = [];
            this.more              = false;
            this.reloading         = true;
            this.filter.pageNumber = 1;

            this.fetchMoreAuditItems();
        },
        // 点击更多按钮加载下一页的审批项
        fetchMoreAuditItems() {
            this.loading = true;

            AuditDao.findAuditItemsByAuditorIdAndStatus(this.currentAuditorId, this.status).then(auditItems => {
                this.auditItems.push(...auditItems);

                this.more      = false; // auditItems.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
    }
};
</script>

<style lang="scss">
.audit-received {
}
</style>
