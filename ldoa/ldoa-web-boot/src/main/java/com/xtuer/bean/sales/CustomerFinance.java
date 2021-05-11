package com.xtuer.bean.sales;

import lombok.Getter;
import lombok.Setter;

/**
 * 客户的财务信息: 累计订单金额、累计应收款、累计已收款
 */
@Getter
@Setter
public class CustomerFinance {
    /**
     * 客户 ID
     */
    private long customerId;

    /**
     * 累计订单金额
     */
    private double totalDealAmount;

    /**
     * 累计应收款
     */
    private double totalShouldPayAmount;

    /**
     * 累计已收款
     */
    private double totalPaidAmount;
}
