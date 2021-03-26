<!--
功能: 维保订单项编辑

属性:
visible: 是否可见，可使用 v-model 双向绑定
order-item: 维保订单项

事件:
on-ok: 点击确定时触发，参数为选择的维保订单项
on-visible-change: 显示或隐藏时触发，显示时参数为 true，隐藏时为 false

案例:
<MaintenanceOrderItemEdit v-model="visible" :order-item="orderItem"/>
-->

<template>
    <Modal :value="visible" title="维保订单项编辑" :width="920" :mask-closable="false" class="maintenance-order-item-edit-modal"
        :styles="{ top: '60px', marginBottom: '40px' }"
        @on-visible-change="showEvent"
    >
        <!-- 弹窗 Body -->
        <Form ref="orderItemForm" :model="orderItem" :label-width="130">
            <div class="column-3">
                <FormItem label="产品名称:" prop="productName">
                    <Input v-model="orderItem.productName" clearable placeholder="请输入产品名称"/>
                </FormItem>
                <FormItem label="产品编码:" prop="productCode">
                    <Input v-model="orderItem.productCode" clearable placeholder="请输入产品编码"/>
                </FormItem>
                <FormItem label="规格型号:" prop="productModel">
                    <Input v-model="orderItem.productModel" clearable placeholder="请输入规格型号"/>
                </FormItem>
            </div>
            <div class="column-3">
                <FormItem label="维修前电量:" prop="electricQuantityBefore">
                    <InputNumber v-model="orderItem.electricQuantityBefore" clearable placeholder="请输入维修前电量"/>
                </FormItem>
                <FormItem label="维修前软件版本:" prop="softwareVersionBefore">
                    <Input v-model="orderItem.softwareVersionBefore" clearable placeholder="请输入维修前软件版本"/>
                </FormItem>
                <FormItem label="维修前硬件版本:" prop="hardwareVersionBefore">
                    <Input v-model="orderItem.hardwareVersionBefore" clearable placeholder="请输入维修前硬件版本"/>
                </FormItem>
            </div>
            <div class="column-3">
                <FormItem label="维修前功耗:" prop="powerDissipationBefore">
                    <InputNumber v-model="orderItem.powerDissipationBefore" clearable placeholder="请输入维修前功耗"/>
                </FormItem>
                <FormItem label="探头换前编号:" prop="probeDetectorCodeBefore">
                    <Input v-model="orderItem.probeDetectorCodeBefore" clearable placeholder="请输入探头换前编号"/>
                </FormItem>
                <FormItem label="维修前温度:" prop="temperatureBefore">
                    <InputNumber v-model="orderItem.temperatureBefore" clearable placeholder="请输入维修前温度"/>
                </FormItem>
            </div>
            <div class="column-3">
                <FormItem label="芯片编号:" prop="chipCode">
                    <Input v-model="orderItem.chipCode" clearable placeholder="请输入芯片编号"/>
                </FormItem>
            </div>
            <FormItem label="检测问题明细:" prop="checkDetails">
                <Input v-model="orderItem.checkDetails" clearable placeholder="请输入检测问题明细"/>
            </FormItem>
            <FormItem label="维修明细:" prop="maintenanceDetails">
                <Input v-model="orderItem.maintenanceDetails" clearable placeholder="请输入维修明细"/>
            </FormItem>
            <div class="column-3">
                <FormItem label="维修后电量:" prop="electricQuantityAfter">
                    <InputNumber v-model="orderItem.electricQuantityAfter" clearable placeholder="请输入维修后电量"/>
                </FormItem>
                <FormItem label="维修后软件版本:" prop="softwareVersionAfter">
                    <Input v-model="orderItem.softwareVersionAfter" clearable placeholder="请输入维修后软件版本"/>
                </FormItem>
                <FormItem label="维修后硬件版本:" prop="hardwareVersionAfter">
                    <Input v-model="orderItem.hardwareVersionAfter" clearable placeholder="请输入维修后硬件版本"/>
                </FormItem>
            </div>
            <div class="column-3">
                <FormItem label="维修后功耗:" prop="powerDissipationAfter">
                    <InputNumber v-model="orderItem.powerDissipationAfter" clearable placeholder="请输入维修后功耗"/>
                </FormItem>
                <FormItem label="探头换后编号:" prop="probeDetectorCodeAfter">
                    <Input v-model="orderItem.probeDetectorCodeAfter" clearable placeholder="请输入探头换后编号"/>
                </FormItem>
            </div>
        </Form>

        <!-- 底部工具栏 -->
        <div slot="footer">
            <Button type="text" @click="showEvent(false)">取消</Button>
            <Button type="primary" @click="ok">确定</Button>
        </div>
    </Modal>
</template>

<script>
export default {
    props: {
        visible  : { type: Boolean, required: true }, // 是否可见
        orderItem: { type: Object,  required: true }, // 维保订单项
    },
    model: {
        prop : 'visible',
        event: 'on-visible-change',
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
        // 点击确定按钮的回调函数
        ok() {
            this.$emit('on-ok', this.orderItem);
            this.showEvent(false); // 关闭弹窗
        },
        // 初始化
        init() {
            // 例如从服务器加载数据
        }
    }
};
</script>

<style lang="scss">
.maintenance-order-item-edit-modal {
    .ivu-input-number {
        width: 100%;
    }
}
</style>
