<!--
功能: 维保订单详情

属性:
visible: 是否可见，可使用 v-model 双向绑定
maintenace-order-id: 维保订单 ID

事件:
on-ok: 点击完成订单时触发，参数为订单 ID
on-visible-change: 显示或隐藏时触发，显示时参数为 true，隐藏时为 false

案例:
<MaintenanceOrderDetails v-model="visible" :maintenace-order-id="maintenaceOrderId"/>
-->

<template>
    <Modal :value="visible" :title="title" class="maintenance-order-details-modal" width="900"
           :styles="{ top: '40px', marginBottom: '80px' }" @on-visible-change="showEvent">
        <!-- 弹窗 Body -->
        <table class="order-table relative">
            <Spin v-if="loading" fix size="large"></Spin>

            <tr>
                <td class="td-label text-color-gray">客户:</td>
                <td class="td-rest">{{ order.customerName }}</td>

                <td class="td-label text-color-gray">销售人员:</td>
                <td class="td-rest">{{ order.salespersonName }}</td>

                <td class="td-label text-color-gray">类型:</td>
                <td class="td-rest">{{ orderType }}</td>
            </tr>

            <!-- 产品 -->
            <tr>
                <td class="td-label text-color-gray">产品名称:</td>
                <td class="td-rest">{{ order.productName }}</td>

                <td class="td-label text-color-gray">产品编码:</td>
                <td class="td-rest">{{ order.productCode }}</td>

                <td class="td-label text-color-gray">规格/型号:</td>
                <td class="td-rest">{{ order.productModel }}</td>
            </tr>

            <!-- 物料 -->
            <tr>
                <td class="td-label text-color-gray">物料名称:</td>
                <td class="td-rest">{{ order.productItemName }}</td>

                <td class="td-label text-color-gray">物料批次:</td>
                <td class="td-rest">{{ order.productItemBatch }}</td>

                <td class="td-label text-color-gray">物料数量:</td>
                <td class="td-rest">{{ order.productItemCount }}</td>
            </tr>
            <tr>
                <td class="td-label text-color-gray">配件:</td>
                <td class="td-rest" colspan="5">{{ order.accessories }}</td>
            </tr>

            <!-- 其他 -->
            <tr>
                <td class="td-label text-color-gray">收货日期:</td>
                <td class="td-rest">{{ order.receivedDate | formatDateSimple }}</td>

                <td class="td-label text-color-gray">订单号:</td>
                <td class="td-rest">{{ order.orderSn }}</td>

                <td class="td-label text-color-gray">需要证书:</td>
                <td class="td-rest">{{ order.needCertificate ? '是' : '否' }}</td>
            </tr>
            <tr>
                <td class="td-label text-color-gray" style="vertical-align: top">反馈的问题:</td>
                <td class="td-rest" colspan="5">
                    <div>{{ order.problem }}</div>
                    <div style="text-align: right; margin-top: 10px">
                        {{ order.servicePersonName }} / {{ order.createdAt | formatDateSimple }}
                    </div>
                </td>
            </tr>

            <tr>
                <td colspan="6" class="text-align-center background-gray">审批</td>
            </tr>

            <!-- 审批信息 -->
            <tr v-for="step in audit.steps" :key="step.step">
                <td colspan="6">
                    <AuditStep :step="step"/>
                </td>
            </tr>
        </table>

        <!-- 底部工具栏 -->
        <div slot="footer">
            <Button v-if="canCompleteOrder" :loading="saving" type="primary" @click="completeOrder">完成订单</Button>
        </div>
    </Modal>
</template>

<script>
import MaintenanceOrderDao from '@/../public/static-p/js/dao/MaintenanceOrderDao';
import AuditDao from '@/../public/static-p/js/dao/AuditDao';
import AuditStep from '@/components/AuditStep.vue';

export default {
    props: {
        visible: { type: Boolean, required: true }, // 是否可见
        maintenaceOrderId: { type: String,  required: true }, // 维保订单 ID
    },
    model: {
        prop : 'visible',
        event: 'on-visible-change',
    },
    components: { AuditStep },
    data() {
        return {
            audit: {}, // 审批
            order: {}, // 维保订单
            loading: false, // 加载中
            saving : false,
        };
    },
    computed: {
        // 标题
        title() {
            return `维保订单: ${this.order.maintenanceOrderSn}`;
        },
        // 订单类型
        orderType() {
            const ns = [];

            if (this.order.maintainable) {
                ns.push('维修');
            }
            if (this.order.repairable) {
                ns.push('保养');
            }

            return ns.join(', ');
        },
        // 是否可以完成订单
        canCompleteOrder() {
            // 当前状态为 3 且其销售员为当前登陆用户
            // order.state === 3; // "初始化", "审批中", "审批拒绝", "审批通过", "完成"
            if (this.order.state === 3 && this.isCurrentUser(this.order.servicePersonId)) {
                return true;
            } else {
                return false;
            }
        }
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
            this.$emit('on-ok', []);
            this.showEvent(false); // 关闭弹窗
        },
        // 初始化
        init() {
            if (!Utils.isValidId(this.maintenaceOrderId)) {
                return;
            }
            this.loading = true;

            Promise.all([
                MaintenanceOrderDao.findMaintenanceOrderById(this.maintenaceOrderId),
                AuditDao.findAuditOfTarget(this.maintenaceOrderId)
            ]).then(([order, audit]) => {
                this.order = order;
                this.audit = audit;
                this.loading = false;
            }).catch(error => {
                this.loading = false;
                console.error(error);
            });
        },
        // 完成订单
        completeOrder() {
            this.saving = true;
            MaintenanceOrderDao.completeOrder(this.maintenaceOrderId).then(() => {
                this.$Message.success('订单完成');
                this.saving = false;
                this.$emit('on-ok', this.maintenaceOrderId);
                this.showEvent(false); // 关闭弹窗
            }).catch(() => {
                this.saving = false;
            });
        }
    }
};
</script>

<style lang="scss">
.maintenance-order-details-modal {
    .order-table {
        border-collapse: collapse;
        width: 100%;

        .td-label {
            width    : 100px;
            min-width: 100px;
            max-width: 100px;
            text-align: right;
        }

        .td-rest {
            /* 先去掉设置 min-width 的 td 占用的空间 */
            /* 有 rest-td class 的 td 从左边 td 开始占用 table 剩余空间的 50% */
            width: 33%;
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
