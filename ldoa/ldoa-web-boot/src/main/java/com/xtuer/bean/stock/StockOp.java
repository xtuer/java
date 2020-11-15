package com.xtuer.bean.stock;

import com.xtuer.bean.ProductItem;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 库存操作
 */
@Getter
@Setter
@Accessors(chain = true)
public class StockOp {
    /**
     * 出库申请 ID (出库时才需要)
     */
    private long outOfStockId;

    /**
     * 库存操作记录 ID
     */
    private long stockRecordId;

    /**
     * 库存操作类型: false (入库), true (出库)
     */
    private boolean type;

    /**
     * 数量
     */
    private int count;

    /**
     * 批次
     */
    private String batch;

    /**
     * 仓库
     */
    private String warehouse;

    /**
     * 备注
     */
    private String comment;

    /**
     * 物料 ID
     */
    private long productItemId;

    /**
     * 物料
     */
    private ProductItem productItem = new ProductItem();
}
