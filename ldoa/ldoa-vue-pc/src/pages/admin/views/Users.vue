<!--
搜索用户、分页加载 (加载下一页的用户)
-->
<template>
    <div class="users-manage list-page">
        <!-- 顶部工具栏 -->
        <div class="list-page-toolbar-top">
            <Input v-model="filter.nickname" search enter-button placeholder="请输入姓名" @on-search="searchUsers"/>
            <Button type="primary" icon="md-add" :disabled="!hasPermissionOfSuperAdmin()" @click="editUser()">添加用户</Button>
        </div>

        <!-- 用户列表 -->
        <Table :data="users" :columns="columns" :loading="reloading" border
            @on-column-width-resize="saveTableColumnWidths(arguments)"
        >
            <!-- 介绍信息 -->
            <template slot-scope="{ row: user }" slot="roles">
                <Tag v-for="roleValue in user.roles" :key="roleValue" color="cyan">{{ roleValue | roleName }}</Tag>
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: user }" slot="action">
                <template v-if="!isSystemAdmin(user)">
                    <Button type="primary" size="small" :disabled="!hasPermissionOfSuperAdmin()" @click="showChangeRoleModal(user)">修改权限</Button>
                    <Button type="primary" size="small" :disabled="!hasPermissionOfSuperAdmin()" @click="resetPassword(user)">重置密码</Button>
                    <Button type="error" size="small" :disabled="!hasPermissionOfSuperAdmin()" @click="deleteUser(user)">删除</Button>
                </template>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreUsers">更多...</Button>
        </div>

        <!-- 用户创建对话框 -->
        <Modal v-model="modal" :mask-closable="false" title="创建用户" class="create-user-modal" :styles="{ top: '60px', marginBottom: '40px' }">
            <Form ref="form" :model="userClone" :rules="userRules" :key="userClone.id" :label-width="80">
                <FormItem label="姓名:" prop="nickname">
                    <Input v-model="userClone.nickname" placeholder="请输入姓名"/>
                </FormItem>
                <FormItem label="账号:" prop="username">
                    <Input v-model="userClone.username" placeholder="请输入账号"/>
                </FormItem>
                <FormItem label="密码:" prop="password">
                    <Input v-model="userClone.password" placeholder="请输入密码"/>
                </FormItem>
                <FormItem label="权限:" prop="roleValue">
                    <Select v-model="userClone.roleValue">
                        <Option v-for="role in roles" :value="role.value" :key="role.value">{{ role.name }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="手机:">
                    <Input v-model="userClone.mobile" placeholder="请输入手机"/>
                </FormItem>
            </Form>

            <div slot="footer">
                <Button type="text" @click="modal = false">取消</Button>
                <Button type="primary" :loading="saving" @click="saveUser">保存</Button>
            </div>
        </Modal>

        <!-- 修改权限弹窗 -->
        <Modal v-model="roleModal" :mask-closable="false" title="修改权限" class="change-role-modal">
            <Select v-model="userClone.roleValue">
                <Option v-for="role in roles" :value="role.value" :key="role.value">{{ role.name }}</Option>
            </Select>
            <div slot="footer">
                <Button type="text" @click="roleModal = false">取消</Button>
                <Button type="primary" :loading="saving" @click="changeRole">保存</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import UserDao from '@/../public/static-p/js/dao/UserDao';

export default {
    data() {
        return {
            users : [],
            filter: { // 搜索条件
                nickname  : '',
                pageSize  : 100,
                pageNumber: 1,
            },
            more     : false, // 是否还有更多用户
            loading  : false, // 加载中
            reloading: false,
            tableName: 'users-table', // 表名
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'nickname', title: '姓名', width: 150, resizable: true },
                { key : 'username', title: '账号', width: 150, sortable: true, resizable: true },
                { key : 'mobile',   title: '手机', width: 150, resizable: true },
                { slot: 'roles',    title: '权限', sortable: true },
                { slot: 'action',   title: '操作', width: 240, align: 'center', className: 'table-action', resizable: true },
            ],

            userClone: {},    // 用于编辑的用户
            modal    : false, // 是否显示弹窗
            saving   : false, // 保存中
            roleModal: false, // 修改用户权限弹窗
            userRules: {
                username: [
                    { required: true, whitespace: true, message: '账号不能为空', trigger: 'blur' }
                ],
                nickname: [
                    { required: true, whitespace: true, message: '姓名不能为空', trigger: 'blur' }
                ],
                password: [
                    { required: true, whitespace: true, message: '密码不能为空', trigger: 'blur' }
                ],
                roleValue: [
                    { required: true, whitespace: true, message: '角色不能为空', trigger: 'blur' }
                ],
            },
            roles: window.ROLES.filter(role => role.value !== 'ROLE_ADMIN_SYSTEM'), // 创建用户的角色去掉系统管理员
        };
    },
    mounted() {
        this.restoreTableColumnWidths(this.columns);
        this.searchUsers();
    },
    methods: {
        // 搜索用户
        searchUsers() {
            this.users             = [];
            this.more              = false;
            this.reloading         = true;
            this.filter.pageNumber = 1;

            this.fetchMoreUsers();
        },
        // 点击更多按钮加载下一页的用户
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
        // 编辑用户: user 为 undefined 表示创建，否则表示更新
        editUser(user) {
            // 1. 重置表单，避免上一次的验证信息影响到本次编辑
            // 2. 生成编辑对象的副本
            // 3. 显示编辑对话框

            this.$refs.form.resetFields();

            if (user) {
                // 更新
                this.userClone = Utils.clone(user); // 重要: 克隆对象
            } else {
                // 创建
                this.userClone = this.newUser();
            }

            this.modal = true;
        },
        // 保存编辑后的用户
        saveUser() {
            // 1. 表单验证不通过则返回
            // 2. 克隆被编辑对象
            // 3. 找到被编辑对象的下标
            // 4. 保存成功后如果是更新则替换已有对象，创建则添加到最前面
            // 5. 提示保存成功，隐藏编辑对话框

            this.$refs.form.validate(valid => {
                if (!valid) { return; }

                // [2] 克隆被编辑对象
                // [3] 找到被编辑对象的下标
                this.saving = true;
                const user  = Utils.clone(this.userClone);  // 重要: 克隆被编辑的对象
                const index = this.users.findIndex(u => u.userId === user.userId); // 用户下标

                // 添加角色
                user.roles = [user.roleValue];

                UserDao.createUser(user).then((newUser) => {
                    // [4] 保存成功后如果是更新则替换已有对象，创建则添加到最前面
                    //     有时服务器保存后会返回 user 对象，例如给 user 分配 ID 等以及修改其他属性，
                    //     这时应该添加服务器返回的对象到 users
                    if (index >= 0) {
                        // 更新: 替换已有对象
                        this.users.splice(index, 1, newUser);
                    } else {
                        // 创建: 添加到最后面
                        this.users.push(newUser);
                    }

                    // [5] 提示保存成功，隐藏编辑对话框
                    this.saving = false;
                    this.modal  = false;
                    this.$Message.success('保存成功');
                }).catch(() => {
                    this.saving = false;
                });
            });
        },
        // 重置用户的密码
        resetPassword(user) {
            this.$Modal.confirm({
                title: `确定重置 <font color="red">${user.nickname}</font> 的密码吗?`,
                loading: true,
                onOk: () => {
                    UserDao.resetPassword(user.userId).then(() => {
                        this.$Modal.remove();
                        this.$Message.success('重置密码成功');
                    });
                }
            });
        },
        // 删除用户
        deleteUser(user) {
            // 1. 删除提示
            // 2. 从服务器删除成功后才从本地删除
            // 3. 提示删除成功

            this.$Modal.confirm({
                title: `确定删除 <font color="red">${user.nickname}</font> 吗?`,
                loading: true,
                onOk: () => {
                    UserDao.deleteUser(user.userId).then(() => {
                        const index = this.users.findIndex(u => u.userId === user.userId); // 用户下标
                        this.users.splice(index, 1); // [2] 从服务器删除成功后才从本地删除
                        this.$Modal.remove();
                        this.$Message.success('删除成功');
                    });
                }
            });
        },
        // 显示修改权限弹窗
        showChangeRoleModal(user) {
            this.userClone = Utils.clone(user); // 重要: 克隆对象
            this.roleModal = true;
        },
        // 修改用户的权限
        changeRole() {
            // 1. 校验权限不能为空
            // 2. 确认修改
            // 3. 修改成功后更新列表中用户的权限，关闭弹窗

            // [1] 校验权限不能为空
            if (!this.userClone.roleValue) {
                this.$Message.error('请选择权限');
                return;
            }

            const user = this.userClone;

            // [2] 确认修改
            this.$Modal.confirm({
                title: `确定修改用户 <font color="red">${user.nickname} </font> 的权限吗?`,
                loading: true,
                onOk: () => {
                    UserDao.patchUser({ userId: user.userId, role: user.roleValue }).then(() => {
                        const found = this.users.find(u => u.userId === user.userId);
                        if (found) {
                            found.roles = [user.roleValue];
                        }
                        this.$Modal.remove();
                        this.roleModal = false;
                    });
                }
            });
        },
        // 创建新用户对象
        newUser() {
            return {
                username : '',
                nickname : '',
                password : '',
                roleValue: '',
            };
        }
    }
};
</script>

<style lang="scss">
.users-manage {
    .list-page-toolbar-top .ivu-input-wrapper {
        width: 300px;
    }
}
</style>
