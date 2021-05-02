package com.xtuer.mapper;

import com.xtuer.bean.sales.SalesOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售订单 Mapper
 */
@Mapper
public interface SalesOrderMapper {
    /**
     * 更新或者插入销售订单
     *
     * @param order 销售订单
     */
    void upsertSalesOrder(SalesOrder order);
}
