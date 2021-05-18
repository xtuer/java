package com.xtuer.bean.sales;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
    @Excel(name = "姓名", width = 10)
    private String name;

    /**
     * 部门
     */
    @Excel(name = "部门", width =30)
    private String department;

    /**
     * 手机号
     */
    @Excel(name = "手机号", width = 20)
    private String phone;
}
