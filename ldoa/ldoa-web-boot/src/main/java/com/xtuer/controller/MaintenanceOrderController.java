package com.xtuer.controller;

import com.xtuer.bean.Page;
import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.bean.User;
import com.xtuer.bean.order.MaintenanceOrder;
import com.xtuer.mapper.MaintenanceOrderMapper;
import com.xtuer.service.MaintenanceOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 维保订单控制器
 */
@RestController
public class MaintenanceOrderController extends BaseController {
    @Autowired
    private MaintenanceOrderMapper orderMapper;

    @Autowired
    private MaintenanceOrderService orderService;

    /**
     * 查询符合条件的维保订单
     *
     * 网址: http://localhost:8080/api/maintenance-orders
     * 参数:
     *      pageNumber [可选]: 页码
     *      pageSize   [可选]: 数量
     *
     * @param page 分页
     * @return payload 为维保订单的数组
     */
    @GetMapping(Urls.API_MAINTENANCE_ORDERS)
    public Result<List<MaintenanceOrder>> findMaintenanceOrders(Page page) {
        return Result.ok(orderMapper.findMaintenanceOrders(page));
    }

    /**
     * 插入或者更新维保订单
     *
     * 网址: http://localhost:8080/api/maintenance-orders/{orderId}
     * 参数:
     *
     * @param order 维保订单
     * @return payload 为更新后的维保订单
     */
    @PutMapping(Urls.API_MAINTENANCE_ORDERS_BY_ID)
    Result<MaintenanceOrder> upsertMaintenanceOrder(MaintenanceOrder order) {
        User servicePerson = super.getCurrentUser();
        return orderService.upsertMaintenanceOrder(order, servicePerson);
    }
}
