/**
 * 销售订单的 DAO
 */
export default class SalesOrderDao {
    // 处理时间
    static handleDate(salesOrder) {
        salesOrder.agreementDate = dayjs(salesOrder.agreementDate).toDate();
        salesOrder.deliveryDate = dayjs(salesOrder.deliveryDate).toDate();
    }

    // 计算支付信息
    static calculatePayment(salesOrder) {
        // 总成交金额: 所有产品的数量*单价 + 咨询费
        // 净销售额: 所有产品的数量*单价

        salesOrder.totalDealAmount = 0;
        for (let item of salesOrder.produceOrder.items) {
            salesOrder.totalDealAmount += item.price * item.count + item.consultationFee;
            salesOrder.costDealAmount += item.price * item.count;
        }
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
            SalesOrderDao.calculatePayment(order);

            return Utils.response(order, success, message);
        });
    }

    /**
     * 查询符合条件的销售订单
     *
     * 网址: http://localhohst:8080/api/sales/salesOrders
     * 参数:
     *      customerName [可选]: 客户
     *      business     [可选]: 行业
     *      topic        [可选]: 主题
     *      pageNumber   [可选]: 页码
     *      pageSize     [可选]: 数量
     *
     * @param {JSON} filter
     * @return {Promise} 返回 Promise 对象，resolve 的参数为销售订单数组，reject 的参数为错误信息
     */
    static findSalesOrders(filter) {
        return Rest.get(Urls.API_SALES_ORDERS, { data: filter }).then(({ data: orders, success, message }) => {
            for (let order of orders) {
                SalesOrderDao.handleDate(order);
                SalesOrderDao.calculatePayment(order);
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
