<!-- 订单管理 -->
<template>
    <div class="orders">
        <!-- 搜索工具栏 -->
        <div class="toolbar">
            <Button @click="editOrder()">创建订单</Button>
        </div>

        <!-- 显示订单 -->
        <Table :data="orders" :columns="orderColumns" border>
            <!-- 订单状态 -->
            <template slot-scope="{ row: order }" slot="status">
                <Poptip transfer trigger="hover">
                    <Tag :color="orderStatus(order.status).color" @click.native="nextOrderStatus(order)">{{ orderStatus(order.status).name }}</Tag>

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

            <template slot-scope="{ row: order }" slot="orderAction">
                <Button size="small" type="primary" style="margin-right: 5px" @click="editOrder(order)">编辑</Button>
                <Button size="small" type="error" @click="deleteOrder(order)">删除</Button>
            </template>

            <!-- 订单项 -->
            <template slot-scope="{ row: order }" slot="orderItems">
                <template v-if="availableOrderItems(order).length">
                    <Poptip transfer trigger="hover">
                        <div style="padding: 6px 0; cursor: default">
                            <div v-for="item in availableOrderItems(order)" :key="item.id">
                                <Badge :text="item.count + ' 个'" type="normal"/>
                                {{ item.type }} - {{ item.sn }}
                            </div>
                        </div>
                        <div slot="content">
                            <Table :columns="orderItemColumns.slice(0, orderItemColumns.length-1)" :data="availableOrderItems(order)" width="500" border/>
                        </div>
                    </Poptip>
                </template>
                <template v-else>
                    无
                </template>
            </template>
        </Table>

        <!-- 加载下一页订单按钮 -->
        <center>
            <Button v-show="more" :loading="loading" icon="md-boat" shape="circle" @click="fetchMoreOrders">更多...</Button>
        </center>

        <!-------------------------------------------------------------------------------------------------------------
                                                              编辑对话框
        -------------------------------------------------------------------------------------------------------------->
        <!-- 编辑订单对话框 -->
        <Modal v-model="orderModal" :mask-closable="false" title="编辑订单" width="700">
            <Form ref="orderForm" :model="editedOrder" :rules="orderRules" :key="editedOrder.id" :label-width="90" class="two-column">
                <FormItem label="客户名称:" prop="customerName">
                    <Input v-model="editedOrder.customerName" placeholder="请输入客户名称"/>
                </FormItem>
                <FormItem label="生产进程:">
                    <Select v-model="editedOrder.status" :disabled="!editedOrder.neu">
                        <Option :value="0">等待备件</Option>
                        <Option :value="1">组装中</Option>
                        <Option :value="2">完成组装</Option>
                    </Select>
                </FormItem>
                <FormItem label="订单类型:">
                    <Select v-model="editedOrder.type">
                        <Option value="订货">订货</Option>
                        <Option value="样品">样品</Option>
                    </Select>
                </FormItem>
                <FormItem label="品牌:">
                    <Select v-model="editedOrder.brand">
                        <Option value="P+H">P+H</Option>
                        <Option value="BD">BD</Option>
                        <Option value="其他">其他</Option>
                    </Select>
                </FormItem>
                <FormItem label="软件版本:" prop="softwareVersion">
                    <Input v-model="editedOrder.softwareVersion" placeholder="请输入软件版本"/>
                </FormItem>
                <FormItem label="订单日期:">
                    <DatePicker v-model="editedOrder.orderDate"
                                :editable="false"
                                format="yyyy-MM-dd"
                                placement="bottom-end"
                                placeholder="请选择订单日期"
                                style="width: 100%">
                    </DatePicker>
                </FormItem>
                <FormItem label="负责人:">
                    <Input v-model="editedOrder.personInCharge" placeholder="请输入负责人"/>
                </FormItem>
                <Button type="dashed" icon="md-add" style="justify-self: end" @click="editOrderItem()">添加订单项</Button>

                <!-- 订单项列表 -->
                <Table :columns="orderItemColumns" :data="availableOrderItems(editedOrder)" border style="grid-column: span 2">
                    <template slot-scope="{ row: item }" slot="orderItemAction">
                        <Button size="small" type="primary" style="margin-right: 5px" @click="editOrderItem(item)">编辑</Button>

                        <Poptip confirm title="确认删除订单项吗?" transfer @on-ok="deleteOrderItem(item)">
                            <Button size="small" type="error">删除</Button>
                        </Poptip>
                    </template>
                </Table>
            </Form>

            <div slot="footer">
                <Button type="primary" :loading="saving" @click="saveOrder()">保存</Button>
            </div>
        </Modal>

        <!-- 编辑订单项对话框 -->
        <Modal v-model="orderItemModal" :mask-closable="false" title="编辑订单项" width="600" :styles="{ top: '150px' }">
            <Form ref="orderItemForm" :model="editedOrderItem" :rules="orderItemRules" :label-width="100" :key="editedOrderItem.id" class="two-column">
                <FormItem label="产品型号:" prop="type">
                    <Select v-model="editedOrderItem.type">
                        <Option v-for="type in window.ORDER_ITEM_TYPES" :value="type" :key="type">{{ type }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="产品序列号:" prop="sn">
                    <Input v-model="editedOrderItem.sn" placeholder="请输入产品序列号"/>
                </FormItem>
                <FormItem label="芯片编号:" prop="chipSn">
                    <Input v-model="editedOrderItem.chipSn" placeholder="请输入芯片编号"/>
                </FormItem>
                <FormItem label="外壳颜色:">
                    <Input v-model="editedOrderItem.shellColor" placeholder="请输入外壳颜色"/>
                </FormItem>
                <FormItem label="外壳批次:">
                    <Input v-model="editedOrderItem.shellBatch" placeholder="请输入外壳批次"/>
                </FormItem>
                <FormItem label="数量:">
                    <InputNumber v-model="editedOrderItem.count" :min="1" placeholder="请输入数量" style="width: 100%"/>
                </FormItem>
                <FormItem label="传感器信息:">
                    <Input v-model="editedOrderItem.sensorInfo" placeholder="请输入传感器信息"/>
                </FormItem>
                <FormItem label="Ο 型圈信息:">
                    <Input v-model="editedOrderItem.circleInfo" placeholder="请输入 Ο 型圈信息"/>
                </FormItem>
            </Form>

            <div slot="footer">
                <Button type="primary" @click="saveOrderItem">保存</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import OrderUtils from '@/../public/static-p/js/utils/OrderUtils';
import OrderDao from '@/../public/static-p/js/dao/OrderDao';

export default {
    data() {
        return {
            orders: [],
            filter: { // 搜索条件
                name      : '',
                pageSize  : 2,
                pageNumber: 1,
            },

            more   : false, // 是否还有更多用户
            loading: false, // 加载中
            saving : false, // 保存中

            // 被编辑的订单和订单项
            editedOrder    : {},
            editedOrderItem: {},
            orderModal     : false, // 显示订单对话框
            orderItemModal : false, // 显示订单项对话框

            orderColumns: [
                { title: '客户名称', key: 'customerName', width: 110 },
                { title: '生产进程', slot: 'status', width: 110 },
                { title: '订单类型', key: 'type', width: 110 },
                { title: '品牌',    key: 'brand', width: 110 },
                { title: '软件版本', key: 'softwareVersion', width: 110 },
                { title: '订单日期', slot: 'orderDate', align: 'center', width: 130 },
                { title: '负责人',   key: 'personInCharge', width: 110 },
                { title: '订单项', slot: 'orderItems' },
                { title: '操作', slot: 'orderAction', align: 'center', width: 130 },
            ],
            orderItemColumns: [
                { title: '产品型号', key: 'type', width: 110, fixed: 'left' },
                { title: '产品序列号', key: 'sn', width: 110 },
                { title: '芯片编号', key: 'chipSn', width: 110 },
                { title: '数量', key: 'count', width: 110 },
                { title: '外壳颜色', key: 'shellColor', width: 110 },
                { title: '外壳批次', key: 'shellBatch', width: 110 },
                { title: '传感器信息', key: 'sensorInfo', width: 200 },
                { title: 'Ο 型圈信息', key: 'circleInfo', width: 200 },
                { title: '操作', slot: 'orderItemAction', align: 'center', width: 130, fixed: 'right' },
            ],

            // 订单的校验规则
            orderRules: {
                customerName: [
                    { required: true, whitespace: true, message: '客户名称不能为空', trigger: 'blur' }
                ],
                softwareVersion: [
                    { required: true, whitespace: true, message: '软件版本不能为空', trigger: 'blur' }
                ],
            },
            orderItemRules: {
                type: [
                    { required: true, message: '型号不能为空', trigger: 'change' }
                ],
                sn: [
                    { required: true, whitespace: true, message: '序列号不能为空', trigger: 'blur' }
                ],
                chipSn: [
                    { required: true, whitespace: true, message: '芯片编号不能为空', trigger: 'blur' }
                ],
            }
        };
    },
    created() {
        this.searchOrders();
    },
    methods: {
        // 搜索订单
        searchOrders() {
            this.orders = [];
            this.filter.pageNumber = 1;
            this.more = false;
            this.fetchMoreOrders();
        },
        // 点击更多按钮加载下一页的订单
        fetchMoreOrders() {
            this.loading = true;

            OrderDao.findOrders(this.filter).then(orders => {
                this.orders.push(...orders);

                this.filter.pageNumber++;
                this.more = (orders.length >= this.filter.pageSize);
                this.loading = false;
            });
        },
        // 点击编辑按钮，在弹窗中编辑订单
        editOrder(order) {
            // 1. 重置表单，避免上一次的验证信息影响到本次编辑
            // 2. 生成编辑对象的副本
            // 3. 显示编辑对话框

            // [1] 重置表单，避免上一次的验证信息影响到本次编辑
            this.$refs.orderForm.resetFields();

            if (order) {
                // 更新
                this.editedOrder = OrderUtils.cloneOrder(order);
            } else {
                // 创建
                this.editedOrder = OrderUtils.newOrder();
            }

            this.orderModal = true;
        },
        // 保存订单
        saveOrder() {
            this.$refs.orderForm.validate(valid => {
                if (!valid) { return; }

                this.saving = true;
                const index = this.orders.findIndex(o => o.id === this.editedOrder.id);

                OrderUtils.cleanOrder(this.editedOrder);
                OrderDao.saveOrder(this.editedOrder).then((order) => {
                    if (index >= 0) {
                        // 更新: 替换已有对象
                        this.orders.replace(index, order);
                    } else {
                        // 创建: 添加到最前面
                        this.orders.insert(0, order);
                    }

                    this.saving = false;
                    this.orderModal = false;
                    this.$Message.success('保存订单成功');
                });
            });
        },
        // 删除订单
        deleteOrder(order) {
            this.$Modal.confirm({
                title: `确定删除 <font color="red">${order.customerName}</font> 的订单吗?`,
                loading: true,
                onOk: () => {
                    OrderDao.deleteOrder(order.id).then(() => {
                        const index = this.orders.findIndex(o => o.id === order.id);
                        this.orders.remove(index); // 服务器删除成功后才从 users 中删除
                        this.$Modal.remove();
                        this.$Message.success('删除成功');
                    });
                }
            });
        },
        // 编辑订单项
        editOrderItem(orderItem) {
            // 1. 重置表单，避免上一次的验证信息影响到本次编辑
            // 2. 生成编辑对象的副本
            // 3. 显示编辑对话框

            // [1] 重置表单，避免上一次的验证信息影响到本次编辑
            this.$refs.orderItemForm.resetFields();

            if (orderItem) {
                // 更新
                this.editedOrderItem = OrderUtils.cloneOrderItem(orderItem);
            } else {
                // 创建
                this.editedOrderItem = OrderUtils.newOrderItem();
            }

            this.orderItemModal = true;
        },
        // 保存订单项
        saveOrderItem() {
            this.$refs.orderItemForm.validate(valid => {
                if (!valid) { return; }

                let orderItem = OrderUtils.cloneOrderItem(this.editedOrderItem);
                let index = this.editedOrder.orderItems.findIndex(item => item.id === orderItem.id);

                if (index >= 0) {
                    // 更新则替换已有的对象
                    this.editedOrder.orderItems.replace(index, orderItem);
                } else {
                    // 创建则添加到最后面
                    this.editedOrder.orderItems.push(orderItem);
                }

                this.orderItemModal = false;
            });
        },
        // 删除订单项
        deleteOrderItem(orderItem) {
            let index = this.editedOrder.orderItems.findIndex(item => item.id === orderItem.id);
            this.editedOrder.orderItems[index].deleted = true;
        },
        // 修改订单状态
        nextOrderStatus(order) {
            // 状态变化:
            //     0 -> 1 == 等待备件 -> 开始组装
            //     1 -> 2 == 开始组装 -> 完成组装

            let status = order.status + 1; // 更新为下一个状态

            if (status === 1 || status === 2) {
                let label = status === 1 ? '开始组装' : '完成组装';

                this.$Modal.confirm({
                    title: `确定 <font color="red">${label}</font> 吗?`,
                    loading: true,
                    onOk: () => {
                        OrderDao.patchOrder({ id: order.id, status }).then(() => {
                            const originalOrder = this.orders.find(o => o.id === order.id);
                            originalOrder.status = status;
                            this.$Modal.remove();
                            this.$Message.success('生产进程更新成功');
                        });
                    }
                });
            }
        },
        // 可用的订单项 (未被删除的)
        availableOrderItems(order) {
            try {
                return order.orderItems.filter(oi => !oi.deleted);
            } catch {
                return [];
            }
        }
    },
};
</script>

<style lang="scss">
.orders {
    display: grid;
    grid-gap: 12px;

    .toolbar {
        display: grid;
        grid-template-columns: max-content 200px 100px;
        grid-gap: 12px;
        align-items: center;
    }

    // .ivu-badge-count-alone {
    //     background: #ddd !important;
    //     color: #888;
    // }
}
</style>
