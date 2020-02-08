package com.xtuer.mapper;

import com.xtuer.bean.Order;
import com.xtuer.bean.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单 Mapper，操作订单和订单项
 */
@Mapper
public interface OrderMapper {
    /**
     * 查找指定 ID 的订单
     *
     * @param orderId 订单 ID
     * @return 返回查询到的订单，查询不到返回 null
     */
    Order findOrderById(long orderId);

    /**
     * 查询符合条件的订单
     *
     * @return 返回订单的数组
     */
    List<Order> findOrders();

    /**
     * 插入或者更新订单
     *
     * @param order 订单
     */
    void upsertOrder(Order order);

    /**
     * 删除指定 ID 的订单 (同时会删除它的订单项)
     *
     * @param orderId 订单 ID
     */
    void deleteOrder(long orderId);

    /**
     * 插入或者更新订单项
     *
     * @param orderItem 订单项
     */
    void upsertOrderItem(OrderItem orderItem);

    /**
     * 删除指定 ID 的订单项
     *
     * @param orderItemId 订单项 ID
     */
    void deleteOrderItem(long orderItemId);
}
