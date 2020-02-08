<template>
    <div class="order">
        <!-- 搜索工具栏 -->
        <div class="toolbar">
            <Button @click="editOrder(-1)">创建订单</Button>
        </div>

        <!-- 显示订单 -->
        <Table :data="orders" :columns="orderColumns" border>
            <template slot-scope="{ row: order }" slot="orderDate">
                {{ order.orderDate | formatDate }}
            </template>

            <template slot-scope="{ index }" slot="action">
                <Button size="small" type="primary" style="margin-right: 5px" @click="editOrder(index)">编辑</Button>
                <Button size="small" type="error" @click="deleteOrder(index)">删除</Button>
            </template>

            <template slot-scope="{ row: order }" slot="orderItems">
                <ul v-if="order.orderItems.length">
                    <li v-for="item in order.orderItems" :key="item.id">{{ item.id }}</li>
                </ul>
                <div v-else>无</div>
            </template>
        </Table>

        <!-- 加载下一页订单按钮 -->
        <center>
            <Button :loading="loading" v-show="more" icon="md-paw" shape="circle" @click="fetchMoreOrders">更多...</Button>
        </center>

        <!-- 编辑订单对话框 -->
        <Modal v-model="orderModal" :mask-closable="false" title="编辑订单" width="700">
            <Form ref="orderForm" :model="editedOrder" :rules="orderRules" :key="editedOrder.id" :label-width="90" class="two-column">
                <FormItem label="客户名称:" prop="customerName">
                    <Input v-model="editedOrder.customerName" placeholder="请输入客户名称"/>
                </FormItem>
                <FormItem label="生产进程:">
                    <Select v-model="editedOrder.process">
                        <Option value="等待备件">等待备件</Option>
                        <Option value="组装中">组装中</Option>
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
                <FormItem>
                    <Button type="dashed" icon="md-add" style="float: right" @click="editOrderItem(-1)">添加订单项</Button>
                </FormItem>
            </Form>

            <div slot="footer">
                <Button type="primary" :loading="loading" @click="saveOrder">保存</Button>
            </div>
        </Modal>

        <!-- 编辑订单项对话框 -->
        <Modal v-model="orderItemModal" :mask-closable="false" title="编辑订单项" width="600">
            <Form ref="orderItemForm" :model="editedOrderItem" :rules="orderItemRules" :label-width="100" class="two-column">
                <FormItem label="产品型号:" prop="type">
                    <Select v-model="editedOrderItem.type">
                        <Option v-for="type in ORDER_ITEM_TYPES" :value="type" :key="type">{{ type }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="产品序列号:" prop="sn">
                    <Input v-model="editedOrderItem.sn" placeholder="请输入产品序列号"/>
                </FormItem>
                <FormItem label="芯片编号:" prop="chipSn">
                    <Input v-model="editedOrderItem.chipSn" placeholder="请输入芯片编号"/>
                </FormItem>
                <FormItem label="外壳颜色:">
                    <Input v-model="editedOrderItem.shellOrder" placeholder="请输入外壳颜色"/>
                </FormItem>
                <FormItem label="外壳批次:">
                    <Input v-model="editedOrderItem.shellBatch" placeholder="请输入外壳批次"/>
                </FormItem>
                <FormItem label="数量:">
                    <InputNumber :min="1" v-model="editedOrderItem.count" placeholder="请输入数量" style="width: 100%"/>
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
import OrderUtils from '@/../public/static-p/js/OrderUtils';
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
            loading: true,  // 加载中

            // 被编辑的订单和订单项
            editedOrder         : {},
            editedOrderItem     : {},
            editedOrderIndex    : -1,
            editedOrderItemIndex: -1,
            orderModal          : false, // 显示订单对话框
            orderItemModal      : false, // 显示订单项对话框
            ORDER_ITEM_TYPES    : ORDER_ITEM_TYPES, // 订单项类型

            orderColumns: [
                { title: '客户名称', key: 'customerName', width: 110 },
                { title: '生产进程', key: 'process', width: 110 },
                { title: '订单类型', key: 'type', width: 110 },
                { title: '品牌',    key: 'brand', width: 110 },
                { title: '软件版本', key: 'softwareVersion', width: 110 },
                { title: '订单日期', slot: 'orderDate', align: 'center', width: 130 },
                { title: '负责人',   key: 'personInCharge', width: 110 },
                { title: '订单项', slot: 'orderItems' },
                { title: '操作', slot: 'action', align: 'center', width: 130 },
            ],
            orderItemColumns: [],

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
                    { required: true, whitespace: true, message: '序列号为空', trigger: 'blur' }
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
        editOrder(index) {
            // 1. 重置表单，去掉上一次的验证信息
            // 2. 生成编辑订单的副本
            // 3. 保存编辑的订单下标
            // 4. 在弹窗对订单的副本进行编辑

            // [1] 重置表单，去掉上一次的验证信息
            this.$refs.orderForm.resetFields();

            if (index === -1) {
                // 创建
                this.editedOrder = OrderUtils.newOrder();
            } else {
                // 编辑
                this.editedOrder = OrderUtils.cloneOrder(this.orders[index]);
            }

            this.editedOrderIndex = index;
            this.orderModal = true;
        },
        // 保存订单
        saveOrder() {
            this.$refs.orderForm.validate(valid => {
                if (!valid) { return; }

                this.loading = true;

                OrderUtils.cleanOrder(this.editedOrder);
                OrderDao.saveOrder(this.editedOrder).then((orderId) => {
                    this.editedOrder.id = orderId;

                    if (this.editedOrderIndex === -1) {
                        // 创建的用户添加到最前面
                        this.orders.splice(0, 0, this.editedOrder);
                    } else {
                        // 更新则替换已有的对象
                        this.orders.splice(this.editedOrderIndex, 1, this.editedOrder);
                    }

                    this.loading = false;
                    this.orderModal = false;
                    this.$Message.success('保存订单成功');
                });
            });
        },
        // 删除订单
        deleteOrder(index) {
            let order = this.orders[index];

            this.$Modal.confirm({
                title: `确定要删除 <font color="red">${order.customerName}</font> 的订单吗?`,
                loading: true,
                onOk: () => {
                    OrderDao.deleteOrder(order.id).then(() => {
                        this.orders.splice(index, 1); // 服务器删除成功后才从 users 中删除
                        this.$Modal.remove();
                        this.$Message.success('删除成功');
                    });
                }
            });
        },
        // 编辑订单项
        editOrderItem(index) {
            // 1. 重置表单，去掉上一次的验证信息
            // 2. 生成编辑订单项的副本
            // 3. 保存编辑的订单项下标
            // 4. 在弹窗对订单项的副本进行编辑

            // [1] 重置表单，去掉上一次的验证信息
            this.$refs.orderItemForm.resetFields();

            if (index === -1) {
                // 创建
                this.editedOrderItem = OrderUtils.newOrderItem();
            } else {
                // 编辑
                this.editedOrderItem = JSON.parse(JSON.stringify(this.orderItems[index]));
            }

            this.editedOrderItemIndex = index;
            this.orderItemModal = true;
        },
        // 保存订单项
        saveOrderItem() {
            this.$refs.orderItemForm.validate(valid => {
                if (!valid) { return; }

                if (this.editedOrderItemIndex === -1) {
                    // 创建的用户添加到最前面
                    this.editedOrder.orderItems.splice(0, 0, this.editedOrderItem);
                } else {
                    // 更新则替换已有的对象
                    this.editedOrder.orderItems.splice(this.editedOrderItemIndex, 1, this.editedOrderItem);
                }

                this.orderItemModal = false;
            });
        },
        // 删除订单项
        deleteOrderItem(index) {

        }
    }
};
</script>

<style lang="scss">
.order {
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
