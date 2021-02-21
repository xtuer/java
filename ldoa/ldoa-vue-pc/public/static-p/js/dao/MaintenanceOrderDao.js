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
     * @param {Json} filter 查询条件
     * @return {Promise} 返回 Promise 对象，resolve 的参数为维保订单的数组，reject 的参数为错误信息
     */
    static findMaintenanceOrders(filter) {
        return Rest.get(Urls.API_MAINTENANCE_ORDERS, { data: filter }).then(({ data: orders, success, message }) => {
            return Utils.response(orders, success, message);
        });
    }

    /**
     * 查询指定 ID 的维保订单
     *
     * 网址: http://localhost:8080/api/maintenance-orders/{orderId}
     * 参数: 无
     *
     * @param {Long} orderId 维保订单 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为维保订单，reject 的参数为错误信息
     */
    static findMaintenanceOrderById(orderId) {
        return Rest.get(Urls.API_MAINTENANCE_ORDERS_BY_ID, { params: { orderId } }).then(({ data: order, success, message }) => {
            order.needCertificate = order.needCertificate ? 1 : 0; // 解决 iView Radio 多个选项时不能使用 bool 的问题
            return Utils.response(order, success, message);
        });
    }

    /**
     * 插入或者更新维保订单
     *
     * 网址: http://localhost:8080/api/maintenance-orders/{orderId}
     * 参数:
     *
     * @param {Json} order 维保订单
     * @return {Promise} 返回 Promise 对象，resolve 的参数为更新后的维保订单，reject 的参数为错误信息
     */
    static upsertMaintenanceOrder(order) {
        return Rest.update(
            Urls.API_MAINTENANCE_ORDERS_BY_ID,
            { params: { orderId: order.maintenanceOrderId }, data: order }
        ).then(({ data: newOrder, success, message }) => {
            return Utils.response(newOrder, success, message);
        });
    }
}
