<!--
功能: 物料出库弹窗

属性:
visible: 是否可见，可使用 v-model 双向绑定
order-id: 订单 ID
order-sn: 订单号
products: 产品数组

事件:
on-ok: 点击确定时触发，参数为无
on-visible-change: 显示或隐藏时触发，显示时参数为 true，隐藏时为 false

案例:
<StockOutModal v-model="visible" :order-id="orderId" :order-sn="orderSn" :products="products"/>
-->

<template>
    <Modal :value="visible" title="物料出库" transfer width="800" :mask-closable="false" class="stock-out-modal" @on-visible-change="showEvent">
        <!-- 弹窗 Body -->
        <div class="body-wrapper">
            <div v-show="!direct" class="order"><b>订单编号:</b> {{ orderSn }}</div>

            <!-- 产品列表 -->
            <div v-for="product in products" :key="product.productId" class="product">
                <!-- 产品信息 -->
                <div v-show="!direct" class="product-info">
                    <div><b>产品名称:</b> {{ product.name }}</div>
                    <div><b>产品编码:</b> {{ product.code }}</div>
                    <div><b>规格/型号:</b> {{ product.model }}</div>
                </div>

                <!-- 物料列表 -->
                <Table :data="product.items" :columns="productItemColumns" border>
                    <!-- 数量 -->
                    <template slot-scope="{ index }" slot="count">
                        <InputNumber
                            v-model="product.items[index].count"
                            :min="1"
                            :step="1"
                            :formatter="value => `${value} 个`"
                            :parser="value => value.replace(' 个', '')">
                        </InputNumber>
                    </template>
                </Table>
            </div>
        </div>

        <!-- 底部工具栏 -->
        <div slot="footer">
            <Button v-show="direct" icon="md-add" style="float: left" @click="itemSelectVisible = true">添加物料</Button>
            <Button type="text" @click="showEvent(false)">取消</Button>
            <Button type="primary" :loading="saving" @click="stockOutRequest">确定</Button>
        </div>

        <!-- 产品项选择弹窗 -->
        <ProductItemSelect v-model="itemSelectVisible" @on-ok="addProductItem"/>
    </Modal>
</template>

<script>
import StockDao from '@/../public/static-p/js/dao/StockDao';
import ProductItemSelect from '@/components/ProductItemSelect.vue';

export default {
    props: {
        visible : { type: Boolean, required: true }, // 是否可见
        orderId : { type: String,  required: true }, // 订单 ID
        orderSn : { type: String,  required: true }, // 订单号
        products: { type: Array,   required: true }, // 产品数组
    },
    model: {
        prop : 'visible',
        event: 'on-visible-change',
    },
    components: { ProductItemSelect },
    data() {
        return {
            productItemColumns: [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'name',     title: '物料名称' },
                { key : 'code',     title: '物料编码', width: 110 },
                { key : 'type',     title: '物料类型', width: 110 },
                { key : 'model',    title: '规格/型号', width: 110 },
                { key : 'standard', title: '标准/规范', width: 110 },
                { slot: 'count',    title: '数量', width: 110, align: 'center' },
            ],
            itemSelectVisible: false,
            saving: false,
        };
    },
    computed: {
        // 弹窗的标题
        title() {
            return `物料出库: 订单-${this.orderSn}`;
        },
        // 物料直接出库
        direct() {
            return !Utils.isValidId(this.orderId);
        }
    },
    methods: {
        // 显示隐藏事件
        showEvent(visible) {
            this.$emit('on-visible-change', visible);

            // 显示弹窗时 visible 为 true，初始化
            if (visible) {
                this.init();
            }
        },
        // 初始化
        init() {
            // 例如从服务器加载数据
        },
        // 添加物料
        addProductItem(item) {
            // 提示:
            // A. 只有在物料直接出库时才调用这个方法
            // B. 如果物料已经存在，不要重复添加
            const items = this.products[0].items;
            const found = items.some(i => i.productItemId === item.productItemId);

            if (!found) {
                items.push(item);
            } else {
                this.$Message.info('物料已经存在');
            }
        },
        // 出库申请
        stockOutRequest() {
            const stockOutInfo = {
                orderId: this.orderId,
                productItems: this.products.flatMap(product => product.items),
            };

            this.saving = true;
            StockDao.stockOutRequest(stockOutInfo).then(newRequest => {
                this.$emit('on-ok', newRequest);
                this.showEvent(false); // 关闭弹窗
                this.saving = false;
            }).catch(err => {
                this.saving = false;
            });
        }
    }
};
</script>

<style lang="scss">
.stock-out-modal {
    .product {
        .product-info {
            display: flex;
            margin: 20px 0 10px 0;

            > div {
                margin-right: 30px;
            }
        }
    }
}
</style>
