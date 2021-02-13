<!--
功能: 物料出库的库存选择

属性:
visible: 是否可见，可使用 v-model 双向绑定
product-item-id: 物料 ID
max-count: 出库的最大数量

事件:
on-ok: 点击确定时触发，参数为选择的物料库存数组，数组元素的对象定义为 [{ productItemId, batch, count }]
on-visible-change: 显示或隐藏时触发，显示时参数为 true，隐藏时为 false

案例:
<StockSelect v-model="visible" :product-item-id="pid"/>
-->

<template>
    <Modal :value="visible" :title="title" :width="600" class="stock-select-modal" @on-visible-change="showEvent">
        <!-- 弹窗 Body -->
        <!-- 库存列表，选择出库数量 -->
        <Table :data="stocks" :columns="columns" border>
            <!-- 库存数量 -->
            <template slot-scope="{ row: stock }" slot="count">
                <div v-if="!stock.batch">未入库</div>
                <div v-else>{{ stock.count }} {{ stock.unit || '' }}</div>
            </template>

            <!-- 选择出库数量 -->
            <template slot-scope="{ index }" slot="action">
                <InputNumber v-model="stocks[index].outCount" :disabled="!stocks[index].batch" :min="0" style="width: 100%" @on-change="ensureInt(index, $event)"/>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div slot="footer">
            <Button type="text" @click="showEvent(false)">取消</Button>
            <Button type="primary" @click="ok">确定</Button>
        </div>
    </Modal>
</template>

<script>
import StockDao from '@/../public/static-p/js/dao/StockDao';

export default {
    props: {
        visible      : { type: Boolean, required: true }, // 是否可见
        productItemId: { type: String,  required: true }, // 物料 ID
        maxCount     : { type: Number,  required: true }, // 最大数量
    },
    model: {
        prop : 'visible',
        event: 'on-visible-change',
    },
    data() {
        return {
            stocks: [], // 物料库存
            loading: false,
            columns: [
                { key : 'name', title: '物料名称' },
                { key : 'batch', title: '批次' },
                { slot: 'count', title: '库存', width: 110, align: 'right' },
                { slot: 'action', title: '出库数量', width: 110 },
            ],
        };
    },
    computed: {
        title() {
            if (this.maxCount === 0) {
                return '库存选择';
            } else {
                return `库存选择: 出库的总数量为 ${this.maxCount}`;
            }
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
            // 从服务器加载库存
            this.loading = true;
            StockDao.findStocks({ productItemId: this.productItemId, pageSize: 10000 }).then(stocks => {
                stocks.forEach(s => { s.outCount = 0; }); // 增加 outCount 属性，双向绑定使用

                this.stocks = stocks;
                this.loading = false;
            }).catch(() => {
                this.loading = false;
            });
        },
        // 确保是整数
        ensureInt(index, count) {
            this.$nextTick(() => {
                this.stocks[index].outCount = parseInt(count) || 0;
            });
        },
        // 点击确定按钮的回调函数
        ok() {
            // 1. 选择 outCount 不为 0 的项，计算总数量
            // 2. 没有选择出库的批次，提示
            // 3. 如果 maxCount 为 0，或者 maxCount 与选中的出库数量相等，则出库
            // 4. maxCount 不等于 total，询问是否出库


            // [1] 选择 outCount 不为 0 的项，计算总数量
            let total  = 0;
            const outs = this.stocks.filter(s => s.outCount > 0).map(s => {
                total += s.outCount;
                return { productItemId: s.productItemId, batch: s.batch, count: s.outCount };
            });

            // [2] 没有选择出库的批次，提示
            if (outs.length === 0) {
                this.$Message.error('请选择出库的批次和出库数量');
                return;
            }

            if (this.maxCount === 0 || this.maxCount === total) {
                // [3] 如果 maxCount 为 0，或者 maxCount 与选中的出库数量相等，则出库
                this.stockOut(outs);
            } else {
                // [4] maxCount 不等于 total，询问是否出库
                this.$Modal.confirm({
                    title: `需要出库数量为 ${this.maxCount}，选择的出库数量为 ${total}, 确定出库吗?`,
                    onOk: () => {
                        this.stockOut(outs);
                    }
                });
            }
        },
        // 确定出库
        stockOut(batchCounts) {
            this.$emit('on-ok', batchCounts);
            this.showEvent(false); // 关闭弹窗
        }
    }
};
</script>

<style lang="scss">
.stock-select-modal {

}
</style>
