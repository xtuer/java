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
    <Modal :value="visible" title="编辑维保订单" :mask-closable="false" :width="1000" class="maintenance-order-edit-modal"
        :styles="{ top: '40px', marginBottom: '80px' }"
        @on-visible-change="showEvent"
    >
        <!-- 弹窗 Body -->
        <Spin v-if="loading" fix size="large"></Spin>
        <Form ref="orderForm" :model="order" :rules="rules" :label-width="100" class="relative">
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
            <!-- <div class="column-3">
                <FormItem label="产品名称:" prop="customerCompany">
                    <Input v-model="order.productName" search placeholder="请输入产品名称" @on-search="showProductSelect"/>
                </FormItem>
                <FormItem label="产品编码:" prop="customerCompany">
                    <Input v-model="order.productCode" clearable placeholder="请输入产品编码"/>
                </FormItem>
                <FormItem label="规格/型号:" prop="customerCompany">
                    <Input v-model="order.productModel" clearable placeholder="请输入规格/型号"/>
                </FormItem>
            </div> -->
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
                    <InputNumber v-model="order.productCount" :min="0" placeholder="产品数量" style="width: 100%" @on-change="ensureInt(order, 'productCount', $event)"/>
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

        <!-- 维修信息项列表 -->
        <Table :data="order.items" :columns="orderItemColumns" :max-height="350" border>
            <template slot-scope="{ index }" slot="productName">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].productName }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].productName" clearable placeholder="请输入产品名称"/>
            </template>
            <template slot-scope="{ index }" slot="productCode">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].productCode }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].productCode" clearable placeholder="请输入产品编码"/>
            </template>
            <template slot-scope="{ index }" slot="productModel">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].productModel }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].productModel" clearable placeholder="请输入规格型号"/>
            </template>
            <template slot-scope="{ index }" slot="productionDate">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].productionDate }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].productionDate" clearable placeholder="请输入出厂时间"/>
            </template>
            <template slot-scope="{ index }" slot="electricQuantityBefore">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].electricQuantityBefore }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].electricQuantityBefore" clearable placeholder="请输入维修前电量"/>
            </template>
            <template slot-scope="{ index }" slot="softwareVersionBefore">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].softwareVersionBefore }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].softwareVersionBefore" clearable placeholder="请输入维修前软件版本"/>
            </template>
            <template slot-scope="{ index }" slot="hardwareVersionBefore">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].hardwareVersionBefore }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].hardwareVersionBefore" clearable placeholder="请输入维修前硬件版本"/>
            </template>
            <template slot-scope="{ index }" slot="powerDissipationBefore">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].powerDissipationBefore }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].powerDissipationBefore" clearable placeholder="请输入维修前功耗"/>
            </template>
            <template slot-scope="{ index }" slot="probeDetectorCodeBefore">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].probeDetectorCodeBefore }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].probeDetectorCodeBefore" clearable placeholder="请输入探头换前编号"/>
            </template>
            <template slot-scope="{ index }" slot="temperatureBefore">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].temperatureBefore }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].temperatureBefore" :min="0" clearable placeholder="请输入维修前高温次数"/>
            </template>
            <template slot-scope="{ index }" slot="chipCode">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].chipCode }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].chipCode" clearable placeholder="请输入芯片编号"/>
            </template>
            <template slot-scope="{ index }" slot="checkDetails">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].checkDetails }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].checkDetails" clearable placeholder="请输入检测问题明细"/>
            </template>
            <template slot-scope="{ index }" slot="maintenanceDetails">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].maintenanceDetails }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].maintenanceDetails" clearable placeholder="请输入维修明细"/>
            </template>
            <template slot-scope="{ index }" slot="electricQuantityAfter">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].electricQuantityAfter }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].electricQuantityAfter" clearable placeholder="请输入维修后电量"/>
            </template>
            <template slot-scope="{ index }" slot="softwareVersionAfter">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].softwareVersionAfter }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].softwareVersionAfter" clearable placeholder="请输入维修后软件版本"/>
            </template>
            <template slot-scope="{ index }" slot="hardwareVersionAfter">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].hardwareVersionAfter }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].hardwareVersionAfter" clearable placeholder="请输入维修后硬件版本"/>
            </template>
            <template slot-scope="{ index }" slot="powerDissipationAfter">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].powerDissipationAfter }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].powerDissipationAfter" clearable placeholder="请输入维修后功耗"/>
            </template>
            <template slot-scope="{ index }" slot="probeDetectorCodeAfter">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].probeDetectorCodeAfter }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].probeDetectorCodeAfter" clearable placeholder="请输入探头换后编号"/>
            </template>
            <template slot-scope="{ index }" slot="temperatureAfter">
                <div v-show="!showItemEdit(index)" @click="selectEditedItem(index)" class="item-content">
                    {{ order.items[index].temperatureAfter }}
                </div>
                <Input v-if="showItemEdit(index)" v-model="order.items[index].temperatureAfter" :min="0" clearable placeholder="请输入维修后高温次数"/>
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: item }" slot="action">
                <!-- <Icon type="md-create" size="16" class="clickable margin-right-5" @click="editOrderItem(item)"/> -->

                <Poptip
                    confirm
                    transfer
                    title="确定删除?"
                    @on-ok="deleteOrderItem(item)">
                    <Icon type="md-close" size="16" class="clickable"/>
                </Poptip>
            </template>
        </Table>
        <Button class="margin-top-10" icon="md-add" @click="editOrderItem()">新建维修信息项</Button>

        <!-- 底部工具栏 -->
        <div slot="footer" class="footer">
            <AuditorSelect v-model="order.currentAuditorId" :step="1" type="MAINTENANCE_ORDER"/>
            <span class="stretch"></span>
            <Button type="text" @click="showEvent(false)">取消</Button>
            <Button type="default" :loading="saving" @click="uncommittedSave">暂存</Button>
            <Button type="primary" :loading="saving" @click="committedSave">确定</Button>
        </div>

        <!-- 产品选择弹窗 -->
        <ProductSelect v-model="productSelectVisible" @on-ok="onProductSelected"/>

        <!-- 维保订单项编辑弹窗 -->
        <!-- <MaintenanceOrderItemEdit v-model="orderItemEditVisible" :order-item="orderItemClone" @on-ok="saveOrderItem"/> -->
    </Modal>
