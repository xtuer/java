import AuditUtils from '@/../public/static-p/js/utils/AuditUtils';

/**
 * 审批的 Dao
 */
export default class AuditDao {
    /**
     * 获取所有的审批配置
     *
     * 网址: http://localhost:8080/api/audit-configs
     * 参数: 无
     *
     * @return {Promise} 返回 Promise 对象，resolve 的参数为审批配置的数组，reject 的参数为错误信息
     */
    static findAuditConfigs() {
        return Rest.get(Urls.API_AUDIT_CONFIGS).then(({ data: configs, success, message }) => {
            configs && configs.map(config => config.steps).flat().forEach(step => {
                step.desc = step.desc || '';
            });

            return Utils.response(configs, success, message);
        });
    }

    /**
     * 插入或者更新审批配置
     *
     * 网址: http://localhost:8080/api/audit-configs
     * 参数: 无
     * 请求体: 审批配置的数组
     *
     * @param configs 审批配置数组
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static upsertAuditConfigs(configs) {
        return Rest.update(Urls.API_AUDIT_CONFIGS, { data: configs, json: true }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }

    /**
     * 查询审批
     *
     * 网址: http://localhost:8080/api/audits/{auditId}
     * 参数: 无
     *
     * @param {Long} auditId  审批 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为审批，reject 的参数为错误信息
     */
    static findAudit(auditId) {
        return Rest.get(Urls.API_AUDITS_BY_ID, { params: { auditId } }).then(({ data: audit, success, message }) => {
            return Utils.response(audit, success, message);
        });
    }

    /**
     * 查询审批目标的审批
     *
     * 网址: http://localhost:8080/api/audits/of-target/{targetId}
     * 参数: 无
     *
     * @param {Long} targetId 审批目标的 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为审批，reject 的参数为错误信息
     */
    static findAuditOfTarget(targetId) {
        return Rest.get(Urls.API_AUDITS_BY_TARGET, { params: { targetId } }).then(({ data: audit, success, message }) => {
            if (audit) {
                // 合并审批的数据，方便使用
                AuditUtils.mergeAuditConfigToAuditItem(audit);
            }

            return Utils.response(audit, success, message);
        });
    }

    /**
     * 查询审批:
     *      * 审批申请人 ID 大于 0，则查询此审批申请人发起的审批
     *      * state 为 -1 时查询所有符合条件的审批，否则查询此状态的审批
     *
     * 网址: http://localhost:8080/api/audits
     * 参数:
     *      applicantId [可选]: 审批申请人 ID
     *      state       [可选]: 审批状态
     *      pageNumber  [可选]: 页码
     *      pageSize    [可选]: 数量
     *
     * @param {Json} filter 过滤条件，参考上面的参数
     * @return {Promise} 返回 Promise 对象，resolve 的参数为审批数组，reject 的参数为错误信息
     */
    static findAudits(filter) {
        return Rest.get(Urls.API_AUDITS, { data: filter }).then(({ data: audits, success, message }) => {
            return Utils.response(audits, success, message);
        });
    }

    /**
     * 查询审批项:
     *     * 审批员 ID 大于 0，则查询此审批员收到的审批
     *     * 申请人 ID 大于 0，则查询此人发起的审批
     *     * state 为 -1 时查询所有符合条件的审批，否则查询此状态的审批
     *
     * 网址: http://localhost:8080/api/audit-items?auditorId=1
     * 参数:
     *      applicantId [可选]: 审批申请人 ID
     *      auditorId   [可选]: 审批员 ID
     *      state       [可选]: 审批项状态
     *      pageNumber  [可选]: 页码
     *      pageSize    [可选]: 数量
     *
     * @param {JSON} filter 过滤条件
     * @return {Promise} 返回 Promise 对象，resolve 的参数为审批项的数组，reject 的参数为错误信息
     */
    static findAuditStepsByApplicantIdOrAuditorIdAndState(filter) {
        return Rest.get(Urls.API_AUDIT_STEPS, { data: filter }).then(({ data: auditItems, success, message }) => {
            return Utils.response(auditItems, success, message);
        });
    }

    /**
     * 审批: 通过或者拒绝审批阶段
     *
     * 网址: http://localhost:8080/api/audits/{auditId}/steps/{step}/accept
     * 参数:
     *      accepted     (必要): 为 true 表示通过，false 表示拒绝
     *      comment      [可选]: 审批意见
     *      attachmentId [可选]: 附件 ID
     *
     * @param {Long}   auditId  审批 ID
     * @param {Int}    step     审批阶段
     * @param {Bool}   accepted true 为通过审批，false 为拒绝审批
     * @param {String} comment  审批意见
     * @param {Long}   attachmentId 附件 ID
     * @param {Long}   nextStepAuditorId 下一阶段的审批员 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static acceptAuditStep(auditId, step, accepted, comment, attachmentId, nextStepAuditorId) {
        return Rest.update(Urls.API_AUDIT_STEPS_ACCEPT, {
            params: { auditId, step },
            data: { accepted, comment, attachmentId, nextStepAuditorId }
        }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }

    /**
     * 审批: 撤销审批阶段
     *
     * 网址: http://localhost:8080/api/audits/{auditId}/steps/{step}/recall
     * 参数: 无
     *
     * @param {Long} auditId  审批 ID
     * @param {Int}  step     审批阶段
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static recallAuditStep(auditId, step) {
        return Rest.update(Urls.API_AUDIT_STEPS_RECALL, { params: { auditId, step } }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }

    /**
     * 查询指定审批类型第 step 阶段的审批员
     *
     * 网址: http://localhost:8080/api/auditors
     * 参数:
     *      type (必要): 审批类型
     *      step (必要): 审批阶段
     *
     * @param type 审批类型
     * @param step 审批阶段
     * @return {Promise} 返回 Promise 对象，resolve 的参数为审批员数组，reject 的参数为错误信息
     */
    static findAuditors(type, step) {
        return Rest.get(Urls.API_AUDITORS, { data: { type, step } }).then(({ data: auditors, success, message }) => {
            return Utils.response(auditors, success, message);
        });
    }

    /**
     * 统计待传入的用户审批阶段数量
     *
     * 网址: http://localhost:8080API_WAITING_AUDIT_STEPS_COUNT_BY_USER_ID
     * 参数: 无
     *
     * @param {Long} userId 用户 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为待审批阶段数量，reject 的参数为错误信息
     */
    static countWaitingAuditStepsByUserId(userId) {
        return Rest.get(Urls.API_WAITING_AUDIT_STEPS_COUNT_BY_USER_ID, { params: { userId } }).then(({ data: count, success, message }) => {
            return Utils.response(count, success, message);
        });
    }
}
