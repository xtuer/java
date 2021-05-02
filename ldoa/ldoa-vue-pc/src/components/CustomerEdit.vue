<!--
功能: 客户编辑

属性:
visible: 是否可见，可使用 v-model 双向绑定
customer-id: 客户 ID

事件:
on-ok: 点击确定时触发，参数为客户
on-visible-change: 显示或隐藏时触发，显示时参数为 true，隐藏时为 false

案例:
<CustomerEdit v-model="visible" :customer-id="customerId"/>
-->

<template>
    <Modal :value="visible" title="客户编辑" :mask-closable="false" class="edit-customer-modal relative"
        :width="700" :styles="{ top: '40px', marginBottom: '80px' }"
        @on-visible-change="showEvent">
        <!-- 弹窗 Body -->
        <Spin v-if="loading" fix size="large"></Spin>

        <div class="box">
            <div class="title">基本信息</div>
            <div class="content" style="padding-left: 0; padding-right: 0">
                <Form ref="customerForm" :model="customer" :rules="customerRules" :key="customer.customerId" :label-width="80" class="column-2">
                    <FormItem label="名字:" prop="name">
                        <Input v-model="customer.name" placeholder="请输入名称"/>
                    </FormItem>
                    <FormItem label="编号:" prop="customerSn">
                        <Input v-model="customer.customerSn" placeholder="请输入编号"/>
                    </FormItem>
                    <FormItem label="行业:" prop="business">
                        <Input v-model="customer.business" placeholder="请输入行业"/>
                    </FormItem>
                    <FormItem label="区域:" prop="region">
                        <Input v-model="customer.region" placeholder="请输入区域"/>
                    </FormItem>
                    <FormItem label="电话:" prop="phone">
                        <Input v-model="customer.phone" placeholder="请输入电话"/>
                    </FormItem>
                    <FormItem label="负责人:" prop="owner">
                        <Input v-model="customer.owner" placeholder="请输入负责人"/>
                    </FormItem>
                    <FormItem label="地址:" prop="address" class="grid-span-2">
                        <Input v-model="customer.address" placeholder="请输入地址"/>
                    </FormItem>
                    <FormItem label="备注:" prop="remark" class="grid-span-2">
                        <Input v-model="customer.remark" placeholder="请输入备注"/>
                    </FormItem>
                </Form>
            </div>
        </div>

        <div class="box">
            <div class="title">
                联系人
                <Icon type="md-add-circle" class="clickable margin-left-10" size="18" @click="addContact"/>
            </div>
            <div class="content">
                <Form ref="contactsForm" :key="customer.customerId" :label-width="80" class="contact">
                    <template v-for="(contact, index) in customer.contacts">
                        <FormItem label="姓名:" prop="name" :key="contact.id + '-1'">
                            <Input v-model="contact.name" placeholder="请输入姓名"/>
                        </FormItem>
                        <FormItem label="部门:" prop="department" :key="contact.id + '-2'">
                            <Input v-model="contact.department" placeholder="请输入部门"/>
                        </FormItem>
                        <FormItem label="手机号:" prop="phone" :key="contact.id + '-3'">
                            <Input v-model="contact.phone" placeholder="请输入手机号"/>
                        </FormItem>

                        <FormItem :key="contact.id + '-5'" class="form-item-icon">
                            <Icon type="md-close" class="clickable" size="16" @click="removeContact(index)"/>
                        </FormItem>
                    </template>
                </Form>
            </div>
        </div>

        <!-- 底部工具栏 -->
        <div slot="footer">
            <Button type="text" @click="showEvent(false)">取消</Button>
            <Button type="primary" :loading="saving" @click="save(customer)">确定</Button>
        </div>
    </Modal>
</template>

<script>
import CustomerDao from '@/../public/static-p/js/dao/CustomerDao';

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
            customer     : this.newCustomer(), // 用于编辑的客户
            saving       : false,
            loading      : false,
            customerRules: {
                name: [
                    { required: true, whitespace: true, message: '名称不能为空', trigger: 'blur' }
                ],
                customerSn: [
                    { required: true, whitespace: true, message: '编号不能为空', trigger: 'blur' }
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
            // 例如从服务器加载数据
            if (this.customerId === '0') {
                this.customer = this.newCustomer();
            } else {
                this.loading = true;
                CustomerDao.findCustomerById(this.customerId).then(customer => {
                    this.customer = customer;
                    this.loading = false;
                });
            }

            this.$refs.customerForm.resetFields();
        },
        // 添加新联系人
        addContact() {
            this.customer.contacts.push(this.newContact());
        },
        // 删除联系人
        removeContact(index) {
            this.customer.contacts.remove(index);
        },
        // 保存客户
        save(customer) {
            // 表单验证
            this.$refs.customerForm.validate(valid => {
                if (!valid) { return; }

                this.saving = true;
                CustomerDao.upsertCustomer(customer).then((newCustomer) => {
                    this.$emit('on-ok', newCustomer);
                    this.showEvent(false); // 关闭弹窗
                    this.saving = false;
                }).catch(() => {
                    this.saving = false;
                });
            });
        },
        // 新客户
        newCustomer() {
            return {
                customerId: '0',
                customerSn: '', // 客户编号
                name      : '', // 客户名称
                business  : '', // 行业
                region    : '', // 区域
                phone     : '', // 电话
                address   : '', // 地址
                owner     : '', // 负责人
                remark    : '', // 备注
                contacts  : [this.newContact()], // 联系人
            };
        },
        // 新客户联系人
        newContact() {
            return { id: Utils.nextId(), name: '', department: '', phone: '' };
        }
    }
};
</script>

<style lang="scss">
.edit-customer-modal {
    .box {
        border: none;

        .title {
            display: flex;
            align-items: center;
        }

        .content {
            padding-left: 0;
            padding-right: 0;
        }
    }

    .contact {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr 30px;
    }

    .form-item-icon .ivu-form-item-content {
        margin-left: 10px !important;
    }
}
</style>
