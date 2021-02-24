/**
 * 维保订单的 Dao
 */
export default class {
    /**
     * 查询符合条件的维保订单
     *
     * 网址: http://localhost:8080/api/maintenance-orders
     * 参数:
     *      state              [可选]: 状态，为 -1 则查询所有
     *      maintenanceOrderSn [可选]: 维保单号
     *      salespersonName    [可选]: 销售人员
     *      productName        [可选]: 产品名称
     *      productCode        [可选]: 产品编码
     *      customerName       [可选]: 客户
     *      receivedStartAt    [可选]: 收货开始时间
     *      receivedEndAt      [可选]: 收货结束时间
     *      pageNumber         [可选]: 页码
     *      pageSize           [可选]: 数量
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

    /**
     * 完成订单
     *
     * 网址: http://localhost:8080/api/maintenance-orders/{orderId}/complete
     * 参数: 无
     *
     * @param {Long} orderId 订单 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static completeOrder(orderId) {
        return Rest.update(Urls.API_MAINTENANCE_ORDERS_COMPLETE, { params: { orderId } }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }

    /**
     * 更新订单的进度
     *
     * 网址: http://localhost:8080/api/maintenance-orders/{orderId}/progress
     * 参数: progress: 进度
     *
     * @param {Long} orderId  订单 ID
     * @param {String} progress 进度
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static updateProgress(orderId, progress) {
        return Rest.update(Urls.API_MAINTENANCE_ORDERS_PROGRESS, { params: { orderId }, data: { progress } }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }

    /**
     * 删除维保订单
     *
     * 网址: http://localhost:8080/api/maintenance-orders/{orderId}
     * 参数: 无
     *
     * @param orderId 订单 ID
     */
    static deleteMaintenanceOrder(orderId) {
        return Rest.del(Urls.API_MAINTENANCE_ORDERS_BY_ID, { params: { orderId } }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }
}
