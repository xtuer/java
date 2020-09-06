/**
 * 产品的 Dao
 */
export default class ProductDao {
    /**
     * 查询符合条件的产品
     * 网址: http://localhost:8080/api/products
     * 参数:
     *      name       [可选]: 名字
     *      code       [可选]: 编码
     *      pageNumber [可选]: 页码
     *      pageSize   [可选]: 数量
     *
     * @param {JSON} filter 过滤条件
     * @return {Promise} 返回 Promise 对象，resolve 的参数为产品数组，reject 的参数为错误信息
     */
    static findProducts(filter) {
        return Rest.get(Urls.API_PRODUCTS, { data: filter }).then(({ data: products, success, message }) => {
            if (success) {
                return Promise.resolve(products);
            } else {
                Message.error(message);
                return Promise.reject(message);
            }
        });
    }

    /**
     * 查询符合条件的产品项
     *
     * 网址: http://localhost:8080/api/productItems
     * 参数:
     *      name       [可选]: 物料名称
     *      code       [可选]: 物料编码
     *      pageNumber [可选]: 页码
     *      pageSize   [可选]: 页码
     *
     * @param filter 过滤条件
     * @return {Promise} 返回 Promise 对象，resolve 的参数为产品项数组，reject 的参数为错误信息
     */
    static findProductItems(filter) {
        return Rest.get(Urls.API_PRODUCT_ITEMS, { data: filter }).then(({ data: items, success, message }) => {
            if (success) {
                return Promise.resolve(items);
            } else {
                Message.error(message);
                return Promise.reject(message);
            }
        });
    }
}
