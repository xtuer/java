<!--
功能: 订单详情弹窗

属性:
visible: 是否可见，可使用 v-model 双向绑定
order-id: 订单 ID

事件:
on-ok: 点击确定时触发，参数为无
on-visible-change: 显示或隐藏时触发，显示时参数为 true，隐藏时为 false

案例:
<OrderDetails v-model="visible" :order-id="orderId"/>
-->

<template>
    <Modal :value="visible" title="订单详情" :mask-closable="false" class="order-details-modal"
            @on-visible-change="showEvent">
        <!-- 内容显示 -->
        {{ order }}

        <!-- 底部工具栏 -->
        <div slot="footer">
            <Button type="text" @click="showEvent(false)">取消</Button>
            <Button type="primary" @click="showEvent(false)">确定</Button>
        </div>
    </Modal>
</template>

<script>
import OrderDao from '@/../public/static-p/js/dao/OrderDao';

export default {
    props: {
        visible: { type: Boolean, required: true }, // 是否可见
        orderId: { type: String,  required: true }, // 订单 ID
    },
    model: {
        prop : 'visible',
        event: 'on-visible-change',
    },
    data() {
        return {
            order: {},
        };
    },
    methods: {
        // 显示隐藏事件
        showEvent(visible) {
            this.$emit('on-visible-change', visible);

            // 显示时加载订单
            if (visible) {
                OrderDao.findOrderById(this.orderId).then(order => {
                    this.order = order;
                });
            }
        },
    }
};
</script>

<style lang="scss">

</style>
