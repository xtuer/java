package com.xtuer.bean.stock;

import lombok.Getter;
import lombok.Setter;

/**
 * 库存查询的过滤器
 */
@Getter
@Setter
public class StockFilter {
    /**
     * 物料名称
     */
    private String name;

    /**
     * 物料编码
     */
    private String code;

    /**
     * 入库批次
     */
    private String batch;

    /**
     * 数量
     */
    private int count;
}
