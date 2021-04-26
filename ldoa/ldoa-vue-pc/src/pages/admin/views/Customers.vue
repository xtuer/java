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
                <Input v-model="filter.nickname" placeholder="请输入查询条件">
                    <span slot="prepend">姓甚名谁</span>
                </Input>

                <!-- 时间范围 -->
                <DatePicker v-model="dateRange"
                            format="MM-dd"
                            separator=" 至 "
                            type="daterange"
                            data-prepend="创建时间"
                            class="date-picker"
                            split-panels
                            placeholder="请选择创建时间范围">
                </DatePicker>

                <!-- 选择条件的搜索 -->
                <Input v-model="filterValue" transfer placeholder="请输入查询条件" search enter-button @on-search="searchCustomers">
                    <Select v-model="filterKey" slot="prepend">
                        <Option value="email">邮件地址</Option>
                        <Option value="phone">电话号码</Option>
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
        <Table :data="customers" :columns="columns" :loading="reloading" border>
            <!-- 介绍信息 -->
            <template slot-scope="{ row: customer }" slot="info">
                {{ customer.userId }}
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: customer }" slot="action">
                <Button type="primary" size="small">编辑</Button>
                <Button type="error" size="small">删除</Button>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreCustomers">更多...</Button>
        </div>
    </div>
</template>

<script>
import SalesDao from '@/../public/static-p/js/dao/SalesDao';
import FileUpload from '@/components/FileUpload.vue';

export default {
    components: { FileUpload },
    data() {
        return {
            customers : [],
            filter: { // 搜索条件
                nickname  : '',
                pageSize  : 2,
                pageNumber: 1,
            },
            filterKey  : 'email',  // 搜索的 Key
            filterValue: '',       // 搜索的 Value
            dateRange  : ['', ''], // 搜索的时间范围
            more     : false, // 是否还有更多客户
            loading  : false, // 加载中
            reloading: false,
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'nickname', title: '名字', width: 150 },
                { slot: 'info',   title: '介绍', minWidth: 500 },
                { slot: 'action', title: '操作', width: 150, align: 'center', className: 'table-action' },
            ]
        };
    },
    mounted() {
        // this.searchCustomers();
    },
    methods: {
        // 搜索客户
        searchCustomers() {
            this.customers             = [];
            this.more              = false;
            this.reloading         = true;
            this.filter.pageNumber = 1;

            // 如果不需要时间范围，则删除
            if (this.dateRange[0] && this.dateRange[1]) {
                this.filter.startAt = this.dateRange[0].format('yyyy-MM-dd');
                this.filter.endAt   = this.dateRange[1].format('yyyy-MM-dd');
            } else {
                this.filter.startAt = '';
                this.filter.endAt   = '';
            }

            this.fetchMoreCustomers();
        },
        // 点击更多按钮加载下一页的客户
        fetchMoreCustomers() {
            this.loading = true;

            UserDao.findCustomers(this.filter).then(customers => {
                this.customers.push(...customers);

                this.more      = customers.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
        // 导入客户
        exportCustomers(file) {
            SalesDao.importCustomers(file.url).then(() => {
                this.$Message.success('导入成功');
            });
        }
    }
};
</script>

<style lang="scss">
</style>
