/**
 * 维保订单的 Dao
 */
export default class {
    /**
     * 查询符合条件的维保订单
     *
     * 网址: http://localhost:8080/api/maintenance-orders
     * 参数:
     *      pageNumber [可选]: 页码
     *      pageSize   [可选]: 数量
     *
     * @param filter 查询条件
     * @return payload 为维保订单的数组
     */
    static findMaintenanceOrders(filter) {
        return Rest.get(Urls.API_MAINTENANCE_ORDERS, { data: filter }).then(({ data: orders, success, message }) => {
            return Utils.response(orders, success, message);
        });
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
    static upsertMaintenanceOrder(maintenanceOrder) {
        return Rest.update(
            Urls.API_MAINTENANCE_ORDERS_BY_ID,
            { params: { orderId: maintenanceOrder.maintenanceOrderId }, data: maintenanceOrder }
        ).then(({ data: newOrder, success, message }) => {
            return Utils.response(newOrder, success, message);
        });
    }
}
