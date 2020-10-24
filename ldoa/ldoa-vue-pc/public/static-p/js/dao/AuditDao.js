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
            configs && configs.map(config => config.steps).flat().forEach(step => {
                step.desc = step.desc || '第一次为 true 时才从服务器加载学员，避免重复加载';
            });

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

    /**
     * 查询审批项:
     *     * 审批员 ID 大于 0，则查询此审批员收到的审批
     *     * 申请人 ID 大于 0，则查询此人发起的审批
     *     * status 为 -1 时查询所有符合条件的审批，否则查询此状态的审批
     *
     * 网址: http://localhost:8080/api/audit-items?auditorId=1
     * 参数:
     *      applicantId [可选]: 审批申请人 ID
     *      auditorId   [可选]: 审批员 ID
     *      status      [可选]: 审批项状态
     *
     * @param {Long} auditorId 审批员 ID
     * @param {Int} status     审批项状态
     * @return {Promise} 返回 Promise 对象，resolve 的参数为审批项的数组，reject 的参数为错误信息
     */
    static findAuditItemsByAuditorIdAndStatus(auditorId, status) {
        return Rest.get(Urls.API_AUDIT_ITEMS, { data: { auditorId, status } }).then(({ data: auditItems, success, message }) => {
            return Utils.handleResponse(auditItems, success, message);
        });
    }
}
