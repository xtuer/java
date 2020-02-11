<!-- 工单: 维修订单 -->
<template>
    <div class="work-orders">
        <!-- 搜索工具栏 -->
        <div class="toolbar">
            <Button @click="editWorkOrder()">创建维修订单</Button>
        </div>

        <!-- 显示工单 -->
        <Table :data="workOrders" :columns="columns" border></Table>

        <!-- 加载下一页按钮 -->
        <center>
            <Button v-show="more" :loading="loading" icon="md-boat" @click="fetchMoreWorkOrders">更多...</Button>
        </center>

        <!-------------------------------------------------------------------------------------------------------------
                                                              编辑对话框
        -------------------------------------------------------------------------------------------------------------->
        <!-- 编辑工单对话框 -->
        <Modal v-model="modal" :mask-closable="false" title="编辑维修订单" width="700">
            <Form ref="workOrderForm" :model="editedWorkOrder" :key="editedWorkOrder.id" :label-width="110" class="two-column">
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
                <Button type="dashed" icon="md-add" style="justify-self: end" @click="editWorkOrder()">添加芯片</Button>
            </Form>

            <div slot="footer">
                <Button type="primary" :loading="saving" @click="saveWorkOrder()">保存</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import WorkOrderDao from '@/../public/static-p/js/dao/WorkOrderDao';
import WorkOrderUtils from '@/../public/static-p/js/WorkOrderUtils';

export default {
    data() {
        return {
            workOrders: [],
            filter: { // 搜索条件
                name      : '',
                pageSize  : 2,
                pageNumber: 1,
            },
            modal  : true,
            more   : false, // 是否还有更多用户
            loading: false, // 加载中
            saving : false, // 保存中
            editedWorkOrder: {}, // 被编辑的工单

            columns: [
                { title: '客户名称', key: 'customerName' },
                { title: '软件版本', key: 'softwareVersion' },
                { title: '负责人', key: 'personInCharge' },
            ],
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
        // 编辑用户: user 为 undefined 表示创建，否则表示更新
        editWorkOrder(main) {
            // 1. 重置表单，避免上一次的验证信息影响到本次编辑
            // 2. 生成编辑对象的副本
            // 3. 显示编辑对话框

            this.$refs.workOrderForm.resetFields();

            if (main) {
                // 更新
                this.editedWorkOrder = WorkOrderUtils.cloneWorkOrder(main); // 重要: 克隆对象
            } else {
                // 创建
                this.editedWorkOrder = WorkOrderUtils.newWorkOrder();
            }

            this.modal = true;
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
                const main  = WorkOrderUtils.cloneWorkOrder(this.editedWorkOrder); // 重要: 克隆被编辑的对象
                const index = this.workOrderIndex(main.id);

                // UserDao.saveUser(user).then(() => {
                    // [4] 保存成功后如果是更新则替换已有对象，创建则添加到最前面
                    if (index >= 0) {
                        // 更新: 替换已有对象
                        this.workOrders.replace(index, main);
                    } else {
                        // 创建: 添加到最前面
                        this.workOrders.insert(0, main);
                    }

                    // [5] 提示保存成功，隐藏编辑对话框
                    this.saving = false;
                    this.modal  = false;
                    this.$Message.success('保存成功');
                // });
            });
        },
        // 删除用户
        deleteWorkOrder(main) {
            // 1. 删除提示
            // 2. 从服务器删除成功后才从本地删除
            // 3. 提示删除成功

            this.$Modal.confirm({
                title: `确定删除 <font color="red">${main.username}</font> 吗?`,
                loading: true,
                onOk: () => {
                    // UserDao.deleteUser(user.id).then(() => {
                        const index = this.workOrderIndex(main.id);
                        this.workOrders.remove(1); // [2] 从服务器删除成功后才从本地删除
                        this.$Modal.remove();
                        this.$Message.success('删除成功');
                    // });
                }
            });
        },
        // 工单的下标
        workOrderIndex(workOrderId) {
            return this.workOrders.findIndex(m => m.id === workOrderId);
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
