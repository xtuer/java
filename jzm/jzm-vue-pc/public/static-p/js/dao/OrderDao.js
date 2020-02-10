/**
 * 访问订单的 Dao
 */
export default class OrderDao {
    /**
     * 保存订单
     *
     * 网址: http://localhost:8080/api/orders/{orderId}
     * 参数: 无
     * 请求体: JSON 格式的订单
     *
     * @param {JSON} order 订单
     * @return {Promise} 返回 Promise 对象，resolve 的参数为订单，reject 的参数为错误信息
     */
    static saveOrder(order) {
        return new Promise((resolve, reject) => {
            order = JSON.parse(JSON.stringify(order));

            Rest.update({ url: Urls.API_ORDERS_BY_ID, pathVariables: { orderId:  order.id }, data: order, json: true })
                .then(({ data: updatedOrder, success, message }) => {
                    if (success) {
                        resolve(updatedOrder);
                    } else {
                        Notice.error({ title: '保存订单错误', desc: message });
                        reject(message);
                    }
                });
        });
    }

    /**
     * 查询符合条件的，第 pageNumber 页的订单
     *
     * 网址: http://localhost:8080/api/orders
     * 参数: filter 可包含下面几个属性
     *      name       [可选]: 用户名，如无则查询所有用户
     *      pageSize   [可选]: 数量，如无则由服务器端决定
     *      pageNumber [可选]: 页码，如无则默认为 1
     *
     * @param {JSON} filter 过滤条件
     * @return {Promise} 返回 Promise 对象，resolve 的参数为订单数组，reject 的参数为错误信息
     */
    static findOrders(filter) {
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
     * 删除指定 ID 的订单
     *
     * 网址: http://localhost:8080/api/orders/{orderId}
     * 参数: 无
     *
     * @param {Long} orderId 订单 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static deleteOrder(orderId) {
        return new Promise((resolve, reject) => {
            Rest.remove({ url: Urls.API_ORDERS_BY_ID, pathVariables: { orderId } }).then(({ success, message }) => {
                if (success) {
                    resolve();
                } else {
                    Notice.error({ title: '删除订单', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 更新订单
     *
     * 网址: http://localhost:8080/api/orders/{orderId}
     * 参数:
     *      status {Int} [可选]: 订单状态
     *
     * @param {JSON} order 订单
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static patchOrder(order) {
        return new Promise((resolve, reject) => {
            Rest.patch({ url: Urls.API_ORDERS_BY_ID, pathVariables: { orderId: order.id }, data: order }).then(({ data, success, message }) => {
                if (success) {
                    resolve(data);
                } else {
                    Notice.error({ title: '更新订单出错', desc: message });
                    reject(message);
                }
            });
        });
    }
}
