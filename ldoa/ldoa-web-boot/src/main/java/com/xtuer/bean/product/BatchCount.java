package com.xtuer.bean.product;

import lombok.Getter;
import lombok.Setter;

/**
 * 物料的批次和数量
 */
@Getter
@Setter
public class BatchCount {
    /**
     * 物料所属产品 ID
     */
    private long productId;

    /**
     * 物料 ID
     */
    private long productItemId;

    /**
     * 物料名称
     */
    private String productItemName;

    /**
     * 入库批次
     */
    private String batch;

    /**
     * 数量，例如出库需要的数量
     */
    private int count;

    /**
     * 批次的库存数量
     */
    private int stockBatchCount;
}
