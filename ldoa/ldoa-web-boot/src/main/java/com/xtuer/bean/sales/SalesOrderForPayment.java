package com.xtuer.bean.sales;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 销售订单提取出的支付订单信息
 */
@Getter
@Setter
public class SalesOrderForPayment {
    /**
     * 订单编号
     */
    @Excel(name = "订单编号", width = 24, orderNum = "1")
    private String salesOrderSn;

    /**
     * 客户
     */
    @Excel(name = "客户", width = 40, orderNum = "2")
    private String customerName;

    /**
     * 主题
     */
    @Excel(name = "客户", width = 20, orderNum = "3")
    private String topic;

    /**
     * 行业
     */
    @Excel(name = "行业", width = 20, orderNum = "4")
    private String business;

    /**
     * 签约日期
     */
    @Excel(name = "签约日期", width = 20, orderNum = "5", exportFormat = "yyyy-MM-dd")
    private Date agreementDate;

    /**
     * 净销售额
     */
    @Excel(name = "净销售额", width = 20, orderNum = "6")
    private double costDealAmount;

    /**
     * 总成交金额
     */
    @Excel(name = "总成交金额", width = 20, orderNum = "7")
    private double dealAmount;

    /**
     * 应收金额
     */
    @Excel(name = "应收金额", width = 20, orderNum = "8")
    private double shouldPayAmount;

    /**
     * 已收金额
     */
    @Excel(name = "已收金额", width = 20, orderNum = "9")
    private double paidAmount;

    /**
     * 收款时间
     */
    @Excel(name = "收款时间", width = 20, orderNum = "10", exportFormat = "yyyy-MM-dd")
    private Date paidAt;

    /**
     * 负责人
     */
    @Excel(name = "负责人", width = 20, orderNum = "11")
    private String ownerName;

    /**
     * 状态: 0 (初始化), 1 (待支付), 2 (已支付), 3 (完成)
     */
    @Excel(name = "状态", width = 20, orderNum = "12", replace = {"初始化_0", "待支付_1", "已支付_2", "完成_3"})
    private int state;
}
