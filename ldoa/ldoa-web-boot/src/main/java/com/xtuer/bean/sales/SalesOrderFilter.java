package com.xtuer.bean.sales;

import lombok.Getter;
import lombok.Setter;

/**
 * 销售订单过滤条件
 */
@Getter
@Setter
public class SalesOrderFilter {
    /**
     * 客户
     */
    private String customerName;

    /**
     * 主题
     */
    private String topic;

    /**
     * 行业
     */
    private String business;
}
