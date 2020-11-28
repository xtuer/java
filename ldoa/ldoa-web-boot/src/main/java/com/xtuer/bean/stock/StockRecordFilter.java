package com.xtuer.bean.stock;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 查询库存记录的过滤器
 */
@Getter
@Setter
@Accessors(chain = true)
public class StockRecordFilter {
    /**
     * 库存操作类型: IN (入库), OUT (出库)
     */
    private StockRecord.Type type;

    /**
     * 物料名称
     */
    private String name;

    /**
     * 物料编码
     */
    private String code;

    /**
     * 批次
     */
    private String batch;

    /**
     * 规格型号
     */
    private String model;

    /**
     * 厂家
     */
    private String manufacturer;

    /**
     * 过滤条件: 开始时间
     */
    private Date startAt;

    /**
     * 过滤条件: 结束时间
     */
    private Date endAt;
}
