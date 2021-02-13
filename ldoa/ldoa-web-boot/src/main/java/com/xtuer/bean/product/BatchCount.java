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
     * 物料 ID
     */
    private long productItemId;

    /**
     * 入库批次
     */
    private String batch;

    /**
     * 数量
     */
    private int count;
}
