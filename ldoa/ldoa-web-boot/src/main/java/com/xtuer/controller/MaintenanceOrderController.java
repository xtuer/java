package com.xtuer.controller;

import com.xtuer.bean.Page;
import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.bean.User;
import com.xtuer.bean.order.MaintenanceOrder;
import com.xtuer.bean.order.MaintenanceOrderFilter;
import com.xtuer.bean.order.Order;
import com.xtuer.mapper.MaintenanceOrderMapper;
import com.xtuer.service.MaintenanceOrderService;
import com.xtuer.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     *      state              [可选]: 状态，为 -1 则查询所有
     *      maintenanceOrderSn [可选]: 维保单号
     *      salespersonName    [可选]: 销售人员
     *      customerName       [可选]: 客户
     *      productName        [可选]: 产品名称
     *      receivedStartAt    [可选]: 收货开始时间
     *      receivedEndAt      [可选]: 收货结束时间
     *      pageNumber         [可选]: 页码
     *      pageSize           [可选]: 数量
     *
     * @param filter 过滤条件
     * @param page 分页
     * @return payload 为维保订单的数组
     */
    @GetMapping(Urls.API_MAINTENANCE_ORDERS)
    public Result<List<MaintenanceOrder>> findMaintenanceOrders(MaintenanceOrderFilter filter, Page page) {
        // 设置查询时间范围
        filter.setReceivedStartAt(Utils.startOfDay(filter.getReceivedStartAt()));
        filter.setReceivedEndAt(Utils.endOfDay(filter.getReceivedEndAt()));

        return Result.ok(orderMapper.findMaintenanceOrders(filter, page));
    }

    /**
     * 查询指定 ID 的维保订单
     *
     * 网址: http://localhost:8080/api/maintenance-orders/{orderId}
     * 参数: 无
     *
     * @param orderId 维保订单 ID
     * @return payload 为维保订单
     */
    @GetMapping(Urls.API_MAINTENANCE_ORDERS_BY_ID)
    public Result<MaintenanceOrder> findMaintenanceOrderById(@PathVariable long orderId) {
        return Result.single(orderMapper.findMaintenanceOrderById(orderId), "维保订单不存在");
    }

    /**
     * 插入或者更新维保订单
     *
     * 网址: http://localhost:8080/api/maintenance-orders/{orderId}
     * 参数: 参考 MaintenanceOrder 的属性
     *
     * @param order 维保订单
     * @return payload 为更新后的维保订单
     */
    @PutMapping(Urls.API_MAINTENANCE_ORDERS_BY_ID)
    public Result<MaintenanceOrder> upsertMaintenanceOrder(MaintenanceOrder order) {
        User servicePerson = super.getCurrentUser();
        return orderService.upsertMaintenanceOrder(order, servicePerson);
    }

    /**
     * 完成订单
     *
     * 网址: http://localhost:8080/api/maintenance-orders/{orderId}/complete
     * 参数: 无
     *
     * @param orderId 订单 ID
     */
    @PutMapping(Urls.API_MAINTENANCE_ORDERS_COMPLETE)
    public Result<Boolean> completeOrder(@PathVariable long orderId) {
        orderMapper.updateMaintenanceOrderState(orderId, MaintenanceOrder.STATE_COMPLETE);
        return Result.ok();
    }
}
