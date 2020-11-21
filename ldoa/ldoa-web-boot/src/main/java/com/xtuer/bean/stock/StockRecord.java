package com.xtuer.bean.stock;

import com.xtuer.bean.ProductItem;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 库存操作记录 (出库、入库)
 */
@Getter
@Setter
@Accessors(chain = true)
public class StockRecord {
    /**
     * 库存操作记录 ID
     */
    private long stockRecordId;

    /**
     * 物料 ID
     */
    private long productItemId;

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
     * 操作是否完成
     * A. 入库直接标记为完成
     * B. 出库申请提交后创建出库记录，当领取物料后才标记为完成
     */
    private boolean completed;

    /**
     * 出库申请 ID (出库时才需要)
     */
    private long stockOutRequestId;

    /**
     * 操作用户 ID
     */
    private userId;

    /**
     * 物料
     */
    private ProductItem productItem;
}