</template>

<script>
import MaintenanceOrderDao from '@/../public/static-p/js/dao/MaintenanceOrderDao';
import ProductSelect from '@/components/ProductSelect.vue';
import AuditorSelect from '@/components/AuditorSelect.vue';
// import MaintenanceOrderItemEdit from '@/components/MaintenanceOrderItemEdit.vue';

export default {
    props: {
        visible: { type: Boolean, required: true }, // 是否可见
        maintenaceOrderId: { type: String,  required: true }, // 维保订单 ID
    },
    model: {
        prop : 'visible',
        event: 'on-visible-change',
    },
    components: { ProductSelect, AuditorSelect, },
    data() {
        return {
            order: this.newOrder(),
            selectedItem: {}, // 选择编辑的维保信息项
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

            orderItemClone: {},          // 用于编辑的维保订单项
            orderItemEditVisible: false, // 维保订单项编辑弹窗是否可见
            orderItemColumns: [          // 维保订单项表格的列
                { type: 'index', width: 50, align: 'center', className: 'table-index' },
                { slot: 'productName', title: '产品名称', width: 150 },
                { slot: 'productCode', title: '产品编码', width: 150 },
                { slot: 'productModel', title: '规格型号', width: 150 },
                { slot: 'productionDate', title: '出厂时间', width: 150 },
                { slot: 'electricQuantityBefore', title: '维修前电量', width: 150 },
                { slot: 'softwareVersionBefore', title: '维修前软件版本', width: 150 },
                { slot: 'hardwareVersionBefore', title: '维修前硬件版本', width: 150 },
                { slot: 'powerDissipationBefore', title: '维修前功耗', width: 150 },
                { slot: 'temperatureBefore', title: '维修前高温次数', width: 150 },
                { slot: 'chipCode', title: '芯片编号', width: 150 },
                { slot: 'checkDetails', title: '检测问题明细', width: 350 },
                { slot: 'maintenanceDetails', title: '维修明细', width: 350 },
                { slot: 'probeDetectorCodeBefore', title: '探头换前编号', width: 150 },
                { slot: 'electricQuantityAfter', title: '维修后电量', width: 150 },
                { slot: 'softwareVersionAfter', title: '维修后软件版本', width: 150 },
                { slot: 'hardwareVersionAfter', title: '维修后硬件版本', width: 150 },
                { slot: 'powerDissipationAfter', title: '维修后功耗', width: 150 },
                { slot: 'temperatureAfter', title: '维修后高温次数', width: 150 },
                { slot: 'probeDetectorCodeAfter', title: '探头换后编号', width: 150 },
                { slot: 'action', title: '', width: 50, align: 'center', fixed: 'right', className: 'table-action' },
            ],
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
        committedSave() {
            this.order.committed = true;
            this.save();
        },
        uncommittedSave() {
            this.order.committed = false;
            this.save();
        },
        // 保存维保订单
        save() {
            // 审批员不能为空
            if (this.order.committed && !Utils.isValidId(this.order.currentAuditorId)) {
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
                items             : [],    // 维保订单项
                committed         : false, // 是否提交
            };
        },
        // 新建维保订单项
        newOrderItem() {
            return {
                maintenanceOrderItemId : Utils.nextId(), // 维保订单项 ID
                productName            : '', // 产品名称
                productCode            : '', // 产品编码
                productModel           : '', // 规格型号
                productionDate         : '', // 出厂时间
                electricQuantityBefore : '',  // 维修前电量
                softwareVersionBefore  : '', // 维修前软件版本
                hardwareVersionBefore  : '', // 维修前硬件版本
                powerDissipationBefore : '',  // 维修前功耗
                temperatureBefore      : '',  // 维修前高温次数
                chipCode               : '', // 芯片编号
                checkDetails           : '', // 检测问题明细
                maintenanceDetails     : '', // 维修明细
                probeDetectorCodeBefore: '', // 探头换前编号
                electricQuantityAfter  : '',  // 维修后电量
                softwareVersionAfter   : '', // 维修后软件版本
                hardwareVersionAfter   : '', // 维修后硬件版本
                powerDissipationAfter  : '',  // 维修后功耗
                temperatureAfter       : '',  // 维修后高温次数
                probeDetectorCodeAfter : '', // 探头换后编号
            };
        },
        // 创建维保订单项
        editOrderItem(item) {
            // item 存在则编辑，否则为创建
            if (item) {
                this.orderItemClone = Utils.clone(item);
            } else {
                this.orderItemClone = this.newOrderItem();
            }

            // this.orderItemEditVisible = true;
            this.saveOrderItem(this.orderItemClone);
        },
        // 保存维保订单项
        saveOrderItem(item) {
            // 查找不到则添加，否则替换
            const index = this.order.items.findIndex(i => i.maintenanceOrderItemId === item.maintenanceOrderItemId);

            if (index >= 0) {
                this.order.items.replace(index, item);
            } else {
                this.order.items.push(item);
            }
        },
        // 删除维保订单项
        deleteOrderItem(item) {
            const index = this.order.items.findIndex(i => i.maintenanceOrderItemId === item.maintenanceOrderItemId);

            if (index >= 0) {
                this.order.items.remove(index);
            }
        },
        // 选择要被编辑的维保信息项
        selectEditedItem(index) {
            this.selectedItem = this.order.items[index];
        },
        // 是否显示维保信息项的编辑组件
        showItemEdit(index) {
            return this.order.items[index] === this.selectedItem;
        }
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

    .ivu-input-number {
        width: 100%;
    }

    .item-content {
        min-height: 24px;
        border: 1px dashed transparent;
        border-radius: 2px;
        transition: .6s border;

        &:hover {
            border: 1px dashed gray;
        }
    }
}
</style>
