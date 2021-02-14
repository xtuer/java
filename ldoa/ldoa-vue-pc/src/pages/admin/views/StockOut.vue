<!-- eslint-disable vue/no-parsing-error -->

<!--
搜索出库申请、分页加载 (加载下一页的出库)

提示: 出库分为
* 物料直接出库: 虚拟出一个产品
* 订单物料出库: 获取订单中的产品
-->
<template>
    <div class="stock-out list-page">
        <!-- 顶部工具栏 -->
        <div class="list-page-toolbar-top">
            <!-- 搜索条件 -->
            <div class="filter">
                <!-- 状态 -->
                <Select v-model="filter.state" data-prepend-label="状态" class="prepend-label" style="width: 100%; min-width: 150px" @on-change="searchRequests">
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
                            data-prepend="创建时间"
                            data-prepend-label="创建时间"
                            class="prepend-label"
                            split-panels
                            placeholder="请选择创建时间范围">
                </DatePicker>

                <!-- 选择条件的搜索 -->
                <Input v-model="filter.stockRequestSn" placeholder="请输入出库单号" search enter-button @on-search="searchRequests">
                   <span slot="prepend">出库单号</span>
                </Input>
            </div>

            <!-- 其他按钮 -->
            <Dropdown @on-click="openSelect">
                <Button type="primary" icon="md-arrow-up">出库申请</Button>
                <DropdownMenu slot="list">
                    <DropdownItem name="item">物料出库</DropdownItem>
                    <DropdownItem name="order">订单出库</DropdownItem>
                </DropdownMenu>
            </Dropdown>
        </div>

        <!-- 出库申请列表 -->
        <Table :data="requests" :columns="columns" :loading="reloading" border>
            <!-- 出库单号 -->
            <template slot-scope="{ row: request }" slot="requestSn">
                <a @click="showStockRequest(request)">{{ request.stockRequestSn }}</a>
            </template>
            <!-- 出库类型 -->
            <template slot-scope="{ row: request }" slot="type">
                {{ request.orderId === '0' ? '物料' : '订单' }}
            </template>
            <!-- 申请时间 -->
            <template slot-scope="{ row: request }" slot="createdAt">
                {{ request.createdAt | formatDate }}
            </template>
            <!-- 状态 -->
            <template slot-scope="{ row: request }" slot="state">
                <Tag :color="stateColor(request.state)" type="border">{{ request.stateLabel }}</Tag>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreRequests">更多...</Button>
        </div>

        <!-- 物料出库弹窗 -->
        <StockOutModal v-model="stockOutVisible" :direct="stockOutDirect" @on-ok="stockOutRequestOk"/>

        <!-- 物料出库申请详情弹窗 -->
        <StockOutDetails v-model="stockRequestDetailsVisible" :stock-request-id="stockRequestId" @on-ok="stockOutComplete"/>
    </div>
</template>

<script>
import StockDao from '@/../public/static-p/js/dao/StockDao';
import StockOutModal from '@/components/StockOutModal.vue';
import StockOutDetails from '@/components/StockOutDetails.vue';

export default {
    components: { StockOutModal, StockOutDetails },
    data() {
        return {
            requests: [],
            filter: { // 搜索条件
                stockRequestSn: '',
                pageSize  : 20,
                pageNumber: 1,
                type      : 'OUT',
                state     : -1,
            },
            dateRange: ['', ''], // 搜索的时间范围
            more     : false, // 是否还有更多出库
            loading  : false, // 加载中
            reloading: false,
            stockOutVisible: false, // 出库弹窗是否可见
            stockOutDirect : false, // 出库类型: true (物料出库), false (订单出库)
            stockRequestId: '0',
            stockRequestDetailsVisible: false,

            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { slot: 'requestSn',         title: '出库单号', width: 200 },
                { key : 'desc',              title: '物料', minWidth: 300 },
                { slot: 'type',              title: '类型', width: 120, align: 'center' },
                { slot: 'state',             title: '状态', width: 120, align: 'center' },
                { key : 'applicantUsername', title: '申请人', width: 120 },
                { slot: 'createdAt',         title: '创建时间', width: 150, align: 'center' },
            ],
        };
    },
    mounted() {
        this.searchRequests();
    },
    methods: {
        // 搜索出库
        searchRequests() {
            this.requests          = [];
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

            this.fetchMoreRequests();
        },
        // 点击更多按钮加载下一页的出库
        fetchMoreRequests() {
            this.loading = true;

            StockDao.findStockRequests(this.filter).then(requests => {
                this.requests.push(...requests);

                this.more      = requests.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
        // 打开选择弹窗
        openSelect(type) {
            if (type === 'item') {
                this.stockOutDirect = true;
                this.stockOutVisible = true;
            } else if (type === 'order') {
                this.stockOutDirect = false;
                this.stockOutVisible = true;
            }
        },
        // 出库申请成功
        stockOutRequestOk(request) {
            this.requests.unshift(request);
        },
        // 出库领取物料完成
        stockOutComplete(stockRequestId) {
            const found = this.requests.find(r => r.stockRequestId === stockRequestId);

            if (found) {
                found.state = 4;
                found.stateLabel = '完成';
            }
        },
        // 显示出库申请详情
        showStockRequest(request) {
            this.stockRequestId = request.stockRequestId;
            this.stockRequestDetailsVisible = true;
        }
    }
};
</script>

<style lang="scss">
</style>
