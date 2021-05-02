package com.xtuer.mapper;

import com.xtuer.bean.sales.SalesOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 销售订单 Mapper
 */
@Mapper
public interface SalesOrderMapper {
    /**
     * 查询符合条件的销售订单
     *
     * @return 返回销售订单的数组
     */
    List<SalesOrder> findSalesOrders();

    /**
     * 更新或者插入销售订单
     *
     * @param order 销售订单
     */
    void upsertSalesOrder(SalesOrder order);
}
