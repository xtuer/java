/**
 * 审批的 Dao
 */
export default class AuditDao {
    /**
     * 获取所有的审批配置
     *
     * 网址: http://localhost:8080/api/audits
     * 参数: 无
     *
     * @return payload 为审批配置的数组
     */
    static findAuditConfigs() {
        return Rest.get(Urls.API_AUDITS).then(({ data: configs, success, message }) => {
            return Utils.handleResponse(configs, success, message);
        });
    }

    /**
     * 插入或者更新审批配置
     *
     * 网址: http://localhost:8080/api/audits
     * 参数: 无
     * 请求体: 审批配置的数组
     *
     * @param configs 审批配置数组
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static upsertAuditConfigs(configs) {
        return Rest.update(Urls.API_AUDITS, { data: configs, json: true }).then(({ success, message }) => {
            return Utils.handleResponse(null, success, message);
        });
    }
}
