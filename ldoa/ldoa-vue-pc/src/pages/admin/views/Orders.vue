<!-- eslint-disable vue/no-parsing-error -->
<!--
搜索订单、分页加载 (加载下一页的订单)
-->
<template>
    <div class="order-manage list-page">
        <!-- 顶部工具栏 -->
        <div class="list-page-toolbar-top">
            <div style="display: flex">
                <Input v-model="filter.orderSn" placeholder="请输入订单号">
                    <span slot="prepend">订单号</span>
                </Input>
                <Input v-model="filter.productCodes" placeholder="请输入产品编码">
                    <span slot="prepend">产品编码</span>
                </Input>
                <Button type="primary" icon="ios-search" @click="searchOrders">搜索</Button>
            </div>
            <Button type="primary" icon="md-add">添加订单</Button>
        </div>

        <!-- 订单列表 -->
        <Table :data="orders" :columns="columns" :loading="reloading" border>
            <!-- 介绍信息 -->
            <template slot-scope="{ row: order }" slot="salesperson">
                {{ order.salesperson && order.salesperson.nickname }}
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: order }" slot="action">
                <Button type="primary" size="small" style="margin-right: 5px">编辑</Button>
                <Button type="error" size="small">删除</Button>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreOrders">更多...</Button>
        </div>
    </div>
</template>

<script>
import OrderDao from '@/../public/static-p/js/dao/OrderDao';

export default {
    data() {
        return {
            orders : [],
            filter: { // 搜索条件
                orderSn     : '',
                productCodes: '',
                pageSize    : 2,
                pageNumber  : 1,
            },
            more     : false, // 是否还有更多订单
            loading  : false, // 加载中
            reloading: false,
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'orderSn',   title: '订单号', width: 180 },
                { slot: 'salesperson',   title: '销售负责人', minWidth: 500 },
                { key : 'statusLabel',   title: '状态', width: 150 },
                { key : 'productCodes',   title: '产品编码 / 数量', width: 150 },
                { slot: 'action', title: '操作', width: 150, align: 'center' },
            ]
        };
    },
    mounted() {
        this.searchOrders();
    },
    methods: {
        // 搜索订单
        searchOrders() {
            this.orders             = [];
            this.more              = false;
            this.reloading         = true;
            this.filter.pageNumber = 1;

            this.fetchMoreOrders();
        },
        // 点击更多按钮加载下一页的订单
        fetchMoreOrders() {
            this.loading = true;

            OrderDao.findOrders(this.filter).then(orders => {
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
.order-manage {
    .list-page-toolbar-top {
        .ivu-input-wrapper {
            width: 250px;
            margin-right: 10px;
        }
    }
}
</style>
