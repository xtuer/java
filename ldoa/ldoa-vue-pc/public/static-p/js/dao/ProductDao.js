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
            return Utils.handleResponse(products, success, message);
        });
    }

    /**
     * 创建或者更新产品
     *
     * 网址: http://localhost:8080/api/products/{productId}
     * 参数: 无
     * 请求体: 产品的 JSON 字符串
     *     name  (必要): 产品名称
     *     code  (必要): 产品编码
     *     desc  [可选]: 产品描述
     *     model (必要): 产品规格/型号
     *     items (必要): [ { productItemId, count } ] 产品项的数组，但数组可以为空
     *
     * @param {JSON} product 产品
     * @return {Promise} 返回 Promise 对象，resolve 的参数为更新后的产品，reject 的参数为错误信息
     */
    static upsertProduct(product) {
        return Rest.update(Urls.API_PRODUCTS_BY_ID, {
            params: { productId: product.productId },
            data: product,
            json: true,
        }).then(({ data: newProduct, success, message }) => {
            return Utils.handleResponse(newProduct, success, message);
        });
    }

    /**
     * 删除产品
     *
     * 网址: http://localhost:8080/api/products/{productId}
     * 参数: 无
     *
     * @param {Long} productId 产品 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static deleteProduct(productId) {
        return Rest.del(Urls.API_PRODUCTS_BY_ID, { params: { productId } }).then(({ success, message }) => {
            return Utils.handleResponse(null, success, message);
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
            return Utils.handleResponse(items, success, message);
        });
    }

    /**
     * 创建或者更新产品项 (物料)
     *
     * 网址: http://localhost:8080/api/productItems/{productItemId}
     * 参数:
     *      name     (必要): 物料名称
     *      code     (必要): 物料编码
     *      type     (必要): 物料类型
     *      desc     [可选]: 物料描述
     *      model    (必要): 物料规格/型号
     *      standard (必要): 标准/规范
     *      material (必要): 材质
     *
     * @param {JSON} item 产品项
     * @return {Promise} 返回 Promise 对象，resolve 的参数为更新后的产品项，reject 的参数为错误信息
     */
    static upsertProductItem(item) {
        return Rest.update(Urls.API_PRODUCT_ITEMS_BY_ID, {
            params: { productItemId: item.productItemId },
            data: item,
        }).then(({ data: newItem, success, message }) => {
            return Utils.handleResponse(newItem, success, message);
        });
    }

    /**
     * 删除产品项
     *
     * 网址: http://localhost:8080/api/productItems/{productItemId}
     * 参数: 无
     *
     * @param productItemId 产品项 ID
     */
    static deleteProductItem(productItemId) {
        return Rest.del(Urls.API_PRODUCT_ITEMS_BY_ID, { params: { productItemId } }).then(({ success, message }) => {
            return Utils.handleResponse(null, success, message);
        });
    }
}