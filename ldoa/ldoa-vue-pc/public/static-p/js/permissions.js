/*
维保订单:
    生产部生产维保人员有编辑、删除，修改的权限
    处理进度那个地方，只能发起订单的人员编辑修改
    销售人员只有查看和导出的权限


审批配置和用户管理：
    只能是超级管理员有修改、编辑、删除权限，其他人只能查看
    每个人若要修改自己的手机号和密码的话，从个人中心修改就可以

物料入库:
    人员只能是生产部质量保证和生产部调度
    其他人只能查看物料入库和出库的情况

物料管理和产品管理: 每个人都可以添加，但是每个人只能编辑和删除自己添加的东西

共享文件部分也是每个人都可以上传，下载，增加编辑和删除功能，但是每个人只能编辑和删除自己上传的文件
*/

/**
 * 是否有维保订单的权限
 *
 * @return 有权限返回 true，否则返回 false
 */
const hasPermissionForMaintenance = function() {
    const roles = this.$store.getters.roles;
    return roles.indexOf('ROLE_PRODUCE_MAINTENANCE') > -1 || roles.indexOf('ROLE_PRODUCE_SCHEDULE') > -1;
};

/**
 * 是否有超级管理员的权限
 *
 * @return 有权限返回 true，否则返回 false
 */
const hasPermissionOfSuperAdmin = function() {
    const roles = this.$store.getters.roles;
    return roles.indexOf('ROLE_ADMIN_SYSTEM') > -1;
};

/**
 * 是否有物料入库的权限
 *
 * @return 有权限返回 true，否则返回 false
 */
const hasPermissionForStockIn = function() {
    const roles = this.$store.getters.roles;
    return roles.indexOf('ROLE_PRODUCE_QUALITY') > -1 || roles.indexOf('ROLE_PRODUCE_SCHEDULE') > -1;
};

export default {
    hasPermissionForMaintenance,
    hasPermissionOfSuperAdmin,
    hasPermissionForStockIn,
};
