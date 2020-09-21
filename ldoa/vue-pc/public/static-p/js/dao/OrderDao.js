/**
 * 订单 Dao
 */
export default class {
    /**
     * 查询指定 ID 的订单
     *
     * 网址: http://localhost:8080/api/orders/{orderId}
     * 参数: 无
     *
     * @param {Long} orderId 订单 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为订单，reject 的参数为错误信息
     */
    static findOrderById(orderId) {
        return Rest.get(Urls.API_ORDERS_BY_ID, { params: orderId }).then(({ data: order, success, message }) => {
            if (success) {
                return Promise.resolve(order);
            } else {
                Message.error(message);
                return Promise.reject(message);
            }
        });
    }

    /**
     * 查询符合条件的订单
     *
     * 网址: http://localhost:8080/api/orders
     * 参数: filter 的属性包括:
     *      orderSn      [可选]: 订单编号
     *      productCodes [可选]: 产品编号
     *      status       [可选]: 状态
     *      pageNumber   [可选]: 页码
     *      pageSize     [可选]: 数量
     *
     * @param filter 过滤器
     * @param page   分页对象
     * @return {Promise} 返回 Promise 对象，resolve 的参数为订单的数组，reject 的参数为错误信息
     */
    static findOrders(filter) {
        return Rest.get(Urls.API_ORDERS, { data: filter }).then(({ data: orders, success, message }) => {
            if (success) {
                return Promise.resolve(orders);
            } else {
                Message.error(message);
                return Promise.reject(message);
            }
        });
    }
}
