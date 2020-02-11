<!-- 工单: 维修订单 -->
<template>
    <div class="work-orders">
        <!-- 搜索工具栏 -->
        <div class="toolbar">
            <Button @click="editWorkOrder()">创建维修订单</Button>
        </div>

        <!-- 显示工单 -->
        <Table :data="workOrders" :columns="workOrderColumns" border>
            <!-- 订单状态 -->
            <template slot-scope="{ row: order }" slot="status">
                <Poptip transfer trigger="hover">
                    <Tag :color="orderStatus(order.status).color">{{ orderStatus(order.status).name }}</Tag>

                    <div slot="content">
                        开始组装时间: {{ order.startAssembleDate }}<br>
                        完成组装时间: {{ order.finishAssembleDate }}
                    </div>
                </Poptip>
            </template>

            <!-- 下单日期 -->
            <template slot-scope="{ row: order }" slot="orderDate">
                {{ order.orderDate | formatDate }}
            </template>

            <template slot-scope="{ row: order }" slot="action">
                <Button size="small" type="primary" style="margin-right: 5px" @click="editWorkOrder(order)">编辑</Button>
                <Button size="small" type="error" @click="deleteWorkOrder(order)">删除</Button>
            </template>
        </Table>

        <!-- 加载下一页按钮 -->
        <center>
            <Button v-show="more" :loading="loading" icon="md-boat" @click="fetchMoreWorkOrders">更多...</Button>
        </center>

        <!-------------------------------------------------------------------------------------------------------------
                                                              编辑对话框
        -------------------------------------------------------------------------------------------------------------->
        <!-- 编辑工单对话框 -->
        <Modal v-model="orderModal" :mask-closable="false" title="编辑维修订单" width="700">
            <Form ref="workOrderForm" :model="editedWorkOrder" :rules="orderRules" :key="editedWorkOrder.id" :label-width="110" class="two-column">
                <FormItem label="客户名称:" prop="customerName">
                    <Input v-model="editedWorkOrder.customerName" placeholder="请输入客户名称"/>
                </FormItem>
                <FormItem label="维修进程:">
                    <Select v-model="editedWorkOrder.status" :disabled="!editedWorkOrder.neu">
                        <Option :value="0">等待备件</Option>
                        <Option :value="1">组装中</Option>
                        <Option :value="2">完成组装</Option>
                    </Select>
                </FormItem>
                <FormItem label="软件版本:" prop="softwareVersion">
                    <Input v-model="editedWorkOrder.softwareVersion" placeholder="请输入软件版本"/>
                </FormItem>
                <FormItem label="订单日期:">
                    <DatePicker v-model="editedWorkOrder.orderDate"
                                :editable="false"
                                format="yyyy-MM-dd"
                                placement="bottom-end"
                                placeholder="请选择订单日期"
                                style="width: 100%">
                    </DatePicker>
                </FormItem>
                <FormItem label="配件信息:" style="grid-column: span 2">
                    <Input v-model="editedWorkOrder.personInCharge" placeholder="请输入负责人"/>
                </FormItem>
                <FormItem label="负责人:">
                    <Input v-model="editedWorkOrder.personInCharge" placeholder="请输入负责人"/>
                </FormItem>
                <Button type="dashed" icon="md-add" style="justify-self: end" @click="editWorkOrderItem()">添加芯片</Button>
            </Form>

            <!-- 显示工单项 -->
            <Table :data="availableWorkOrderItems(editedWorkOrder)" :columns="workOrderItemColumns" border>
                <template slot-scope="{ row: item }" slot="action">
                    <Button size="small" type="primary" style="margin-right: 5px" @click="editWorkOrderItem(item)">编辑</Button>

                    <Poptip confirm title="确认删除订单项吗?" transfer @on-ok="deleteWorkOrderItem(item)">
                        <Button size="small" type="error">删除</Button>
                    </Poptip>
                </template>
            </Table>

            <div slot="footer">
                <Button type="primary" :loading="saving" @click="saveWorkOrder()">保存</Button>
            </div>
        </Modal>

        <!-- 编辑工单对话框 -->
        <Modal v-model="orderItemModal" :mask-closable="false" title="编辑维修订单" width="700" class="order-item-modal">
            <Form ref="workOrderItemForm"
                    :model="editedWorkOrderItem"
                    :rules="orderItemrules"
                    :key="editedWorkOrderItem.id"
                    :label-width="120"
                    class="two-column">
                <FormItem label="品牌:" prop="brand">
                    <Select v-model="editedWorkOrderItem.brand">
                        <Option value="P+H">P+H</Option>
                        <Option value="BD">BD</Option>
                        <Option value="EBRO">EBRO</Option>
                    </Select>
                </FormItem>
                <FormItem label="型号:" prop="type">
                    <Select v-model="editedWorkOrderItem.type">
                        <Option value="型号-1">型号-1</Option>
                        <Option value="型号-2">型号-2</Option>
                        <Option value="型号-3">型号-3</Option>
                    </Select>
                </FormItem>
                <FormItem label="产品序列号:" prop="">
                    <Input v-model="editedWorkOrderItem.sn" placeholder="请输入产品序列号"/>
                </FormItem>

                <template v-if="editedWorkOrderItem.brand !== 'EBRO'">
                    <FormItem label="固件版本:" prop="">
                        <Input v-model="editedWorkOrderItem.firmwareVersion" placeholder="请输入固件版本"/>
                    </FormItem>
                    <FormItem label="芯片编号:" prop="">
                        <Input v-model="editedWorkOrderItem.chipSn" placeholder="请输入芯片编号"/>
                    </FormItem>
                </template>

                <FormItem label="维修前电量:" prop="beforeElectricQuantity">
                    <InputNumber v-model="editedWorkOrderItem.beforeElectricQuantity" placeholder="请输入维修前电量"/>
                </FormItem>
                <FormItem label="维修前高温次数:" prop="beforeHighTemperatureTimes">
                    <InputNumber v-model="editedWorkOrderItem.beforeHighTemperatureTimes" placeholder="请输入维修前高温次数"/>
                </FormItem>
                <FormItem label="客户反馈:" prop="feedback" style="grid-column: span 2">
                    <Input v-model="editedWorkOrderItem.feedback" type="textarea" autosize placeholder="请输入客户反馈"/>
                </FormItem>
                <FormItem label="检测明细:" prop="testDetails" style="grid-column: span 2">
                    <Input v-model="editedWorkOrderItem.testDetails" type="textarea" autosize placeholder="请输入检测明细"/>
                </FormItem>
            </Form>

            <div slot="footer">
                <Button type="primary" :loading="saving" @click="saveWorkOrderItem()">保存</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import WorkOrderDao from '@/../public/static-p/js/dao/WorkOrderDao';
