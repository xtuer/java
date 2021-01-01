package com.xtuer.bean.stock;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 库存操作申请过滤器
 */
@Getter
@Setter
public class StockRequestFilter {
    /**
     * 库存操作类型: IN (入库), OUT (出库)
     */
    private StockRecord.Type type;

    /**
     * 出库申请 SN
     */
    private String stockRequestSn;

    /**
     * 申请者的 ID
     */
    private long applicantId;

    /**
     * 状态, 为 -1 时表示查询所有的
     */
    private int state;

    /**
     * 过滤条件: 开始时间
     */
    private Date startAt;

    /**
     * 过滤条件: 结束时间
     */
    private Date endAt;
}
