/**
 * 备件 Dao
 */
export default class SpareDao {
    /**
     * 查询符合条件的备件
     *
     * 网址: http://localhost:8080/api/spares
     * 参数: filter 可包含下面几个属性
     *      pageSize   [可选]: 数量，如无则由服务器端决定
     *      pageNumber [可选]: 页码，如无则默认为 1
     *
     * @param {JSON} filter 过滤条件
     * @return {Promise} 返回 Promise 对象，resolve 的参数为备件数组，reject 的参数为错误信息
     */
    static findSpares(filter) {
        return new Promise((resolve, reject) => {
            Rest.get({ url: Urls.API_SPARES }).then(({ data: spares, success, message }) => {
                if (success) {
                    resolve(spares);
                } else {
                    Notice.error({ title: '查询备件错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 保存备件
     *
     * 网址: http://localhost:8080/api/spares/{spareId}
     * 参数: 备件属性，参考 SpareUtils.newSpare() 返回的备件对象的属性
     *
     * @param {JSON} spare 订单
     * @return {Promise} 返回 Promise 对象，resolve 的参数为备件 ID，reject 的参数为错误信息
     */
    static saveSpare(spare) {
        return new Promise((resolve, reject) => {
            Rest.update({ url: Urls.API_SPARES_BY_ID, data: spare }).then(({ data: spareId, success, message }) => {
                if (success) {
                    resolve(spareId);
                } else {
                    Notice.error({ title: '保存备件错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 删除指定 ID 的备件
     *
     * 网址: http://localhost:8080/api/spares/{spareId}
     * 参数: 无
     *
     * @param {Long} spareId 备件 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static deleteSpare(spareId) {
        return new Promise((resolve, reject) => {
            Rest.remove({ url: Urls.API_SPARES_BY_ID, pathVariables: { spareId } }).then(({ success, message }) => {
                if (success) {
                    resolve();
                } else {
                    Notice.error({ title: '删除备件错误', desc: message });
                    reject(message);
                }
            });
        });
    }
}
