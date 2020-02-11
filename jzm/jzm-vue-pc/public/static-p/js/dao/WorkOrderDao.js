/**
 * 工单 Dao
 */
export default class WorkOrderDao {
    /**
     * 查询符合条件的，第 pageNumber 页的工单
     *
     * 网址: http://localhost:8080/api/maintenances
     * 参数: filter 可包含下面几个属性
     *      name       [可选]: 用户名，如无则查询所有用户
     *      pageSize   [可选]: 数量，如无则由服务器端决定
     *      pageNumber [可选]: 页码，如无则默认为 1
     *
     * @param {JSON} filter 过滤条件
     * @return {Promise} 返回 Promise 对象，resolve 的参数为工单数组，reject 的参数为错误信息
     */
    static findWorkOrders(filter) {
        return new Promise((resolve, reject) => {
            Rest.get({ url: Urls.API_ORDERS, data: filter }).then(({ data: orders, success, message }) => {
                if (success) {
                    resolve(orders);
                } else {
                    Notice.error({ title: '查询订单错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 删除指定 ID 的工单
     *
     * 网址: http://localhost:8080/api/workOrders/{workOrderId}
     * 参数: 无
     *
     * @param {Long} workOrderId 工单 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static deleteOrder(orderId) {
        return new Promise((resolve, reject) => {
            Rest.remove({ url: Urls.API_ORDERS_BY_ID, pathVariables: { orderId } }).then(({ success, message }) => {
                if (success) {
                    resolve();
                } else {
                    Notice.error({ title: '删除工单错误', desc: message });
                    reject(message);
                }
            });
        });
    }
}
