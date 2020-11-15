package com.xtuer.bean.stock;

import com.xtuer.util.Utils;

/**
 * 出库申请
 */
public class OutOfStock {
    private static final String[] STATUS_LABELS = { "初始化", "审批中", "审批拒绝", "审批通过" };

    /**
     * 出库申请 ID
     */
    private long outOfStockId;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 状态: 0 (初始化), 1 (待审批), 2 (审批拒绝), 3 (审批完成), 4 (完成)
     */
    private int status;

    /**
     * 获取订单状态 Label
     *
     * @return 返回订单状态的 Label
     */
    public String getStatusLabel() {
        return Utils.getStatusLabel(STATUS_LABELS, status);
    }
}
