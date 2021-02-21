<!-- eslint-disable vue/no-parsing-error -->

<!--
搜索维保订单、分页加载 (加载下一页的维保订单)
-->
<template>
    <div class="maintenance-orders list-page">
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
                <Input v-model="filterValue" transfer placeholder="请输入查询条件" search enter-button @on-search="searchOrders">
                    <Select v-model="filterKey" slot="prepend">
                        <Option value="email">邮件地址</Option>
                        <Option value="phone">电话号码</Option>
                    </Select>
                </Input>
            </div>

            <!-- 其他按钮 -->
            <Button type="primary" icon="md-add" @click="editOrder()">新建维保订单</Button>
        </div>

        <!-- 维保订单列表 -->
        <Table :data="orders" :columns="columns" :loading="reloading" border>
            <!-- 订单号 -->
            <template slot-scope="{ row: order }" slot="maintenanceOrderSn">
                <a @click="detailsOrder(order)">{{ order.maintenanceOrderSn }}</a>
            </template>

            <!-- 类型 -->
            <template slot-scope="{ row: order }" slot="type">
                {{ orderType(order) }}
            </template>

            <!-- 收货日期 -->
            <template slot-scope="{ row: order }" slot="receivedDate">
                {{ order.receivedDate | formatDateSimple }}
            </template>

            <!-- 状态 -->
            <template slot-scope="{ row: order }" slot="state">
                <Tag :color="stateColor(order.state)" type="border">{{ order.stateLabel }}</Tag>
            </template>

            <!-- 介绍信息 -->
            <template slot-scope="{ row: order }" slot="info">
                {{ order.userId }}
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: order }" slot="action">
                <Button type="primary" size="small" @click="editOrder(order)">编辑</Button>
                <Button type="error" size="small">删除</Button>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreOrders">更多...</Button>
        </div>

        <!-- 维保订单编辑弹窗 -->
        <MaintenanceOrderEdit v-model="editModal" :maintenace-order-id="maintenanceOrderId" @on-ok="orderSaved"/>

        <!-- 维保订单详情弹窗 -->
        <MaintenanceOrderDetails v-model="detailsModal" :maintenace-order-id="maintenanceOrderId" @on-ok="orderCompleted(maintenanceOrderId)"/>
    </div>
</template>

<script>
import MaintenanceOrderDao from '@/../public/static-p/js/dao/MaintenanceOrderDao';
import MaintenanceOrderEdit from '@/components/MaintenanceOrderEdit.vue';
import MaintenanceOrderDetails from '@/components/MaintenanceOrderDetails.vue';

export default {
    components: { MaintenanceOrderEdit, MaintenanceOrderDetails },
    data() {
        return {
            orders: [],
            filter: { // 搜索条件
                nickname  : '',
                pageSize  : 50,
                pageNumber: 1,
            },
            filterKey  : 'email',  // 搜索的 Key
            filterValue: '',       // 搜索的 Value
            dateRange  : ['', ''], // 搜索的时间范围
            more     : false, // 是否还有更多维保订单
            loading  : false, // 加载中
            reloading: false,
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { slot: 'maintenanceOrderSn', title: '订单号', width: 180 },
                { key : 'customerName', title: '客户', width: 150 },
                { key : 'salespersonName', title: '销售人员', width: 120 },
                { slot: 'type',   title: '类型', width: 110 },
                { key : 'productName', title: '产品名称', width: 150 },
                { key : 'productItemName', title: '物料名称', width: 150 },
                { slot: 'state', title: '状态', width: 110, align: 'center' },
                { key : 'problem',   title: '反馈的问题', minWidth: 400 },
                { key : 'servicePersonName', title: '售后服务人员', width: 130 },
                { slot: 'receivedDate', title: '收货日期', width: 130, align: 'center' },
                { slot: 'action', title: '操作', width: 150, align: 'center', className: 'table-action' },
            ],
            editModal: false, // 编辑弹窗是否可见
            detailsModal: false, // 维保订单详情弹窗是否可见
            maintenanceOrderId: '0', // 维保订单 ID
        };
    },
    mounted() {
        this.searchOrders();
    },
    methods: {
        // 搜索维保订单
        searchOrders() {
            this.orders            = [];
            this.more              = false;
            this.reloading         = true;
            this.filter.pageNumber = 1;

            // 如果不需要时间范围，则删除
            if (this.dateRange[0] && this.dateRange[1]) {
                this.filter.startAt = this.dateRange[0].format('yyyy-MM-dd');
                this.filter.endAt   = this.dateRange[1].format('yyyy-MM-dd');
            } else {
                this.filter.startAt = '';
                this.filter.endAt   = '';
            }

            this.fetchMoreOrders();
        },
        // 点击更多按钮加载下一页的维保订单
        fetchMoreOrders() {
            this.loading = true;

            MaintenanceOrderDao.findMaintenanceOrders(this.filter).then(orders => {
                this.orders.push(...orders);

                this.more      = orders.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
        // 订单类型
        orderType(order) {
            const ns = [];

            if (order.maintainable) {
                ns.push('维修');
            }
            if (order.repairable) {
                ns.push('保养');
            }

            return ns.join(', ');
        },
        // 编辑订单
        editOrder(order) {
            // 弹窗订单编辑窗口
            // order 存在则是更新，不存在这是新建
            if (order) {
                this.maintenanceOrderId = order.maintenanceOrderId;
            } else {
                this.maintenanceOrderId = '0';
            }

            this.editModal = true;
        },
        // 订单保存成功
        orderSaved(order) {
            // 查找订单，如果存在则替换，否则插入都第一行
            const index = this.orders.findIndex(o => o.maintenanceOrderId === order.maintenanceOrderId);

            if (index >= 0) {
                this.orders.replace(index, order);
            } else {
                this.orders.insert(0, order);
            }
        },
        // 完成订单
        orderCompleted(maintenanceOrderId) {
            const found = this.orders.find(o => o.maintenanceOrderId === maintenanceOrderId);

            if (found) {
                found.state = 4;
                found.stateLabel = '完成';
            }
        },
        // 显示订单详情
        detailsOrder(order) {
            this.maintenanceOrderId = order.maintenanceOrderId;
            this.detailsModal = true;
        }
    }
};
</script>

<style lang="scss">
/* 页面布局 */
.list-page {
    display: grid;
    grid-gap: 24px;

    .list-page-toolbar-top {
        display: grid;
        grid-template-columns: max-content max-content;
        justify-content: space-between;
        align-items: center;

        .filter {
            display: flex;

            input {
                width: 180px;
            }

            > div {
                margin-right: 10px;
            }

            /* 下拉选择过滤条件的输入框 */
            .ivu-input-group-prepend .ivu-select {
                width: auto;
            }
        }
    }

    .list-page-toolbar-bottom {
        display: grid;
        justify-content: center;
        align-items: center;
    }
}
</style>
