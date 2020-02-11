<!-- 页面布局 -->
<template>
    <div class="home">
        <!-- Header -->
        <Header/>

        <!-- 左侧侧边栏 -->
        <div class="sidebar">
            <Menu :active-name="activeName" :open-names="['1', '2', '3']" width="auto" @on-select="navigateTo">
                <!-- <MenuItem v-for="item in menuItems" :key="item.name" :name="item.name">{{ item.label }}</MenuItem> -->
                <Submenu name="1">
                    <template slot="title"><Icon type="logo-snapchat"/>订单管理</template>
                    <MenuItem name="orders">订单管理</MenuItem>
                </Submenu>
                <Submenu name="2">
                    <template slot="title"><Icon type="ios-basket"/>库存备件</template>
                    <MenuItem name="spares">备件管理</MenuItem>
                    <MenuItem name="warehousingLogs">库存跟踪</MenuItem>
                </Submenu>
                <Submenu name="3">
                    <template slot="title"><Icon type="ios-construct"/>维修管理</template>
                    <MenuItem name="workOrders">维修订单</MenuItem>
                </Submenu>
            </Menu>
        </div>

        <!-- 内容显示区 -->
        <div class="content">
            <PerfectScrollbar class="content-wrapper">
                <router-view/>
            </PerfectScrollbar>
        </div>
    </div>
</template>

<script>
import Header from '@/components/Header.vue';

export default {
    components: {
        Header,
    },
    data() {
        return {
            activeName: '',
            menuItems: [ // 所有菜单项，每个菜单项有不同的权限
                // { label: '订单管理', name: 'orders'              },
                // { label: '备件管理', name: 'spares'              },
                // { label: '库存跟踪', name: 'warehousingLogs'      },
                // { label: '任务管理', name: 'scroll'              },
                // { label: '绩效查询', name: 'performance',        },
                // { label: '问题类型', name: 'question-types',     },
                // { label: '用户管理', name: 'admin-users',        },
            ],
        };
    },
    mounted() {
        this.activeName = this.$route.name;
    },
    methods: {
        navigateTo(name) {
            this.$router.push({ name });
        },
    },
    watch: {
        // 监听路由变化时高亮对应的菜单项
        '$route'(to, from) {
            if (this.menuItems.some(item => item.name === to.name)) {
                this.activeName = to.name;
            }
        }
    }
};
</script>

<style lang="scss">
.home {
    display: grid;
    grid-template-columns: 180px 1fr;
    grid-template-rows: max-content 1fr;
    min-height: 100%;

    > .header {
        grid-column: span 2;
        box-shadow: 0 0px 15px #ccc;
        z-index: 1000;
    }

    > .sidebar {
        // 隐藏 Menu 右边框
        .ivu-menu-vertical.ivu-menu-light:after {
            display: none;
        }
    }

    > .content {
        display: grid;
        background: #eceef8;
        padding: 24px;

        > .content-wrapper {
            padding: 18px;
            background: white;
            border-radius: 4px;
        }
    }
}
</style>
