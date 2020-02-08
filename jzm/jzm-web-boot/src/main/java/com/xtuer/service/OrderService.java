package com.xtuer.service;

import com.xtuer.bean.Order;
import com.xtuer.mapper.OrderMapper;
import com.xtuer.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单服务
 */
@Service
@Slf4j
public class OrderService extends BaseService {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 插入或者更新订单
     *
     * @param order 订单
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void upsertOrder(Order order) {
        // 1. 校正订单的 ID
        // 2. 保存订单
        // 3. 设置订单项的订单 ID
        // 4. 校正订单项的 ID
        // 5. 删除标记 deleted 为 true 的订单
        // 6. 保存订单项

        // [1] 校正订单的 ID
        if (Utils.isInvalidId(order.getId())) {
            order.setId(super.nextId());
        }

        log.info("[开始] 保存订单 {}", order.getId());

        // [2] 保存订单
        orderMapper.upsertOrder(order);

        order.getOrderItems().forEach(orderItem -> {
            // [3] 设置订单项的订单 ID
            orderItem.setOrderId(order.getId());

            // [4] 校正订单项的 ID
            if (Utils.isInvalidId(orderItem.getId())) {
                orderItem.setId(super.nextId());
            }

            if (orderItem.isDeleted()) {
                // [5] 删除标记 deleted 为 true 的订单
                orderMapper.deleteOrderItem(orderItem.getId());
                log.info("[进行] 删除订单项 {}", orderItem.getId());
            } else {
                // [6] 保存订单项
                orderMapper.upsertOrderItem(orderItem);
                log.info("[进行] 保存订单项 {}", orderItem.getId());
            }
        });

        log.info("[完成] 保存订单 {}", order.getId());
    }
}
