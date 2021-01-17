<!-- eslint-disable vue/no-parsing-error -->

<!--
搜索维保订单、分页加载 (加载下一页的维保订单)
-->
<template>
    <div class="orders list-page">
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
            <Button type="primary" icon="md-add" @click="editModal = true">添加维保订单</Button>
        </div>

        <!-- 维保订单列表 -->
        <Table :data="orders" :columns="columns" :loading="reloading" border>
            <!-- 介绍信息 -->
            <template slot-scope="{ row: order }" slot="info">
                {{ order.userId }}
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: order }" slot="action">
                <Button type="primary" size="small">编辑</Button>
                <Button type="error" size="small">删除</Button>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreOrders">更多...</Button>
        </div>

        <!-- 维保订单编辑弹窗 -->
        <MaintenanceOrderEdit v-model="editModal"/>
    </div>
</template>

<script>
import MaintenanceOrderDao from '@/../public/static-p/js/dao/MaintenanceOrderDao';
import MaintenanceOrderEdit from '@/components/MaintenanceOrderEdit.vue';

export default {
    components: { MaintenanceOrderEdit },
    data() {
        return {
            orders: [],
            filter: { // 搜索条件
                nickname  : '',
                pageSize  : 2,
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
                { key : 'nickname', title: '名字', width: 150 },
                { slot: 'info',   title: '介绍', minWidth: 500 },
                { slot: 'action', title: '操作', width: 150, align: 'center', className: 'table-action' },
            ],
            editModal: false, // 编辑弹窗是否可见
        };
    },
    mounted() {
        this.searchOrders();
    },
    methods: {
        // 搜索维保订单
        searchOrders() {
            this.orders             = [];
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
