<template>
    <div class="warehousing-logs">
        <Table :data="logs" :columns="columns" border>
            <template slot-scope="{ row: log }" slot="type">
                <!-- 入库绿色，出库红色 -->
                <Tag :color="log.quantity > 0 ? 'success' : 'error'">{{ log.type }}</Tag>
            </template>

            <template slot-scope="{ row: log }" slot="quantity">
                <!-- 入库绿色，出库红色 -->
                <Badge :text="Math.abs(log.quantity) + ''" :type="log.quantity > 0 ? 'success' : 'error'"/>
            </template>

            <template slot-scope="{ row: log }" slot="date">
                {{ log.date | formatDate }}
            </template>
        </Table>

        <!-- 加载下一页按钮 -->
        <center>
            <Button v-show="more" :loading="loading" icon="md-boat" shape="circle" @click="fetchMoreLogs">更多...</Button>
        </center>
    </div>
</template>

<script>
import SpareDao from '@/../public/static-p/js/dao/SpareDao';

export default {
    data() {
        return {
            logs: [], // 库存日志
            columns: [
                { title: '操作类型', slot: 'type', align: 'center', width: 100 },
                { title: '操作员', key: 'username', width: 140 },
                { title: '备件入库单号', key: 'spareSn', minWidth: 200 },
                { title: '操作前芯片数量', key: 'oldChipQuantity', width: 140 },
                { title: '操作后芯片数量', key: 'newChipQuantity', width: 140 },
                { title: '数量', slot: 'quantity', width: 140 },
                { title: '时间', slot: 'date', align: 'center', width: 130 },
            ],
            filter: { // 搜索条件
                pageSize  : 20,
                pageNumber: 1,
            },
            more   : false,
            loading: false,
        };
    },
    mounted() {
        this.fetchMoreLogs();
    },
    methods: {
        // 搜索日志
        searchLogs() {
            this.logs = [];
            this.filter.pageNumber = 1;
            this.more = false;
            this.fetchMoreLogs();
        },
        // 点击更多按钮加载下一页的日志
        fetchMoreLogs() {
            this.loading = true;

            SpareDao.findSpareWarehousingLogs(this.filter).then(logs => {
                this.logs.push(...logs);

                this.filter.pageNumber++;
                this.more = logs.length >= this.filter.pageSize;
                this.loading = false;
            });
        },
    }
};
</script>

<style lang="scss">
.warehousing-logs {
    display: grid;
    grid-gap: 12px;
}
</style>
