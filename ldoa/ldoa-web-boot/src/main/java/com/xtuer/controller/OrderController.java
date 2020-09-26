package com.xtuer.controller;

import com.xtuer.bean.Order;
import com.xtuer.bean.Page;
import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单的控制器
 */
@RestController
public class OrderController extends BaseController {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 查询指定 ID 的订单
     *
     * 网址: http://localhost:8080/api/orders/{orderId}
     * 参数: 无
     *
     * @param orderId 订单 ID
     * @return payload 为查询到的订单，查询不到返回 null
     */
    @GetMapping(Urls.API_ORDERS_BY_ID)
    public Result<Order> findOrderById(@PathVariable long orderId) {
        return Result.single(orderMapper.findOrderById(orderId), "订单不存在: " + orderId);
    }

    /**
     * 查询符合条件的订单
     *
     * 网址: http://localhost:8080/api/orders
     * 参数:
     *      orderSn      [可选]: 订单编号
     *      productCodes [可选]: 产品编号
     *      status       [可选]: 状态
     *      pageNumber   [可选]: 页码
     *      pageSize     [可选]: 数量
     *
     * @param filter 过滤器
     * @param page   分页对象
     * @return payload 为订单数组
     */
    @GetMapping(Urls.API_ORDERS)
    public Result<List<Order>> findOrders(Order filter, Page page) {
        return Result.ok(orderMapper.findOrders(filter, page));
    }
}
