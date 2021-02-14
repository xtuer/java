package com.xtuer.bean.stock;

import com.xtuer.bean.product.BatchCount;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * 接收出库请求的对象
 */
@Getter
@Setter
public class StockOutRequestFormBean {
    /**
     * 订单 ID
     */
    private long orderId;

    /**
     * 物料出库的批次和数量
     */
    private List<BatchCount> batchCounts = new LinkedList<>();

    /**
     * 当前审批员 ID
     */
    private long currentAuditorId;
}
