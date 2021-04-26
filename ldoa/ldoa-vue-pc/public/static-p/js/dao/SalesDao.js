/**
 * 销售的 Dao
 */
export default class SalesDao {
    /**
     * 查询客户
     *
     * 网址: http://localhost:8080/api/sales/customers
     * 参数:
     *      name       [可选]: 客户名称
     *      customerSn [可选]: 客户编号
     *      business   [可选]: 行业
     *      pageNumber [可选]: 页码
     *      pageSize   [可选]: 数量
     *
     * @param {JSON} filter 过滤条件
     * @return {Promise} 返回 Promise 对象，resolve 的参数为客户的数组，reject 的参数为错误信息
     */
    static findCustomers(filter) {
        return Rest.get(Urls.API_SALES_CUSTOMERS, { data: filter }).then(({ data: customers, success, message }) => {
            return Utils.response(customers, success, message);
        });
    }

    /**
     * 导入客户
     *
     * 网址: http://localhost:8080/api/sales/customers
     * 参数: tempFileUrl 客户信息临时文件的 URL
     *
     * @param {String} tempFileUrl 客户信息临时文件的 URL
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static importCustomers(tempFileUrl) {
        return Rest.update(Urls.API_SALES_CUSTOMERS, { data: { tempFileUrl } }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }
}
