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
                <Input v-model="filter.stockRequestSn" placeholder="请输入出库单号" search enter-button @on-search="searchRequests">
                   <span slot="prepend">出库单号</span>
                </Input>
            </div>

            <!-- 其他按钮 -->
            <Dropdown @on-click="openSelect">
                <Button type="primary" icon="md-arrow-up">出库申请</Button>
                <DropdownMenu slot="list">
                    <DropdownItem name="item">选择物料</DropdownItem>
                    <DropdownItem name="order">选择订单</DropdownItem>
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
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreRequests">更多...</Button>
        </div>

        <!-- 物料选择弹窗 -->
        <ProductItemSelect v-model="itemSelectVisible" @on-ok="stockOutDirectProductItems"/>

        <!-- 订单选择弹窗 -->
        <OrderSelect v-model="orderSelectVisible" @on-ok="stockOutOrderProductItems"/>

        <!-- 物料出库弹窗 -->
        <StockOutModal v-model="stockOutVisible" :order-id="order.orderId" :order-sn="order.orderSn" :products="products" @on-ok="stockOut"/>

        <!-- 物料出库申请详情弹窗 -->
        <StockRequestDetails v-model="stockRequestDetailsVisible" :stock-request-id="stockRequestId"/>
    </div>
</template>

<script>
import StockDao from '@/../public/static-p/js/dao/StockDao';
import OrderDao from '@/../public/static-p/js/dao/OrderDao';
import ProductDao from '@/../public/static-p/js/dao/ProductDao';
import OrderSelect from '@/components/OrderSelect.vue';
import ProductItemSelect from '@/components/ProductItemSelect.vue';
import StockOutModal from '@/components/StockOutModal.vue';
import StockRequestDetails from '@/components/StockRequestDetails.vue';

export default {
    components: { OrderSelect, ProductItemSelect, StockOutModal, StockRequestDetails },
    data() {
        return {
            requests: [],
            filter: { // 搜索条件
                stockRequestSn: '',
                pageSize  : 20,
                pageNumber: 1,
                type      : 'OUT',
            },
            dateRange: ['', ''], // 搜索的时间范围
            more     : false, // 是否还有更多出库
            loading  : false, // 加载中
            reloading: false,
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { slot: 'requestSn',         title: '出库单号', width: 200 },
                { key : 'desc',              title: '物料', minWidth: 500 },
                { slot: 'type',              title: '类型', width: 110, align: 'center' },
                { key : 'stateLabel',        title: '状态', width: 110 },
                { key : 'applicantUsername', title: '申请人', width: 110 },
                { slot: 'createdAt',         title: '创建时间', width: 150, align: 'center' },
            ],
            itemSelectVisible : false, // 物料选择弹窗是否可见
            orderSelectVisible: false, // 订单选择弹窗石佛可见
            stockOutVisible   : false, // 出库弹窗是否可见
            order   : { orderId: '0', orderSn: 'XXXX' },   // 订单
            products: [{ productId: '0', items: [] }], // 订单的产品: 每个产品有多个 items => { items }
            stockRequestId: '0',
            stockRequestDetailsVisible: false,
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
                this.itemSelectVisible = true;
            } else if (type === 'order') {
                this.orderSelectVisible = true;
            }
        },
        // 物料直接出库
        stockOutDirectProductItems(productItem) {
            // 1. 构建虚拟订单和产品
            // 2. 显示出库弹窗

            // [1] 构建虚拟订单和产品
            this.order = { orderId: '0', orderSn: 'XXXXX' };
            this.products = [];
            this.products.push({
                productId: '0',
                items    : [productItem],
            });

            // [2] 显示出库弹窗
            this.stockOutVisible = true;
        },
        // 订单的物料出库
        stockOutOrderProductItems(order) {
            // 1. 查询订单
            // 2. 查询订单的产品
            // 3. 得到产品项
            // 4. 显示出库弹窗

            // [1] 查询订单
            OrderDao.findOrderById(order.orderId).then(retOrder => {
                // 产品的 ID
                const productIds = retOrder.items
                    .map(item => item.product)
                    .filter(product => product) // 去掉无效产品
                    .map(product => product.productId);

                // [2] 查询订单的产品
                ProductDao.findProducts({ productIds }).then(products => {
                    // [3] 得到产品项
                    this.order = retOrder;
                    this.products = products;

                    // [4] 显示出库弹窗
                    this.stockOutVisible = true;
                });
            });
        },
        // 出库申请成功
        stockOut(request) {
            console.log(request);
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
