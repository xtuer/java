<!-- eslint-disable vue/no-parsing-error -->
<!--
搜索物料、分页加载 (加载下一页的物料)
-->
<template>
    <div class="product-item-manage list-page">
        <!-- 顶部工具栏 -->
        <div class="list-page-toolbar-top">
            <div style="display: flex">
                <Input v-model="filter.name" placeholder="请输入物料名称">
                    <span slot="prepend">物料名称</span>
                </Input>
                <Input v-model="filter.code" placeholder="请输入物料编码">
                    <span slot="prepend">物料编码</span>
                </Input>
                <Button type="primary" icon="ios-search" @click="searchProductItems">搜索</Button>
            </div>

            <Button type="primary" icon="md-add">添加物料</Button>
        </div>

        <!-- 物料列表 -->
        <Table :data="productItems" :columns="columns" :loading="reloading" border>
            <!-- 操作按钮 -->
            <template slot-scope="{ row: productItem }" slot="action">
                <Button type="primary" size="small" style="margin-right: 5px">编辑</Button>
                <Button type="error" size="small">删除</Button>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreProductItems">更多...</Button>
        </div>
    </div>
</template>

<script>
import ProductDao from '@/../public/static-p/js/dao/ProductDao';

export default {
    data() {
        return {
            productItems : [],
            filter: { // 搜索条件
                name      : '',
                code      : '',
                pageSize  : 2,
                pageNumber: 1,
            },
            more     : false, // 是否还有更多物料
            loading  : false, // 加载中
            reloading: false,
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'name',   title: '物料名称', width: 200 },
                { key : 'code',   title: '物料编码', width: 130 },
                { key : 'type',   title: '物料类型', width: 130 },
                { key : 'model',   title: '规格/型号', width: 130 },
                { key : 'standard',   title: '标准/规范', width: 130 },
                { key : 'material',   title: '材质', width: 130 },
                { key : 'desc',   title: '物料描述', minWidth: 150 },
                { slot: 'action', title: '操作', width: 150, align: 'center' },
            ]
        };
    },
    mounted() {
        this.searchProductItems();
    },
    methods: {
        // 搜索物料
        searchProductItems() {
            this.productItems             = [];
            this.more              = false;
            this.reloading         = true;
            this.filter.pageNumber = 1;

            this.fetchMoreProductItems();
        },
        // 点击更多按钮加载下一页的物料
        fetchMoreProductItems() {
            this.loading = true;

            ProductDao.findProductItems(this.filter).then(productItems => {
                this.productItems.push(...productItems);

                this.more      = productItems.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
    }
};
</script>

<style lang="scss">
.product-item-manage {
    .list-page-toolbar-top {
        .ivu-input-wrapper {
            width: 250px;
            margin-right: 10px;
        }
    }
}
</style>
