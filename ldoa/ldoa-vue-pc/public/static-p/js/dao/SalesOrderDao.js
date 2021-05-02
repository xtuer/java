/**
 * 销售订单的 DAO
 */
export default class SalesOrderDao {
    /**
     * 查询符合条件的销售订单
     *
     * 网址: http://localhohst:8080/api/sales/salesOrders
     * 参数: 无
     *
     * @return {Promise} 返回 Promise 对象，resolve 的参数为销售订单数组，reject 的参数为错误信息
     */
    static findSalesOrders() {
        return Rest.get(Urls.API_SALES_ORDERS).then(({ data: orders, success, message }) => {
            return Utils.response(orders, success, message);
        });
    }

    /**
     * 更新或者插入销售订单
     *
     * 网址: http://localhohst:8080/api/sales/salesOrders/{salesOrderId}
     * 参数: 无
     * 请求体: 参数 SalesOrder 的属性
     *
     * @param {JSON} salesOrder 销售订单
     * @return {Promise} 返回 Promise 对象，resolve 的参数为销售订单，reject 的参数为错误信息
     */
    static upsertSalesOrder(salesOrder) {
        return Rest.update(
            Urls.API_SALES_ORDERS_BY_ID,
            { params: { salesOrderId: salesOrder.salesOrderId }, data: salesOrder, json: true }
        ).then(({ data: newOrder, success, message }) => {
            return Utils.response(newOrder, success, message);
        });
    }
}
