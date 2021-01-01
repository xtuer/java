package com.xtuer.bean.order;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 查询订单的过滤器
 */
@Getter
@Setter
@Accessors(chain = true)
public class OrderFilter {
    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 订单的产品编码，使用逗号分隔，方便搜索
     */
    private String productCodes;

    /**
     * 状态: 0 (初始化), 1 (待审批), 2 (审批拒绝), 3 (审批完成), 4 (完成)
     */
    private int state;

    /**
     * 是否在出库请求中有记录
     */
    private boolean notInStockRequest;
}
