/**
 * 库存操作的 Dao
 */
export default class StockDao {
    /**
     * 查询库存操作记录
     *
     * 网址: http://localhost:8080/api/stocks/records?type=IN
     * 参数:
     *      type         (必要): IN (入库)、OUT (出库)
     *      name         [可选]: 名字
     *      code         [可选]: 编码
     *      batch        [可选]: 批次
     *      model        [可选]: 规格型号
     *      manufacturer [可选]: 厂家
     *      startAt      [可选]: 开始时间
     *      enAt         [可选]: 结束时间
     *      pageNumber   [可选]: 页码
     *      pageSize     [可选]: 数量
     *
     * @param {JSON} filter 条件过滤器 (参考上面的参数说明)
     * @return {Promise} 返回 Promise 对象，resolve 的参数为库存操作记录的数组，reject 的参数为错误信息
     */
    static findStockRecords(filter) {
        return Rest.get(Urls.API_STOCKS_RECORDS, { data: filter }).then(({ data: records, success, message }) => {
            return Utils.handleResponse(records, success, message);
        });
    }

    /**
     * 入库
     *
     * 网址: http://localhost:8080/api/stocks/in
     * 参数:
     *      productItemId (必要): 物料 ID
     *      count         (必要): 入库数量
     *      batch         (必要): 批次
     *      warehouse     [可选]: 仓库
     *
     * @param {JSON} record 库存操作记录 (属性参考上面)
     * @return {Promise} 返回 Promise 对象，resolve 的参数为更新后的入库记录，reject 的参数为错误信息
     */
    static stockIn(record) {
        return Rest.create(Urls.API_STOCKS_IN, { data: record }).then(({ data: newRecord, success, message }) => {
            return Utils.handleResponse(newRecord, success, message);
        });
    }
}
