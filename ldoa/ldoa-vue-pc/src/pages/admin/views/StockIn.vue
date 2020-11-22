<!--
搜索入库、分页加载 (加载下一页的入库)
-->
<template>
    <div class="stock-in list-page">
        <!-- 顶部工具栏 -->
        <div class="list-page-toolbar-top">
            <Input search enter-button placeholder="请输入入库名" @on-search="searchStockRecords"/>
            <Button type="primary" icon="md-add">入库</Button>
        </div>

        <!-- 入库列表 -->
        <Table :data="stockRecords" :columns="columns" :loading="reloading" border>
            <!-- 介绍信息 -->
            <template slot-scope="{ row: StockRecord }" slot="info">
                ---
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: StockRecord }" slot="action">
                <Button type="primary" size="small" style="margin-right: 5px">编辑</Button>
                <Button type="error" size="small">删除</Button>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreStockRecords">更多...</Button>
        </div>
    </div>
</template>

<script>
import StockDao from '@/../public/static-p/js/dao/StockDao';

export default {
    data() {
        return {
            stockRecords : [],
            filter: { // 搜索条件
                name      : '',
                pageSize  : 2,
                pageNumber: 1,
            },
            more     : false, // 是否还有更多入库
            loading  : false, // 加载中
            reloading: false,
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'name',   title: '名字', width: 150 },
                { slot: 'info',   title: '介绍', minWidth: 500 },
                { slot: 'action', title: '操作', width: 150, align: 'center' },
            ]
        };
    },
    mounted() {
        this.searchStockRecords();
    },
    methods: {
        // 搜索入库
        searchStockRecords() {
            this.stockRecords             = [];
            this.more              = false;
            this.reloading         = true;
            this.filter.pageNumber = 1;

            this.fetchMoreStockRecords();
        },
        // 点击更多按钮加载下一页的入库
        fetchMoreStockRecords() {
            this.loading = true;

            UserDao.findStockRecords(this.filter).then(stockRecords => {
                this.stockRecords.push(...stockRecords);

                this.more      = stockRecords.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
    }
};
</script>

<style lang="scss">
.stock-in {
}
</style>
