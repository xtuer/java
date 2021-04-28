package com.xtuer.bean.sales;

import lombok.Getter;
import lombok.Setter;

/**
 * 客户联系人
 */
@Getter
@Setter
public class CustomerContact {
    /**
     * 姓名
     */
    private String name;

    /**
     * 部门
     */
    private String department;

    /**
     * 手机号
     */
    private String phone;
}
