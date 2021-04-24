package com.xtuer.bean;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

/**
 * 客户
 */
@Getter
@Setter
public class Customer {
    /**
     * 客户 ID
     */
    private long customerId;

    /**
     * 客户编码: 唯一
     */
    @Excel(name = "编号")
    private String customerSn;

    /**
     * 客户名称
     */
    @Excel(name = "名称")
    private String name;

    /**
     * 行业
     */
    @Excel(name = "行业")
    private String business;

    /**
     * 区域
     */
    @Excel(name = "区域")
    private String region;

    /**
     * 分类
     */
    @Excel(name = "分类")
    private String type;

    /**
     * 电话
     */
    @Excel(name = "电话")
    private String phone;

    /**
     * 关系等级
     */
    @Excel(name = "关系等级")
    private String level;

    /**
     * 重要程度
     */
    @Excel(name = "重要程度")
    private String importance;

    /**
     * 状态
     */
    @Excel(name = "状态")
    private String status;
}
