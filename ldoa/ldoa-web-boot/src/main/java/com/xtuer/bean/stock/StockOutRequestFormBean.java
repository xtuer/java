package com.xtuer.bean.stock;

import com.xtuer.bean.product.ProductItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 接收出库请求的对象
 */
@Getter
@Setter
public class StockOutRequestFormBean {
    /**
     * 订单 ID
     */
    private long orderId;

    /**
     * 产品项 (物料)
     */
    private List<ProductItem> productItems;
}
