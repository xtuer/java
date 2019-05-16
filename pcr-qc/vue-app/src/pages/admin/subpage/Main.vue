<template>
    <div class="main-layout">
        <!-- [1] :active-name 为当前高亮的 MenuItem 的 name -->
        <!-- [2] :open-names 为要展开的 Submenu 的 name 的数组 -->
        <!-- [3] @on-select 为点击 MenuItem 后的回调，参数为 MenuItem 的 name，一般设置为要跳转的路由名字 -->
        <Sider :style="{position: 'fixed', height: '100vh', left: 0, overflow: 'auto'}">
            <Menu :active-name="activeName" :open-names="['1', '2', '3', '4', '5']" theme="dark" width="auto" @on-select="navigateTo">
                <Submenu name="1">
                    <template slot="title">
                        <Icon type="ios-boat"></Icon>产品追踪
                    </template>
                    <MenuItem name="bd">产品追踪</MenuItem>
                </Submenu>
                <Submenu name="2">
                    <template slot="title">
                        <Icon type="ios-basket"></Icon>库存备件
                    </template>
                    <MenuItem name="chip-store">备件入库</MenuItem>
                    <MenuItem name="2-2">备件出库</MenuItem>
                    <MenuItem name="2-2">库存备件</MenuItem>
                </Submenu>
                <Submenu name="3">
                    <template slot="title">
                        <Icon type="ios-cart"></Icon>生产订单
                    </template>
                    <MenuItem name="3-1">新建生产订单</MenuItem>
                    <MenuItem name="3-2">生产进程管理</MenuItem>
                </Submenu>
                <Submenu name="4">
                    <template slot="title">
                        <Icon type="ios-construct"></Icon>维修订单
                    </template>
                    <MenuItem name="4-1">新建维修订单</MenuItem>
                    <MenuItem name="4-2">维修进程管理</MenuItem>
                </Submenu>
                <Submenu name="5">
                    <template slot="title">
                        <Icon type="ios-analytics"></Icon>数据分析
                    </template>
                    <MenuItem name="5-1">XXX 数据分析</MenuItem>
                </Submenu>
            </Menu>
        </Sider>
        <Layout :style="{marginLeft: '200px'}">
            <Header class="header">
                <span/><b>管理员</b><Avatar src="/static/img/avatar.jpg"/>
            </Header>
            <Content :style="{padding: '0 16px 16px'}">
                <!-- <Breadcrumb :style="{margin: '16px 0'}">
                    <BreadcrumbItem>Home</BreadcrumbItem>
                    <BreadcrumbItem>Components</BreadcrumbItem>
                    <BreadcrumbItem>Layout</BreadcrumbItem>
                </Breadcrumb> -->
                <Card shadow :style="{margin: '16px 0'}">
                    <div style="min-height: 600px">
                        <router-view />
                    </div>
                </Card>
            </Content>
        </Layout>
    </div>
</template>

<script>
export default {
    data() {
        return {
            activeName: 'bd',
        };
    },
    mounted() {
        this.activeName = this.$route.name;
    },
    methods: {
        navigateTo(name) {
            this.$router.push({ name: name });
        },
        goBack() {
            this.$router.back();
        }
    }
};
</script>

<style lang="scss">
.main-layout {
    background: #f5f7f9;
    position: relative;
    border-radius: 4px;
    overflow: hidden;

    .ivu-layout-header {
        box-shadow: 0px 0px 5px 0px rgba(0, 0, 0, 0.0980392) !important;
    }

    .header {
        display: grid;
        grid-template-columns: 1fr max-content max-content;
        grid-gap: 6px;
        align-items: center;
        padding: 0 10px;
        background: #fff;
        box-shadow: 0 2px 3px 2px rgba(0, 0, 0, .1);
    }
}
</style>
