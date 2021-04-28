/**
 * 客户的 Dao
 */
export default class CustomerDao {
    /**
     * 查找指定 ID 的客户
     *
     * 网址: http://localhost:8080/api/sales/customers/{customerId}
     * 参数: 无
     *
     * @param customerId 客户 ID
     * @return payload 为查询到的客户，查询不到为 null
     */
    static findCustomerById(customerId) {
        return Rest.get(Urls.API_SALES_CUSTOMERS_BY_ID, { params: { customerId } }).then(({ data: customer, success, message }) => {
            return Utils.response(customer, success, message);
        });
    }

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
     * 网址: http://localhost:8080/api/sales/customers/import
     * 参数: tempFileUrl 客户信息临时文件的 URL
     *
     * @param {String} tempFileUrl 客户信息临时文件的 URL
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static importCustomers(tempFileUrl) {
        return Rest.update(Urls.API_SALES_CUSTOMERS_IMPORT, { data: { tempFileUrl } }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }

    /**
     * 更新或者插入指定 ID 的客户
     *
     * 网址: http://localhost:8080/api/sales/customers/{customerId}
     * 参数:
     *      name:
     *      customerSn:
     * @param customer 客户
     */
    static upsertCustomer(customer) {
        return Rest.update(
            Urls.API_SALES_CUSTOMERS_BY_ID,
            { params: { customerId: customer.customerId }, data: customer, json: true }
        ).then(({ data: newCustomer, success, message }) => {
            return Utils.response(newCustomer, success, message);
        });
    }

    /**
     * 删除指定 ID 的客户
     *
     * 网址: http://localhost:8080/api/sales/customers/{customerId}
     * 参数: 无
     *
     * @param {Long} customerId 客户 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static deleteCustomer(customerId) {
        return Rest.del(Urls.API_SALES_CUSTOMERS_BY_ID, { params: { customerId } }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }
}
