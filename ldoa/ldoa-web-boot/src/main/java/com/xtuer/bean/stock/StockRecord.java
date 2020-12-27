package com.xtuer.bean.stock;

import com.xtuer.bean.product.ProductItem;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

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
     * 产品 ID (当订单物料出库时需要)
     */
    private long productId;

    /**
     * 物料 ID
     */
    private long productItemId;

    /**
     * 物料的类型: 成品、零件、部件
     */
    private String productItemType;

    /**
     * 库存操作类型: IN (入库), OUT (出库)
     */
    private Type type;

    /**
     * 数量
     */
    private int count;

    /**
     * 批次
     */
    private String batch;

    /**
     * 厂家
     */
    private String manufacturer;

    /**
     * 备注
     */
    private String comment;

    /**
     * 操作是否完成
     * A. 入库直接标记为完成
     * B. 出库申请提交后创建出库记录，当领取物料后才标记为完成
     */
    private boolean complete;

    /**
     * 库存操作申请 ID (出库时才需要)
     */
    private long stockRequestId;

    /**
     * 出库申请 SN，显示时方便归类查看
     */
    private String stockRequestSn;

    /**
     * 操作用户 ID
     */
    private long userId;

    /**
     * 操作员的名字
     */
    private String username;

    /**
     * 入库日期
     */
    private Date createdAt;

    /**
     * 物料
     */
    private ProductItem productItem;

    /**
     * 库存操作类型
     */
    public enum Type {
        IN, OUT
    }
}
