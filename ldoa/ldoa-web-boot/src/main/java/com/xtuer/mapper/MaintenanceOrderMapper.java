package com.xtuer.mapper;

import com.xtuer.bean.Page;
import com.xtuer.bean.order.MaintenanceOrder;
import com.xtuer.bean.order.MaintenanceOrderFilter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 维保订单 Mapper
 */
@Mapper
public interface MaintenanceOrderMapper {
    /**
     * 查询符合条件的维保订单
     *
     * @param filter 过滤条件
     * @param page 分页
     * @return 返回维保订单的数组
     */
    List<MaintenanceOrder> findMaintenanceOrders(MaintenanceOrderFilter filter, Page page);

    /**
     * 查询指定 ID 的维保订单
     *
     * @param orderId 维保订单 ID
     * @return 返回查询到的维保订单，查询不到返回 null
     */
    MaintenanceOrder findMaintenanceOrderById(long orderId);

    /**
     * 插入或者更新维保订单
     *
     * @param order 维保订单
     */
    void upsertMaintenanceOrder(MaintenanceOrder order);

    /**
     * 修改维保订单的状态
     *
     * @param orderId 订单 ID
     * @param state   状态
     */
    void updateMaintenanceOrderState(long orderId, int state);

    /**
     * 更新订单的进度
     *
     * @param orderId  订单 ID
     * @param progress 进度
     */
    void updateMaintenanceOrderProgress(long orderId, String progress);

    /**
     * 删除维保订单
     *
     * @param orderId 订单 ID
     */
    void deleteMaintenanceOrder(long orderId);
}
