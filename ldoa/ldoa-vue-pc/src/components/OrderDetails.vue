<!--
功能: 订单详情弹窗

属性:
visible: 是否可见，可使用 v-model 双向绑定
order-id: 订单 ID

事件:
on-ok: 点击确定时触发，参数为订单 ID
on-visible-change: 显示或隐藏时触发，显示时参数为 true，隐藏时为 false

案例:
<OrderDetails v-model="visible" :order-id="orderId"/>
-->

<template>
    <Modal :value="visible" :title="'订单详情: ' + order.orderSn" width="800"
           :styles="{ top: '40px', marginBottom: '80px' }"
           class="order-details-modal" @on-visible-change="showEvent">
        <!-- 内容显示 -->
        <table class="order-table relative">
            <Spin v-if="loading" fix size="large"></Spin>

            <!-- 客户信息 -->
            <tr>
                <td>客户单位</td>
                <td colspan="4">{{ order.customerCompany }}</td>
            </tr>
            <tr>
                <td>客户联系人</td>
                <td colspan="5">{{ order.customerContact }}</td>
            </tr>
            <tr>
                <td>客户收件地址</td>
                <td colspan="4">{{ order.customerAddress }}</td>
            </tr>
            <tr>
                <td>销售负责人</td>
                <td colspan="4">{{ salesperson }}</td>
            </tr>

            <tr>
                <td colspan="5">
                    <div style="display: grid; grid-template-columns: max-content max-content 1fr; grid-gap: 40px">
                        <div>订单日期: {{ order.orderDate | formatDate }}</div>
                        <div>交货日期: {{ order.deliveryDate | formatDate }}</div>
                    </div>
                </td>
            </tr>

            <!-- 产品 -->
            <tr>
                <td>产品名称</td>
                <td>产品编码</td>
                <td>规格/型号</td>
                <td class="center">数量</td>
                <td>备注</td>
            </tr>
            <tr v-for="item in items" :key="item.productId">
                <td>{{ item.product && item.product.name }}</td>
                <td>{{ item.product && item.product.code }}</td>
                <td>{{ item.product && item.product.model }}</td>
                <td class="center">{{ item.count }}</td>
                <td>{{ item.comment }}</td>
            </tr>

            <!-- 校准 -->
            <tr>
                <td colspan="5">
                    <div>是否校准: {{ order.calibrated ? '是' : '否' }}</div>
                    <div style="margin-top: 20px">校准信息: {{ order.calibrationInfo || '无' }}</div>
                </td>
            </tr>

            <!-- 其他要求 -->
            <tr>
                <td colspan="5">
                    <div> 其他要求: {{ order.requirement || '无' }}</div>

                    <div style="margin-top: 20px">
                        订单附件:
                        <a v-if="attachment.id !== '0'" :href="attachment.url">{{ attachment.filename }}</a>
                        <span v-else>无</span>
                    </div>

                    <div class="sign">销售人员 ({{ salesperson }}) / {{ order.createdAt | formatDate }}</div>
                </td>
            </tr>

            <!-- 审批信息 -->
            <tr v-for="item in audit.items" :key="item.auditItemId">
                <td colspan="5">
                    <AuditItem :audit-item="item"/>
                </td>
            </tr>
        </table>

        <!-- 底部工具栏 -->
        <div slot="footer">
            <!-- <Button type="text" @click="showEvent(false)">取消</Button> -->
            <!-- <Button type="primary" @click="showEvent(false)">确定</Button> -->
            <Button v-if="auditPass" :loading="saving" type="primary" @click="completeOrder">完成订单</Button>
        </div>
    </Modal>
</template>

<script>
import OrderDao from '@/../public/static-p/js/dao/OrderDao';
import AuditDao from '@/../public/static-p/js/dao/AuditDao';
import AuditItem from '@/components/AuditItem.vue';

export default {
    props: {
        visible: { type: Boolean, required: true }, // 是否可见
        orderId: { type: String,  required: true }, // 订单 ID
    },
    model: {
        prop : 'visible',
        event: 'on-visible-change',
    },
    components: { AuditItem },
    data() {
        return {
            order: {}, // 订单
            audit: {}, // 审批
            loading  : false,
            saving   : false,
            auditPass: false, // 审批是否通过
        };
    },
    computed: {
        // 销售负责人
        salesperson() {
            if (this.order.salesperson) {
                return this.order.salesperson.nickname;
            } else {
                return '未知';
            }
        },
        // 订单项
        items() {
            if (this.order.items) {
                return this.order.items;
            } else {
                return [];
            }
        },
        // 订单附件
        attachment() {
            if (this.order.attachment && this.order.attachment.id !== '0') {
                return this.order.attachment;
            } else {
                return { id: '0' };
            }
        }
    },
    methods: {
        // 显示隐藏事件
        showEvent(visible) {
            this.$emit('on-visible-change', visible);

            // 显示时加载订单
            if (visible) {
                this.init();
            }
        },
        // 初始化
        init() {
            // 查询订单和订单的审批
            this.loading = true;

            Promise.all([
                OrderDao.findOrderById(this.orderId),
                AuditDao.findAuditOfTarget(this.orderId)
            ]).then(([order, audit]) => {
                this.order = order;
                this.audit = audit;
                this.auditPass = order.state === 3; // "初始化", "审批中", "审批拒绝", "审批通过", "完成"
                this.loading = false;
            }).catch(error => {
                this.loading = false;
                console.error(error);
            });
        },
        // 完成订单
        completeOrder() {
            this.saving = true;
            OrderDao.completeOrder(this.orderId).then(() => {
                this.$Message.success('订单完成');
                this.saving = false;
                this.$emit('on-ok', this.orderId);
                this.showEvent(false); // 关闭弹窗
            }).catch(() => {
                this.saving = false;
            });
        }
    }
};
</script>

<style lang="scss">
.order-details-modal {
    .order-table {
        border-collapse: collapse;
        width: 100%;

        td:nth-child(1), td:nth-child(2), td:nth-child(3) {
            width: 150px;
        }
        td:nth-child(4) {
            width: 70px;
        }

        td {
            border: 1px solid $borderColor;
            padding: 8px 12px;

            .gap {
                display: inline-block;
                width: 100px;
            }

            &.center {
                text-align: center;
            }
        }

        .audit-item .ivu-input-group {
            border-collapse: collapse;
        }
    }
}
</style>
