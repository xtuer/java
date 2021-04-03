// 权限判断

/**
 * 是否有维保订单的权限
 */
const hasPermissionForMaintenance = function() {
    console.log(this.$store.getters.roles);
};

export default {
    hasPermissionForMaintenance
};