import WorkOrderUtils from '@/../public/static-p/js/utils/WorkOrderUtils';

export default {
    data() {
        return {
            workOrders: [],
            filter: { // 搜索条件
                name      : '',
                pageSize  : 2,
                pageNumber: 1,
            },

            more               : false, // 是否还有更多用户
            loading            : false, // 加载中
            saving             : false, // 保存中
            orderModal         : false,
            orderItemModal     : false,
            editedWorkOrder    : {}, // 被编辑的工单
            editedWorkOrderItem: {}, // 被编辑的工单项

            // 订单的校验规则
            orderRules: {
                customerName: [
                    { required: true, whitespace: true, message: '客户名称不能为空', trigger: 'blur' }
                ],
            },
            // 订单项的校验规则
            orderItemrules: {
                brand: [
                    { required: true, message: '请选择品牌', trigger: 'change' }
                ],
                type: [
                    { required: true, message: '请选择型号', trigger: 'change' }
                ],
            },

            workOrderColumns: [
                { title: '客户名称', key: 'customerName', width: 110 },
                { title: '维修进程', slot: 'status', width: 110 },
                { title: '软件版本', key: 'softwareVersion', width: 110 },
                { title: '配件信息', key: 'accessory', width: 180 },
                { title: '订单日期', slot: 'orderDate', align: 'center', width: 130 },
                { title: '负责人', key: 'personInCharge', width: 110 },
                { title: '订单项', key: '', minWidth: 200 },
                { title: '操作', slot: 'action', align: 'center', width: 130 },
            ],
            workOrderItemColumns: [
                { title: '品牌', key: 'brand', width: 80 },
                { title: '型号', key: 'type', width: 80 },
                { title: '产品序列号', key: 'sn', width: 120 },
                { title: '固件版本', key: 'firmwareVersion', width: 120 },
                { title: '芯片编号', key: 'chipSn', width: 120 },
                { title: '维修前电量', key: 'beforeElectricQuantity', width: 120 },
                { title: '维修前高温次数', key: 'beforeHighTemperatureTimes', width: 140 },
                { title: '客户反馈', key: 'feedback', minWidth: 200 },
                { title: '检测明细', key: 'testDetails', minWidth: 200 },
                { title: '操作', slot: 'action', align: 'center', width: 130, fixed: 'right' }
            ]
        };
    },
    methods: {
        // 搜索用户
        searchWorkOrders() {
            this.workOrders = [];
            this.filter.pageNumber = 1;
            this.more = false;
            this.fetchMoreWorkOrders();
        },
        // 点击更多按钮加载下一页的用户
        fetchMoreWorkOrders() {
            this.loading = true;

            WorkOrderDao.findWorkOrders(this.filter).then(workOrders => {
                this.workOrders.push(...workOrders);

                this.filter.pageNumber++;
                this.more = workOrders.length >= this.filter.pageSize;
                this.loading = false;
            });
        },
        // 编辑用户: order 为 undefined 表示创建，否则表示更新
        editWorkOrder(order) {
            // 1. 重置表单，避免上一次的验证信息影响到本次编辑
            // 2. 生成编辑对象的副本
            // 3. 显示编辑对话框

            this.$refs.workOrderForm.resetFields();

            if (order) {
                // 更新
                this.editedWorkOrder = WorkOrderUtils.cloneWorkOrder(order); // 重要: 克隆对象
            } else {
                // 创建
                this.editedWorkOrder = WorkOrderUtils.newWorkOrder();
            }

            this.orderModal = true;
        },
        // 保存编辑后的工单
        saveWorkOrder() {
            // 1. 表单验证不通过则返回
            // 2. 克隆被编辑对象
            // 3. 找到被编辑对象的下标
            // 4. 保存成功后如果是更新则替换已有对象，创建则添加到最前面
            // 5. 提示保存成功，隐藏编辑对话框

            this.$refs.workOrderForm.validate(valid => {
                if (!valid) { return; }

                // [2] 克隆被编辑对象
                // [3] 找到被编辑对象的下标
                this.saving = true;
                const order = WorkOrderUtils.cloneWorkOrder(this.editedWorkOrder); // 重要: 克隆被编辑的对象
                const index = this.workOrderIndex(order.id);

                // UserDao.saveUser(user).then(() => {
                    // [4] 保存成功后如果是更新则替换已有对象，创建则添加到最前面
                    if (index >= 0) {
                        // 更新: 替换已有对象
                        this.workOrders.replace(index, order);
                    } else {
                        // 创建: 添加到最前面
                        this.workOrders.insert(0, order);
                    }

                    // [5] 提示保存成功，隐藏编辑对话框
                    this.saving = false;
                    this.orderModal = false;
                    this.$Message.success('保存成功');
                // });
            });
        },
        // 删除工单
        deleteWorkOrder(order) {
            // 1. 删除提示
            // 2. 从服务器删除成功后才从本地删除
            // 3. 提示删除成功

            this.$Modal.confirm({
                title: `确定删除 <font color="red">${order.customerName}</font> 的维修订单吗?`,
                loading: true,
                onOk: () => {
                    // UserDao.deleteUser(user.id).then(() => {
                        const index = this.workOrderIndex(order.id);
                        this.workOrders.remove(index); // [2] 从服务器删除成功后才从本地删除
                        this.$Modal.remove();
                        this.$Message.success('删除成功');
                    // });
                }
            });
        },
        // 编辑用户: orderItem 为 undefined 表示创建，否则表示更新
        editWorkOrderItem(orderItem) {
            // 1. 重置表单，避免上一次的验证信息影响到本次编辑
            // 2. 生成编辑对象的副本
            // 3. 显示编辑对话框

            this.$refs.workOrderItemForm.resetFields();

            if (orderItem) {
                // 更新
                this.editedWorkOrderItem = WorkOrderUtils.cloneWorkOrderItem(orderItem); // 重要: 克隆对象
            } else {
                // 创建
                this.editedWorkOrderItem = WorkOrderUtils.newWorkOrderItem();
            }

            this.orderItemModal = true;
        },
        // 保存编辑后的工单项
        saveWorkOrderItem() {
            // 1. 表单验证不通过则返回
            // 2. 克隆被编辑对象
            // 3. 找到被编辑对象的下标
            // 4. 保存成功后如果是更新则替换已有对象，创建则添加到最前面
            // 5. 隐藏编辑对话框

            this.$refs.workOrderItemForm.validate(valid => {
                if (!valid) { return; }

                // [2] 克隆被编辑对象
                // [3] 找到被编辑对象的下标
                const item  = WorkOrderUtils.cloneWorkOrderItem(this.editedWorkOrderItem); // 重要: 克隆被编辑的对象
                const index = this.workOrderItemIndex(item.id);

                // [4] 保存成功后如果是更新则替换已有对象，创建则添加到最前面
                if (index >= 0) {
                    // 更新: 替换已有对象
                    this.editedWorkOrder.orderItems.replace(index, item);
                } else {
                    // 创建: 添加到最后面
                    this.editedWorkOrder.orderItems.push(item);
                }

                // [5] 隐藏编辑对话框
                this.orderItemModal = false;
            });
        },
        // 删除工单
        deleteWorkOrderItem(orderItem) {
            const index = this.workOrderItemIndex(orderItem.id);
            this.editedWorkOrder.orderItems[index].deleted = true;
        },
        // 可用的订单项 (未被删除的)
        availableWorkOrderItems(order) {
            try {
                return order.orderItems.filter(oi => !oi.deleted);
            } catch {
                return [];
            }
        },
        // 工单的下标
        workOrderIndex(workOrderId) {
            return this.workOrders.findIndex(m => m.id === workOrderId);
        },
        // 工单项的下标
        workOrderItemIndex(workOrderItemId) {
            return this.editedWorkOrder.orderItems.findIndex(item => item.id === workOrderItemId);
        },
    }
};
</script>

<style lang="scss">
.work-orders {
    display: grid;
    grid-gap: 12px;

    .toolbar {
        display: grid;
        grid-template-columns: max-content 200px 100px;
        grid-gap: 12px;
        align-items: center;
    }
}
</style>
