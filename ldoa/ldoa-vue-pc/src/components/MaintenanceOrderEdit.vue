<!--
功能: 编辑维保订单

属性:
visible: 是否可见，可使用 v-model 双向绑定
maintenace-order-id: 维保订单 ID

事件:
on-ok: 点击确定时触发，参数为维保订单
on-visible-change: 显示或隐藏时触发，显示时参数为 true，隐藏时为 false

案例:
<MaintenaceOrderEdit v-model="visible" :maintenace-order-id="maintenaceOrderId"/>
-->

<template>
    <Modal :value="visible" title="编辑维保订单" :mask-closable="false" :width="900" class="maintenance-order-edit-modal"
            :styles="{ top: '40px', marginBottom: '40px' }"
            @on-visible-change="showEvent">
        <!-- 弹窗 Body -->
        <Form ref="orderForm" :model="order" :rules="rules" :label-width="100" class="relative">
            <Spin v-if="loading" fix size="large"></Spin>

            <div class="column-3">
                <FormItem label="客户:" prop="customerName">
                    <Input v-model="order.customerName" clearable placeholder="请输入客户"/>
                </FormItem>
                <FormItem label="销售人员:" prop="salespersonName">
                    <Input v-model="order.salespersonName" clearable placeholder="请输入销售人员"/>
                </FormItem>
                <FormItem label="类型:">
                    <Checkbox v-model="order.maintainable">维修</Checkbox>
                    <Checkbox v-model="order.repairable">保养</Checkbox>
                </FormItem>
            </div>
            <div class="column-3">
                <FormItem label="产品名称:" prop="customerCompany">
                    <Input v-model="order.productName" search placeholder="请输入产品名称" @on-search="showProductSelect"/>
                </FormItem>
                <FormItem label="产品编码:" prop="customerCompany">
                    <Input v-model="order.productCode" clearable placeholder="请输入产品编码"/>
                </FormItem>
                <FormItem label="规格/型号:" prop="customerCompany">
                    <Input v-model="order.productModel" clearable placeholder="请输入规格/型号"/>
                </FormItem>
            </div>
            <!-- <div class="column-3">
                <FormItem label="物料名称:" prop="customerCompany">
                    <Input v-model="order.productItemName" clearable placeholder="请输入物料名称"/>
                </FormItem>
                <FormItem label="物料批次:" prop="customerCompany">
                    <Input v-model="order.productItemBatch" clearable placeholder="请输入物料批次"/>
                </FormItem>
                <FormItem label="物料数量:" prop="customerCompany">
                    <InputNumber v-model="order.productItemCount" :min="0" placeholder="物料数量" style="width: 100%" @on-change="ensureInt(index, $event)"/>
                </FormItem>
            </div> -->
            <div class="column-3">
                <FormItem label="产品数量:" prop="customerCompany">
                    <InputNumber v-model="order.productCount" :min="0" placeholder="产品数量" style="width: 100%" @on-change="ensureInt(index, $event)"/>
                </FormItem>
                <FormItem label="配件:" prop="customerCompany">
                    <Input v-model="order.accessories" clearable placeholder="请输入配件信息"/>
                </FormItem>
            </div>
            <div class="column-3">
                <FormItem label="收货日期:" prop="receivedDate">
                    <DatePicker v-model="order.receivedDate" type="date" placeholder="请选择收货日期"></DatePicker>
                </FormItem>
                <FormItem label="订单号:" prop="customerCompany">
                    <Input v-model="order.orderSn" clearable placeholder="请输入订单号"/>
                </FormItem>
                <FormItem label="需要证书:">
                    <RadioGroup v-model="order.needCertificate">
                        <Radio :label="1">是</Radio>
                        <Radio :label="0">否</Radio>
                    </RadioGroup>
                </FormItem>
            </div>
            <FormItem label="反馈的问题:" prop="problem">
                <Input v-model="order.problem" type="textarea" :rows="5" placeholder="请输入客户反馈的问题"/>
            </FormItem>
            <FormItem label="处理进度:" prop="progress">
                <Input v-model="order.progress" placeholder="请输入进度情况"/>
            </FormItem>
        </Form>

        <!-- 底部工具栏 -->
        <div slot="footer" class="footer">
            <AuditorSelect v-model="order.currentAuditorId" :step="1" type="MAINTENANCE_ORDER"/>
            <span class="stretch"></span>
            <Button type="text" @click="showEvent(false)">取消</Button>
            <Button type="primary" :loading="saving" @click="save">确定</Button>
        </div>

        <!-- 产品选择弹窗 -->
        <ProductSelect v-model="productSelectVisible" @on-ok="onProductSelected"/>
    </Modal>
