package com.xtuer.bean.order;

import com.xtuer.bean.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 订单项
 */
@Getter
@Setter
@Accessors(chain = true)
public class OrderItem {
    /**
     * 订单项 ID
     */
    private long orderItemId;

    /**
     * 所属订单 ID
     */
    private long orderId;

    /**
     * 产品 ID
     */
    private long productId;

    /**
     * 产品数量
     */
    private int count;

    /**
     * 备注
     */
    private String comment;

    /**
     * 订单项的产品，每个产品项对应一个产品
     */
    private Product product;
}
