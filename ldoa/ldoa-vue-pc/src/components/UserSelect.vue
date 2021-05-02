<!-- eslint-disable vue/no-parsing-error -->
<!--
功能: 用户择弹窗

属性:
visible: 是否可见，可使用 v-model 双向绑定

事件:
on-ok: 点击确定时触发，参数为选择的用户
on-visible-change: 显示或隐藏时触发，显示时参数为 true，隐藏时为 false

案例:
<UserSelect v-model="visible"/>
-->

<template>
    <Modal :value="visible" title="用户选择" :mask-closable="false" transfer width="700" class="user-select-modal" @on-visible-change="showEvent">
        <!-- 弹窗 Body -->
        <div class="list-page">
            <!-- 搜索条件 -->
            <div class="list-page-toolbar-top">
                <Input v-model="filter.nickname" placeholder="请输入用户名称" @on-enter="searchUsers">
                    <span slot="prepend">用户名称</span>
                </Input>
                <Input v-model="filter.username" placeholder="请输入用户账号" search enter-button @on-enter="searchUsers">
                    <span slot="prepend">用户账号</span>
                </Input>
            </div>

            <!-- 物料列表 -->
            <Table :data="users" :columns="columns" :loading="reloading" border>
                <!-- 用户角色 -->
                <template slot-scope="{ row: user }" slot="roles">
                    <Tag v-for="roleValue in user.roles" :key="roleValue" color="cyan">{{ roleValue | roleName }}</Tag>
                </template>

                <!-- 操作按钮 -->
                <template slot-scope="{ row: user, index }" slot="action">
                    <Checkbox :value="user.userId === userSelected.userId" @on-change="selectItem(index, $event)"></Checkbox>
                </template>
            </Table>
        </div>

        <!-- 底部工具栏 -->
        <div slot="footer">
            <Button v-show="more" :loading="loading" icon="md-boat" style="float: left" @click="fetchMoreUsers">更多...</Button>
            <Button type="text" @click="showEvent(false)">取消</Button>
            <Button type="primary" @click="ok">确定</Button>
        </div>
    </Modal>
</template>

<script>
import UserDao from '@/../public/static-p/js/dao/UserDao';

export default {
    props: {
        visible: { type: Boolean, required: true }, // 是否可见
    },
    model: {
        prop : 'visible',
        event: 'on-visible-change',
    },
    data() {
        return {
            users: [],
            userSelected: {}, // 选中的用户
            filter: this.newFilter(),
            more     : false, // 是否还有更多用户
            loading  : false, // 加载中
            reloading: false, // 重新加载中
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { slot: 'action',   title: '选择', width: 70, align: 'center' },
                { key : 'nickname', title: '用户名称' },
                { key : 'username', title: '用户账号', width: 170 },
                { slot: 'roles',    title: '角色', width: 170 },
            ],
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
            if (!this.userSelected.userId) {
                this.$Message.warning('请选择用户');
                return;
            }

            this.$emit('on-ok', this.userSelected);
            this.showEvent(false); // 关闭弹窗
        },
        // 初始化
        init() {
            this.userSelected = {};
            this.searchUsers();
        },
        // 搜索物料
        searchUsers() {
            this.users     = [];
            this.more      = false;
            this.reloading = true;
            this.filter    = { ...this.newFilter(), username: this.filter.username, nickname: this.filter.nickname };

            this.fetchMoreUsers();
        },
        // 点击更多按钮加载下一页的物料
        fetchMoreUsers() {
            this.loading = true;

            UserDao.findUsers(this.filter).then(users => {
                this.users.push(...users);

                this.more      = users.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
        // 选择用户
        selectItem(index, selected) {
            if (selected) {
                this.userSelected = this.users[index];
            } else {
                this.userSelected = {};
            }
        },
        // 搜索条件
        newFilter() {
            return {
                username  : '',
                nickname  : '',
                pageSize  : 10,
                pageNumber: 1,
            };
        }
    }
};
</script>

<style lang="scss">
.user-select-modal {
    .list-page-toolbar-top {
        grid-template-columns: 1fr 1fr;
        grid-gap: 10px;
    }

    .ivu-checkbox-wrapper {
        margin-right: 0;
    }
}
</style>
