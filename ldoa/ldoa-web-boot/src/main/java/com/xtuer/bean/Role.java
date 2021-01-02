package com.xtuer.bean;

/**
 * 用户角色
 */
public enum Role {
    ROLE_ADMIN_SYSTEM("系统管理员"),
    ROLE_ADMIN_ORG("机构管理员"),
    ROLE_SALESPERSON("销售"),
    ROLE_SERVICE("售后"),
    ROLE_CHECK("检验"),
    ROLE_BUYER("采购物流"),
    ROLE_PRODUCE("生产操作"),
    ROLE_SCHEDULE("生产调度"),
    ROLE_QUALITY("质量保证"),
    ROLE_SUPPORT("技术支持"),
    ROLE_DESIGN("设计工艺");

    // 角色的中文名字，方便记忆 (JSON 序列化的时候不会输出)
    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
