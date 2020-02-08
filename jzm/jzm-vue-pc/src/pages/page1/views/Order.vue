<template>
    <div class="order">
        <!-- 搜索工具栏 -->
        <div class="toolbar">
            <Button @click="editOrder(-1)">创建订单</Button>
        </div>

        <!-- 显示订单 -->
        <Table :data="orders" :columns="orderColumns">
            <template slot-scope="{ row: order }" slot="orderDate">
                {{ order.orderDate | formatDate }}
            </template>
        </Table>

        <!-- 加载下一页订单按钮 -->
        <Button :loading="loading" v-show="more" @click="fetchMoreOrders">更多...</Button>

        <!-- 编辑订单对话框 -->
        <Modal v-model="orderModal"
                :loading="loading"
                :mask-closable="false"
                title="编辑订单"
                width="600"
                class="edit-order-modal"
                @on-ok="saveOrder">
            <Form :model="editedOrder" :label-width="80" class="order-form">
                <FormItem label="客户名称:">
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
                <FormItem label="软件版本:">
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
                    <Button type="dashed" icon="md-add" style="float: right">添加订单项</Button>
                </FormItem>
            </Form>
        </Modal>

        <!-- 编辑订单项对话框 -->
        <Modal v-model="orderItemModal"
                :mask-closable="false"
                title="编辑订单项"
                width="500"
                class="edit-order-item-modal"
                @on-ok="saveOrderItem">
            <Form :model="editedOrderItem" :label-width="90" class="order-form">
                <FormItem label="产品型号:">
                    <Select v-model="editedOrderItem.type">
                        <Option v-for="type in ORDER_ITEM_TYPES" :value="type" :key="type">{{ type }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="产品序列号:">
                    <Input v-model="editedOrderItem.sn" placeholder="请输入产品序列号"/>
                </FormItem>
                <FormItem label="芯片编号:">
                    <Input v-model="editedOrderItem.chipSn" placeholder="请输入芯片编号"/>
                </FormItem>
                <FormItem label="外壳颜色:">
                    <Input v-model="editedOrderItem.shellOrder" placeholder="请输入外壳颜色"/>
                </FormItem>
                <FormItem label="外壳批次:">
                    <Input v-model="editedOrderItem.shellBatch" placeholder="请输入外壳批次"/>
                </FormItem>
                <FormItem label="传感器信息:">
                    <Input v-model="editedOrderItem.sensorInfo" placeholder="请输入传感器信息"/>
                </FormItem>
                <FormItem label="Ο 型圈信息:">
                    <Input v-model="editedOrderItem.circleInfo" placeholder="请输入 Ο 型圈信息"/>
                </FormItem>
            </Form>
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
                { title: '客户名称', key: 'customerName' },
                { title: '生产进程', key: 'process' },
                { title: '订单类型', key: 'type' },
                { title: '品牌',    key: 'brand' },
                { title: '软件版本', key: 'softwareVersion' },
                { title: '订单日期', slot: 'orderDate' },
                { title: '负责人',   key: 'personInCharge' },
            ],
            orderItemColumns: [],
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
        // 点击更多按钮加载下一页的用户
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
            // 1. 生成编辑订单的副本
            // 2. 保存编辑的订单下标
            // 3. 在弹窗对订单的副本进行编辑

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

                this.orderModal = false;
                this.loading = false;
                this.$Message.success('保存订单成功');
            });
        },
        // 删除订单
        deleteOrder() {

        },
        // 编辑订单项
        editOrderItem(index) {

        },
        // 保存订单项
        saveOrderItem() {

        },
        // 删除订单项
        deleteOrderItem() {

        }
    }
};
</script>

<style lang="scss">
.order {
    .toolbar {
        display: grid;
        grid-template-columns: max-content 200px 100px;
        grid-gap: 12px;
        align-items: center;
    }

}

.edit-order-modal {
    .order-form {
        display: grid;
        grid-template-columns: 1fr 1fr;
    }
}
</style>
