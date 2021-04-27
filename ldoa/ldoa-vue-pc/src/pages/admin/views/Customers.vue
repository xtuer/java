<!-- eslint-disable vue/no-parsing-error -->

<!--
搜索客户、分页加载 (加载下一页的客户)
-->
<template>
    <div class="customers list-page">
        <!-- 顶部工具栏 -->
        <div class="list-page-toolbar-top">
            <!-- 搜索条件 -->
            <div class="filter">
                <!-- 指定条件的搜索 -->
                <Input v-model="filter.name" placeholder="请输入查询条件">
                    <span slot="prepend">客户名称</span>
                </Input>

                <!-- 选择条件的搜索 -->
                <Input v-model="filterValue" transfer placeholder="请输入查询条件" search enter-button @on-search="searchCustomers">
                    <Select v-model="filterKey" slot="prepend">
                        <Option value="customerSn">编号</Option>
                        <Option value="business">行业</Option>
                    </Select>
                </Input>
            </div>

            <!-- 其他按钮 -->
            <div>
                <Button type="default" icon="md-add">添加客户</Button>
                <FileUpload class="margin-left-10" excel @on-success="exportCustomers">导入客户</FileUpload>
            </div>
        </div>

        <!-- 客户列表 -->
        <Table :data="customers" :columns="columns" :loading="reloading" border @on-column-width-resize="saveTableColumnWidths(arguments)">
            <!-- 操作按钮 -->
            <template slot-scope="{ row: customer }" slot="action">
                <Button type="primary" size="small">编辑</Button>
                <Button type="error" size="small" @click="deleteCustomer(customer)">删除</Button>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreCustomers">更多...</Button>
        </div>
    </div>
</template>

<script>
import CustomerDao from '@/../public/static-p/js/dao/CustomerDao';
import FileUpload from '@/components/FileUpload.vue';

export default {
    components: { FileUpload },
    data() {
        return {
            customers : [],
            filter: this.newFilter(),
            filterKey  : 'customerSn', // 搜索的 Key
            filterValue: '',           // 搜索的 Value
            more     : false, // 是否还有更多客户
            loading  : false, // 加载中
            reloading: false,
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'customerSn', title: '客户编号', width: 150, resizable: true },
                { key : 'name',       title: '客户名称', width: 150, resizable: true },
                { key : 'business',   title: '行业', width: 150, resizable: true },
                { key : 'region',     title: '区域', width: 150, resizable: true },
                { key : 'phone',      title: '电话', width: 150, resizable: true },
                { key : 'address',    title: '地址', width: 150, resizable: true },
                { key : 'owner',      title: '负责人', width: 150, resizable: true },
                { key : 'remark',     title: '备注', width: 150, resizable: true },
                { slot: 'action',     title: '操作', width: 150, align: 'center', className: 'table-action' },
            ]
        };
    },
    mounted() {
        this.restoreTableColumnWidths(this.columns);
        this.searchCustomers();
    },
    methods: {
        // 搜索客户
        searchCustomers() {
            this.customers              = [];
            this.more                   = false;
            this.reloading              = true;
            this.filter                 = { ...this.newFilter(), name: this.filter.name };
            this.filter[this.filterKey] = this.filterValue;

            this.fetchMoreCustomers();
        },
        // 点击更多按钮加载下一页的客户
        fetchMoreCustomers() {
            this.loading = true;

            CustomerDao.findCustomers(this.filter).then(customers => {
                this.customers.push(...customers);

                this.more      = customers.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
        // 导入客户
        exportCustomers(file) {
            CustomerDao.importCustomers(file.url).then(() => {
                this.$Message.success('导入成功');
                this.searchCustomers();
            });
        },
        // 删除客户
        deleteCustomer(customer) {
            // 1. 删除提示
            // 2. 从服务器删除成功后才从本地删除
            // 3. 提示删除成功

            this.$Modal.confirm({
                title: `确定删除客户 <font color="red">${customer.name}</font> 吗?`,
                loading: true,
                onOk: () => {
                    CustomerDao.deleteCustomer(customer.customerId).then(() => {
                        const index = this.customers.findIndex(c => c.customerId === customer.customerId); // 客户下标
                        this.customers.splice(index, 1); // [2] 从服务器删除成功后才从本地删除
                        this.$Modal.remove();
                        this.$Message.success('删除成功');
                    });
                }
            });
        },
        // 新建搜索条件
        newFilter() {
            return { // 搜索条件
                // customerSn : '',
                // business: '',
                name      : '',
                pageSize  : 50,
                pageNumber: 1,
            };
        },
    }
};
</script>

<style lang="scss">
</style>
