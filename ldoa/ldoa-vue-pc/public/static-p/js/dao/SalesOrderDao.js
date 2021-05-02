/**
 * 销售订单的 DAO
 */
export default class SalesOrderDao {
    // 处理时间
    static handleDate(salesOrder) {
        salesOrder.agreementDate = dayjs(salesOrder.agreementDate).toDate();
        salesOrder.deliveryDate = dayjs(salesOrder.deliveryDate).toDate();
    }

    /**
     * 查询指定 ID 的销售订单
     *
     * 网址: http://localhohst:8080/api/sales/salesOrders/{salesOrderId}
     * 参数: 无
     *
     * @param salesOrderId 销售订单 ID
     * @return payload 为销售订单，查询不到时为 null
     */
    static findSalesOrder(salesOrderId) {
        return Rest.get(Urls.API_SALES_ORDERS_BY_ID, { params: { salesOrderId } }).then(({ data: order, success, message }) => {
            SalesOrderDao.handleDate(order);
            return Utils.response(order, success, message);
        });
    }

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
            for (let order of orders) {
                SalesOrderDao.handleDate(order);
            }

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
