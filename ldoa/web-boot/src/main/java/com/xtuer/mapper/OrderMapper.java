package com.xtuer.mapper;

import com.xtuer.bean.Order;
import com.xtuer.bean.OrderItem;
import com.xtuer.bean.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单的 Mapper
 */
@Mapper
public interface OrderMapper {
    /**
     * 查询指定 ID 的订单
     *
     * @param orderId 订单 ID
     * @return 返回查询到的订单，查询不到返回 null
     */
    Order findOrderById(long orderId);

    /**
     * 查询符合条件的订单
     *
     * @param filter 过滤器
     * @param page   分页对象
     * @return 返回查询到的订单，查询不到返回空 List
     */
    List<Order> findOrders(Order filter, Page page);

    /**
     * 插入或者更新订单
     *
     * @param order 订单
     */
    void upsertOrder(Order order);

    /**
     * 插入订单项
     *
     * @param items 订单项
     */
    void insertOrderItems(List<OrderItem> items);

    /**
     * 删除订单的订单项
     *
     * @param orderId 订单 ID
     */
    void deleteOrderItems(long orderId);
}