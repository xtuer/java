<!-- eslint-disable vue/no-parsing-error -->

<!--
搜索销售订单、分页加载 (加载下一页的销售订单)
-->
<template>
    <div class="sales-orders list-page">
        <!-- 顶部工具栏 -->
        <div class="list-page-toolbar-top">
            <!-- 搜索条件 -->
            <div class="filter">
                <!-- 指定条件的搜索 -->
                <Input v-model="filter.nickname" placeholder="请输入查询条件">
                    <span slot="prepend">姓甚名谁</span>
                </Input>

                <!-- 时间范围 -->
                <DatePicker v-model="dateRange"
                            format="MM-dd"
                            separator=" 至 "
                            type="daterange"
                            data-prepend="创建时间"
                            class="date-picker"
                            split-panels
                            placeholder="请选择创建时间范围">
                </DatePicker>

                <!-- 选择条件的搜索 -->
                <Input v-model="filterValue" transfer placeholder="请输入查询条件" search enter-button @on-search="searchSalesOrders">
                    <Select v-model="filterKey" slot="prepend">
                        <Option value="email">邮件地址</Option>
                        <Option value="phone">电话号码</Option>
                    </Select>
                </Input>
            </div>

            <!-- 其他按钮 -->
            <Button type="primary" icon="md-add" @click="salesOrderEdit = true">创建销售订单</Button>
        </div>

        <!-- 销售订单列表 -->
        <Table :data="salesOrders" :columns="columns" :loading="reloading" border
            @on-column-width-resize="saveTableColumnWidths(arguments)"
        >
            <!-- 签约日期 -->
            <template slot-scope="{ row: salesOrder }" slot="agreementDate">
                {{ salesOrder.agreementDate | formatDateSimple }}
            </template>
            <!-- 交货日期 -->
            <template slot-scope="{ row: salesOrder }" slot="deliveryDate">
                {{ salesOrder.deliveryDate | formatDateSimple }}
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: salesOrder }" slot="action">
                <a>编辑</a>
                <span class="action-seperator"></span>
                <a class="delete">删除</a>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreSalesOrders">更多...</Button>
        </div>

        <!-- 销售订单编辑弹窗 -->
        <SalesOrderEdit v-model="salesOrderEdit" :sales-order-id="salesOrderId"/>
    </div>
</template>

<script>
import SalesOrderDao from '@/../public/static-p/js/dao/SalesOrderDao';
import SalesOrderEdit from '@/components/SalesOrderEdit.vue';


export default {
    components: { SalesOrderEdit },
    data() {
        return {
            salesOrders   : [],
            filter     : this.newFilter(), // 搜索条件
            filterKey  : 'email',  // 搜索的 Key
            filterValue: '',       // 搜索的 Value
            dateRange  : ['', ''], // 搜索的时间范围
            more     : false, // 是否还有更多销售订单
            loading  : false, // 加载中
            reloading: false,
            tableName: 'sales-orders-table',
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'salesOrderSn', title: '订单编号', width: 150, resizable: true },
                { key : 'topic', title: '主题', width: 150, resizable: true },
                { key : 'ownerName', title: '负责人', width: 150, resizable: true },
                { key : 'customerName', title: '客户', width: 150, resizable: true },
                { key : 'business', title: '行业', width: 150, resizable: true },
                { key : 'workUnit', title: '执行单位', width: 150, resizable: true },
                { key : 'customerContact', title: '联系人', width: 150, resizable: true },
                { slot: 'agreementDate', title: '签约日期', width: 110, align: 'center' },
                { slot: 'deliveryDate', title: '交货日期', width: 110, align: 'center' },
                { key : 'remark', title: '备注', minWidth: 250 },
                { slot: 'action', title: '操作', width: 110, align: 'center', className: 'table-action' },
            ],
            salesOrderId  : '0',
            salesOrderEdit: false,
        };
    },
    mounted() {
        this.restoreTableColumnWidths(this.columns);
        this.searchSalesOrders();
    },
    methods: {
        // 搜索销售订单
        searchSalesOrders() {
            this.salesOrders               = [];
            this.more                   = false;
            this.reloading              = true;
            this.filter                 = { ...this.newFilter(), name: this.filter.nickname };
            this.filter[this.filterKey] = this.filterValue;

            // 如果不需要时间范围，则删除
            if (this.dateRange[0] && this.dateRange[1]) {
                this.filter.startAt = this.dateRange[0].format('yyyy-MM-dd');
                this.filter.endAt   = this.dateRange[1].format('yyyy-MM-dd');
            } else {
                this.filter.startAt = '';
                this.filter.endAt   = '';
            }

            this.fetchMoreSalesOrders();
        },
        // 点击更多按钮加载下一页的销售订单
        fetchMoreSalesOrders() {
            this.loading = true;

            SalesOrderDao.findSalesOrders(this.filter).then(salesOrders => {
                console.log(salesOrders);
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
                // customerSn : '',
                // business: '',
                nickname  : '',
                pageSize  : 50,
                pageNumber: 1,
            };
        },
    }
};
</script>

<style lang="scss">
</style>
