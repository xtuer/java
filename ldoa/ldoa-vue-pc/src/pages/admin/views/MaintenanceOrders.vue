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
                <!-- 状态 -->
                <Select v-model="filter.state" data-prepend-label="状态" class="prepend-label" style="width: 100%; min-width: 150px" @on-change="searchOrders">
                    <Option :value="-1">全部</Option>
                    <Option :value="1">审批中</Option>
                    <Option :value="2">审批拒绝</Option>
                    <Option :value="3">审批通过</Option>
                    <Option :value="4">完成</Option>
                </Select>

                <!-- 时间范围 -->
                <DatePicker v-model="dateRange"
                            format="MM-dd"
                            separator=" 至 "
                            type="daterange"
                            data-prepend-label="收货时间"
                            class="prepend-label"
                            split-panels
                            placeholder="请选择创建时间范围">
                </DatePicker>

                <!-- 选择条件的搜索 -->
                <Input v-model="filterValue" transfer placeholder="请输入查询条件" search enter-button @on-search="searchOrders">
                    <Select v-model="filterKey" slot="prepend">
                        <Option value="maintenanceOrderSn">维保单号</Option>
                        <Option value="salespersonName">销售人员</Option>
                        <Option value="servicePersonName">售后人员</Option>
                        <Option value="productName">产品名称</Option>
                        <Option value="productCode">产品编码</Option>
                        <Option value="customerName">客户</Option>
                    </Select>
                </Input>
            </div>

            <!-- 其他按钮 -->
            <Button type="primary" icon="md-add" @click="editOrder()">新建维保订单</Button>
        </div>

        <!-- 维保订单列表 -->
        <Table :data="orders" :columns="columns" :loading="reloading" border
            @on-column-width-resize="saveTableColumnWidths(tableName, currentUserId(), ...arguments)"
        >
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

            <!-- 进度 -->
            <template slot-scope="{ row: order }" slot="progress">
                <div v-show="progressEditedOrder !== order" class="progress-content">
                    {{ order.progress }}
                    <Icon type="md-create" class="clickable" @click="progressEditedOrder = order"/>
                </div>
                <Input v-show="progressEditedOrder === order" v-model="order.progress"
                        @on-enter="saveProgress(order)"
                        @on-keyup="keyupForProgress(order, $event)"
                        @on-blur="cancelEditProgress(order)"/>
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: order }" slot="action">
                <Button :disabled="!canEditOrder(order)" type="primary" size="small" @click="editOrder(order)">编辑</Button>
                <Button :disabled="!canEditOrder(order)" type="error" size="small" @click="deleteOrder(order)">删除</Button>
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
            filter: this.newFilter(),
            filterKey  : 'maintenanceOrderSn', // 搜索的 Key
            filterValue: '',       // 搜索的 Value
            dateRange  : ['', ''], // 搜索的时间范围
            more     : false, // 是否还有更多维保订单
            loading  : false, // 加载中
            reloading: false,
            tableName: 'maintenance-orders-table', // 表名
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { slot: 'maintenanceOrderSn', title: '维保单号', width: 180, resizable: true },
                { key : 'customerName', title: '客户', width: 150, resizable: true },
                { slot: 'type',   title: '类型', width: 110, resizable: true },
                { key : 'productName', title: '产品名称', width: 150, resizable: true },
                { key : 'productCode', title: '产品编码', width: 110, resizable: true },
                { key : 'productModel', title: '型号/规格', width: 110, resizable: true },
                { key : 'productCount', title: '产品数量', width: 110, resizable: true },
                { slot: 'state', title: '状态', width: 110, align: 'center', resizable: true },
                { key : 'problem',   title: '反馈的问题', minWidth: 400 },
                { slot: 'progress',   title: '处理进度', width: 200, className: 'order-progress', resizable: true },
                { key : 'servicePersonName', title: '售后服务人员', width: 130, resizable: true },
                { key : 'salespersonName', title: '销售人员', width: 120, resizable: true },
                { slot: 'receivedDate', title: '收货日期', width: 130, align: 'center', resizable: true },
                { slot: 'action', title: '操作', width: 150, align: 'center', className: 'table-action', resizable: true },
            ],
            editModal: false, // 编辑弹窗是否可见
            detailsModal: false, // 维保订单详情弹窗是否可见
            maintenanceOrderId: '0', // 维保订单 ID
            progressEditedOrder: {}, // 选中编辑进度的维保订单
        };
    },
    mounted() {
        this.restoreTableColumnWidths(this.tableName, this.currentUserId(), this.columns);
        this.searchOrders();
    },
    methods: {
        // 搜索维保订单
        searchOrders() {
            this.orders                 = [];
            this.more                   = false;
            this.reloading              = true;
            this.filter                 = { ...this.newFilter(), state: this.filter.state };
            this.filter[this.filterKey] = this.filterValue;

            // 如果不需要时间范围，则删除
            if (this.dateRange[0] && this.dateRange[1]) {
                this.filter.receivedStartAt = this.dateRange[0].format('yyyy-MM-dd');
                this.filter.receivedEndAt   = this.dateRange[1].format('yyyy-MM-dd');
            } else {
                this.filter.receivedStartAt = '';
                this.filter.receivedEndAt   = '';
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
        },
        // 判断订单是否可以编辑: 售后服务人员为当前用户，且审批拒绝的订单才能编辑
        canEditOrder(order) {
            if (this.isCurrentUser(order.servicePersonId) && order.state === 2) {
                return true;
            } else {
                return false;
            }
        },
        // 取消编辑进度
        cancelEditProgress(order) {
            const found = this.orders.find(o => o.maintenanceOrderId === order.maintenanceOrderId);

            if (found) {
                order.progress = found.progress;
            }

            this.progressEditedOrder = {};
        },
        // 保存进度
        saveProgress(order) {
            // 1. 在 orders 中查找表格中行的 order 对应的原始订单对象 found
            // 2. 保存进度到服务器
            // 3. 更新成功后更新 found 的进度
            // 4. 重置选中的订单 progressEditedOrder，隐藏进度输入框

            // [1] 在 orders 中查找表格中行的 order 对应的原始订单对象 found
            const found = this.orders.find(o => o.maintenanceOrderId === order.maintenanceOrderId);

            if (!found) {
                return;
            }

            // [2] 保存进度到服务器
            MaintenanceOrderDao.updateProgress(order.maintenanceOrderId, order.progress).then(() => {
                // [3] 更新成功后更新 found 的进度
                found.progress = order.progress;

                // [4] 重置选中的订单 progressEditedOrder，隐藏进度输入框
                this.progressEditedOrder = {};

                this.$Message.success('进度保存成功');
            });
        },
        // 进度的输入框键盘事件
        keyupForProgress(order, event) {
            if (event.keyCode === 27) {
                this.cancelEditProgress(order);
            }
        },
        // 删除维保订单
        deleteOrder(order) {
            // 1. 删除提示
            // 2. 从服务器删除成功后才从本地删除
            // 3. 提示删除成功

            this.$Modal.confirm({
                title: `确定删除维保订单 <font color="red">${order.maintenanceOrderSn}</font> 吗?`,
                loading: true,
                onOk: () => {
                    MaintenanceOrderDao.deleteMaintenanceOrder(order.maintenanceOrderId).then(() => {
                        const index = this.orders.findIndex(o => o.maintenanceOrderId === order.maintenanceOrderId); // 用户下标
                        this.orders.splice(index, 1); // [2] 从服务器删除成功后才从本地删除
                        this.$Modal.remove();
                        this.$Message.success('删除成功');
                    });
                }
            });
        },
        // 新建搜索条件
        newFilter() {
            return { // 搜索条件
                // maintenanceOrderSn: '',
                // salespersonName   : '',
                // servicePersonName : '',
                // productName : '',
                // productCode : '',
                // customerName: '',
                state          : -1,
                receivedStartAt: '',
                receivedEndAt  : '',
                pageSize       : 50,
                pageNumber     : 1,
            };
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

    .order-progress .progress-content {
        .ivu-icon {
            display: none;
        }
    }
    .order-progress:hover .progress-content {
        .ivu-icon {
            display: inline-block;
        }
    }
}
</style>
