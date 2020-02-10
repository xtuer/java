package com.xtuer.controller;

import com.xtuer.bean.Order;
import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.mapper.OrderMapper;
import com.xtuer.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 查找指定 ID 的订单
     *
     * 网址: http://localhost:8080/api/orders/{orderId}
     * 参数: 无
     *
     * @param orderId 订单 ID
     * @return payload 为查询到的订单，查询不到时为 null
     */
    @GetMapping(Urls.API_ORDERS_BY_ID)
    public Result<Order> findOrderById(@PathVariable long orderId) {
        Order order = orderMapper.findOrderById(orderId);
        return Result.single(order, "查找不到订单 " + orderId);
    }

    /**
     * 查询符合条件的订单
     *
     * 网址: http://localhost:8080/api/orders
     * 参数: 无
     *
     * @return payload 为订单数组
     */
    @GetMapping(Urls.API_ORDERS)
    public Result<List<Order>> findOrders() {
        List<Order> orders = orderMapper.findOrders();
        return Result.ok(orders);
    }

    /**
     * 插入或者更新订单
     *
     * 网址: http://localhost:8080/api/orders/{orderId}
     * 参数: 无
     * 请求体: JSON 格式的订单
     *
     * @param order 订单
     * @return Payload 为订单
     */
    @PutMapping(Urls.API_ORDERS_BY_ID)
    public Result<Order> upsertOrder(@RequestBody Order order) {
        orderService.upsertOrder(order);
        return Result.ok(order);
    }

    /**
     * 删除指定 ID 的订单
     *
     * 网址: http://localhost:8080/api/orders/{orderId}
     * 参数: 无
     *
     * @param orderId 订单 ID
     */
    @DeleteMapping(Urls.API_ORDERS_BY_ID)
    public Result<Boolean> deleteOrder(@PathVariable long orderId) {
        orderMapper.deleteOrder(orderId);
        return Result.ok();
    }

    /**
     * 删除指定 ID 的订单项
     *
     * 网址: http://localhost:8080/api/orderItems/{orderItemId}
     * 参数: 无
     *
     * @param orderItemId 订单项 ID
     */
    @DeleteMapping(Urls.API_ORDERS_ITEM_BY_ID)
    public Result<Boolean> deleteOrderItem(@PathVariable long orderItemId) {
        orderMapper.deleteOrderItem(orderItemId);
        return Result.ok();
    }

    /**
     * 更新订单
     *
     * 网址: http://localhost:8080/api/orders/{orderId}
     * 参数:
     *      status [可选]: 订单状态
     *
     * @param orderId 订单 ID
     * @param status  订单状态
     */
    @PatchMapping(Urls.API_ORDERS_BY_ID)
    public Result<Boolean> patchOrder(@PathVariable long orderId,
                                      @RequestParam(required = false, defaultValue="-1") int status) {
        if (status != -1) {
            orderMapper.updateOrderStatus(orderId, status);
        }

        return Result.ok();
    }
}
