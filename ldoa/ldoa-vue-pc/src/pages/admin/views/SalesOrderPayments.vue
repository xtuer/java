<!-- eslint-disable vue/no-parsing-error -->

<!-- 订单收款情况 -->
<template>
    <div class="sales-order-payments list-page">
        <!-- 顶部工具栏 -->
        <div class="list-page-toolbar-top">
            <RadioGroup v-model="filter.searchType" type="button" button-style="solid" @on-change="searchSalesOrders">
                <Radio :label="0">所有订单</Radio>
                <Radio :label="1">应收款订单</Radio>
                <Radio :label="2">本月已收款</Radio>
                <Radio :label="3">本年已收款</Radio>
            </RadioGroup>

            <div class="filter">
                <!-- 时间范围 -->
                <DatePicker v-if="filter.searchType === 0 || filter.searchType === 1"
                            v-model="dateRange"
                            format="MM-dd"
                            separator=" 至 "
                            type="daterange"
                            data-prepend-label="签约时间"
                            class="prepend-label"
                            split-panels
                            placeholder="请选择签约时间范围">
                </DatePicker>

                <!-- 选择条件的搜索 -->
                <Input v-model="filterValue" transfer placeholder="请输入查询条件" search enter-button @on-search="searchSalesOrders">
                    <Select v-model="filterKey" slot="prepend">
                        <Option value="customerName">客户</Option>
                        <Option value="business">行业</Option>
                        <Option value="topic">主题</Option>
                    </Select>
                </Input>
            </div>
        </div>

        <!-- 销售订单列表 -->
        <Table :data="salesOrders" :columns="columns" :loading="reloading" border
            @on-column-width-resize="saveTableColumnWidths(arguments)"
        >
            <!-- 订单编号 -->
            <template slot-scope="{ row: salesOrder }" slot="salesOrderSn">
                <a @click="showSalesOrderDetails(salesOrder)">{{ salesOrder.salesOrderSn }}</a>
            </template>

            <!-- 签约日期 -->
            <template slot-scope="{ row: salesOrder }" slot="agreementDate">
                {{ salesOrder.agreementDate | formatDateSimple }}
            </template>

            <!-- 订单状态 -->
            <template slot-scope="{ row: salesOrder }" slot="state">
                <Tag :color="salesOrder.state | colorForValue(window.SALES_ORDER_TYPES)" type="border">{{ salesOrder.state | labelForValue(window.SALES_ORDER_TYPES) }}</Tag>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreSalesOrders">更多...</Button>
        </div>
    </div>
</template>

<script>
import SalesOrderDao from '@/../public/static-p/js/dao/SalesOrderDao';

export default {
    data() {
        return {
            salesOrders: [],
            filter     : this.newFilter(), // 搜索条件
            filterKey  : 'customerName',   // 搜索的 Key
            filterValue: '',       // 搜索的 Value
            dateRange  : ['', ''], // 搜索的时间范围
            more     : false, // 是否还有更多销售订单
            loading  : false, // 加载中
            reloading: false,

            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key: 'salesOrderSn', title: '订单编号', width: 150, resizable: true },
                { key : 'customerName', title: '客户', width: 150, resizable: true },
                { key : 'topic', title: '主题', width: 150, resizable: true },
                { key : 'business', title: '行业', width: 150, resizable: true },
                { key : 'ownerName', title: '负责人', width: 150, resizable: true },
                { slot: 'agreementDate', title: '签约日期', width: 110, align: 'center' },
                { key : 'costDealAmount', title: '净销售额', width: 120, resizable: false },
                { key : 'dealAmount', title: '总成交金额', width: 120, resizable: false },
                { key : 'shouldPayAmount', title: '应收金额', width: 120, resizable: false },
                { slot: 'state', title: '状态', width: 100, resizable: true, align: 'center' },
            ],
            tableName: 'sales-orders-payment-table',
        };
    },
    mounted() {
        this.restoreTableColumnWidths(this.columns);
        this.searchSalesOrders();
    },
    methods: {
        // 搜索销售订单
        searchSalesOrders() {
            this.salesOrders = [];
            this.more        = false;
            this.reloading   = true;
            this.filter      = { ...this.newFilter(), searchType: this.filter.searchType };
            this.filter[this.filterKey] = this.filterValue;

            // 如果不需要时间范围，则删除
            if (this.dateRange[0] && this.dateRange[1]) {
                this.filter.agreementStart = this.dateRange[0].format('yyyy-MM-dd');
                this.filter.agreementEnd   = this.dateRange[1].format('yyyy-MM-dd');
            } else {
                this.filter.agreementStart = '';
                this.filter.agreementEnd   = '';
            }

            this.fetchMoreSalesOrders();
        },
        // 点击更多按钮加载下一页的销售订单
        fetchMoreSalesOrders() {
            this.loading = true;

            SalesOrderDao.findSalesOrders(this.filter).then(salesOrders => {
                this.salesOrders.push(...salesOrders);

                this.more      = salesOrders.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
        // 新建搜索条件
        newFilter() {
            return { // 搜索条件
                searchType  : 0, // 搜索类型
                topic       : '',
                customerName: '',
                business    : '',
                pageSize    : 5,
                pageNumber  : 1,
            };
        },
    }
};
</script>

<style lang="scss">
</style>
