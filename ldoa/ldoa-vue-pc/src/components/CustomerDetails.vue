<!--
功能: 客户信息弹窗

属性:
visible: 是否可见，可使用 v-model 双向绑定
customer-id: 客户 ID

事件:
on-visible-change: 显示或隐藏时触发，显示时参数为 true，隐藏时为 false

案例:
<CustomerDetails v-model="visible" :customer-id="projectId"/>
-->

<template>
    <Modal :value="visible" title="客户信息" width="700"
        :styles="{ top: '40px', marginBottom: '80px' }"
        class="customer-details-modal relative" @on-visible-change="showEvent"
    >
        <Spin v-if="loading" fix size="large"></Spin>

        <div class="title-x margin-bottom-5">基本信息</div>
        <table class="customer-table">
            <tr>
                <td class="text-color-gray">客户:</td>
                <td>{{ customer.name }}</td>

                <td class="text-color-gray">编号:</td>
                <td>{{ customer.customerSn }}</td>
            </tr>

            <tr>
                <td class="text-color-gray">行业:</td>
                <td>{{ customer.business }}</td>

                <td class="text-color-gray">区域:</td>
                <td>{{ customer.region }}</td>
            </tr>

            <tr>
                <td class="text-color-gray">电话:</td>
                <td>{{ customer.phone }}</td>

                <td class="text-color-gray">负责人:</td>
                <td>{{ customer.owner }}</td>
            </tr>

            <tr>
                <td class="text-color-gray">地址:</td>
                <td colspan="3">{{ customer.address }}</td>
            </tr>

            <tr>
                <td class="text-color-gray">备注:</td>
                <td colspan="3">{{ customer.remark }}</td>
            </tr>
        </table>

        <div class="title-x margin-top-20 margin-bottom-5">联系人</div>
        <table class="contact-table">
            <tr>
                <th>姓名</th>
                <th>部门</th>
                <th>手机号</th>
            </tr>
            <tr v-for="(contact, index) in contacts" :key="index">
                <td>{{ contact.name }}</td>
                <td>{{ contact.department }}</td>
                <td>{{ contact.phone }}</td>
            </tr>
            <tr v-if="contacts.length === 0">
                <td colspan="3" class="text-align-center text-color-gray">无</td>
            </tr>
        </table>

        <div class="title-x margin-top-20 margin-bottom-5">财务信息</div>
        <table class="finance-table">
            <tr>
                <th>累计订单金额</th>
                <th>累计应收款</th>
                <th>累计已收款</th>
            </tr>
            <tr>
                <td>{{ customer.totalDealAmount }}</td>
                <td>{{ customer.totalShouldPayAmount }}</td>
                <td>{{ customer.totalPaidAmount }}</td>
            </tr>
        </table>

        <!-- 底部工具栏 -->
        <div slot="footer">
            <!-- <Button type="text" @click="showEvent(false)">取消</Button>
            <Button type="primary" @click="ok">确定</Button> -->
        </div>
    </Modal>
</template>

<script>
import CustomerDao from '@/../public/static-p/js/dao/CustomerDao';
import SalesOrderDao from '@/../public/static-p/js/dao/SalesOrderDao';

export default {
    props: {
        visible   : { type: Boolean, required: true }, // 是否可见
        customerId: { type: String,  required: true }, // 客户 ID
    },
    model: {
        prop : 'visible',
        event: 'on-visible-change',
    },
    data() {
        return {
            customer: {},
            loading: false,
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
        // 点击确定按钮的回调函数
        ok() {
            this.$emit('on-ok', []);
            this.showEvent(false); // 关闭弹窗
        },
        // 初始化
        init() {
            // 例如从服务器加载数据
            this.loading = true;
            Promise.all([
                CustomerDao.findCustomerById(this.customerId),
                SalesOrderDao.findCustomerFinance(this.customerId)
            ]).then(([customer, finance]) => {
                this.customer = customer;
                this.customer.totalDealAmount = finance.totalDealAmount;
                this.customer.totalShouldPayAmount = finance.totalShouldPayAmount;
                this.customer.totalPaidAmount = finance.totalPaidAmount;
                this.loading = false;
            });
        }
    },
    computed: {
        // 客户联系人
        contacts() {
            return this.customer.contacts || [];
        }
    }
};
</script>

<style lang="scss">
.customer-details-modal {
    .customer-table, .contact-table, .finance-table {
        border-collapse: collapse;
        width: 100%;

        td:nth-child(odd) {
            width: 80px;
            min-width: 80px;
            max-width: 80px;
        }
        td:nth-child(even) {
            width: 50%;
        }

        td, th {
            border: 1px solid $borderColor;
            padding: 8px 12px;
        }
    }

    .contact-table, .finance-table {
        table-layout: fixed;
    }
}
</style>
