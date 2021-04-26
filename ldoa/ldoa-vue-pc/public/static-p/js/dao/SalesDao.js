/**
 * 销售的 Dao
 */
export default class SalesDao {
    /**
     * 导入客户
     *
     * 网址: http://localhost:8080/api/sales/customers
     * 参数: tempFileUrl 客户信息临时文件的 URL
     *
     * @param tempFileUrl 客户信息临时文件的 URL
     */
    static importCustomers(tempFileUrl) {
        return Rest.update(Urls.API_SALES_CUSTOMERS, { data: { tempFileUrl } }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }
}