</template>

<script>
import MaintenanceOrderDao from '@/../public/static-p/js/dao/MaintenanceOrderDao';
import ProductSelect from '@/components/ProductSelect.vue';
import AuditorSelect from '@/components/AuditorSelect.vue';

export default {
    props: {
        visible: { type: Boolean, required: true }, // 是否可见
        maintenaceOrderId: { type: String,  required: true }, // 维保订单 ID
    },
    model: {
        prop : 'visible',
        event: 'on-visible-change',
    },
    components: { ProductSelect, AuditorSelect },
    data() {
        return {
            order: this.newOrder(),
            productSelectVisible: false, // 产品选择弹窗是否可见
            saving: false, // 是否保存中
            loading: false,

            // 校验规则
            rules: {
                // 客户名称
                customerName: [
                    { required: true, whitespace: true, message: '客户名称不能为空', trigger: 'blur' }
                ],
                // 销售人员
                salespersonName: [
                    { required: true, whitespace: true, message: '销售人员不能为空', trigger: 'blur' }
                ],
                // 反馈的问题
                problem: [
                    { required: true, whitespace: true, message: '反馈的问题不能为空', trigger: 'blur' }
                ],
                // 时间验证
                receivedDate: [
                    { required: true, type: 'date', message: '请选择收货时间', trigger: 'blur' }
                ],
            },
        };
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
            // 例如 ID 有效则从服务器加载，否则创建一个新的
            if (Utils.isValidId(this.maintenaceOrderId)) {
                this.loading = true;
                MaintenanceOrderDao.findMaintenanceOrderById(this.maintenaceOrderId).then(order => {
                    this.order = order;
                    this.loading = false;
                });
            } else {
                this.order = this.newOrder();
            }
        },
        // 显示产品选择弹窗
        showProductSelect() {
            this.productSelectVisible = true;
        },
        // 选择产品的回调函数
        onProductSelected(product) {
            this.order.productName  = product.name;
            this.order.productCode  = product.code;
            this.order.productModel = product.model;
        },
        // 确保是整数
        ensureInt(index, count) {
            this.$nextTick(() => {
                this.order.productItemCount = parseInt(count) || 0;
            });
        },
        // 保存维保订单
        save() {
            // 审批员不能为空
            if (!Utils.isValidId(this.order.currentAuditorId)) {
                this.$Message.error('请选择审批员');
                return;
            }

            // 表单验证
            this.$refs.orderForm.validate(valid => {
                if (!valid) { return; }

                // 提交表单
                this.saving = true;
                const order = JSON.parse(JSON.stringify(this.order)); // 解决 Date 问题

                MaintenanceOrderDao.upsertMaintenanceOrder(order).then(retOrder => {
                    this.saving = false;
                    this.$emit('on-ok', retOrder);
                    this.showEvent(false); // 关闭弹窗
                }).catch(() => {
                    this.saving = false;
                });
            });
        },
        // 新建维保订单
        newOrder() {
            return {
                maintenanceOrderId: '0',   // 维保订单 ID
                customerName      : '',    // 客户名字
                maintainable      : false, // 保养
                repairable        : false, // 维修
                salespersonName   : '',    // 销售人员名字
                receivedDate      : '',    // 收货日期
                productName       : '',    // 产品名称
                productCode       : '',    // 产品序列号
                productModel      : '',    // 规格/型号
                productItemName   : '',    // 物料名称
                productItemBatch  : '',    // 批次
                productItemCount  : 0,     // 数量
                accessories       : '',    // 配件
                orderSn           : '',    // 订单号
                needCertificate   : 0,     // 是否需要证书
                problem           : '',    // 客户反馈的问题
                progress          : '',    // 进度
                currentAuditorId  : '0',   // 当前审批员 ID
            };
        },
    }
};
</script>

<style lang="scss">
.maintenance-order-edit-modal {
    .footer {
        display: flex;

        .auditor-select {
            width: 100px;
        }
    }
}
</style>
