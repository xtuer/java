package com.xtuer.bean.stock;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 物料的库存
 */
@Getter
@Setter
@Accessors(chain = true)
public class Stock {
    /**
     * 物料 ID
     */
    private long productItemId;

    /**
     * 数量
     */
    private int count;

    /**
     * 库存操作类型
     */
    public enum OpType {
        IN, OUT
    }
}
