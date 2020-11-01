/**
 * 审批的辅助类
 */
export default class AuditUtils {
    /**
     * 创建订单的审批配置
     *
     * @return {JSON} 返回审批配置
     */
    static newOrderAuditConfig() {
        return {
            type: window.TYPE_ORDER,
            steps: [
                AuditUtils.newStep(1, '[生产调度] 确认订单，并填写产品型号、批次及序列号、要求证书日期'),
                AuditUtils.newStep(2, '[证书负责人] 校准证书负责人员确认信息'),
                AuditUtils.newStep(3, '[生产调度] 填写发货信息 (包括发货单号、发货日期)'),
                AuditUtils.newStep(4, '[销售人员] 确认回款情况'),
            ]
        }
    }

    /**
     * 创建审批阶段对象
     *
     * @param {Int} step 阶段值，从 1 开始
     * @param {String} desc 阶段描述
     * @return {JSON} 返回审批阶段的对象
     */
    static newStep(step, desc) {
        return { step, desc, auditors: [], uid: Utils.uid() };
    }

    /**
     * 校正审批配置
     *
     * @param {Array} configs 审批配置的数组
     * @return 无
     */
    static correctAuditConfigs(configs) {
        // 1. 订单的审批必须有 4 个阶段，如果不对，则恢复初始值
        const orderAuditConfig = configs.filter(config => config.type === window.TYPE_ORDER)[0] || AuditUtils.newOrderAuditConfig();
        if (orderAuditConfig.steps.length !== 4) {
            orderAuditConfig.steps = AuditUtils.newOrderAuditConfig().steps;
            console.error('订单的审批配置数据有问题，重置');
        }
    }

    /**
     * 清理审批配置，去掉 auditors 为空的阶段，调整阶段值
     *
     * @param {Array} configs 审批配置的数组
     * @return {Array} 返回处理后得到的 configs 副本
     */
    static cleanAuditConfigs(configs) {
        // 1. 克隆对象，对备份进行操作
        // 2. 遍历处理每一个配置
        // 3. 去掉 auditors 为空的阶段 step
        // 4. 调整 step 的阶段值

        // [1] 克隆对象，对备份进行操作
        configs = Utils.clone(configs);

        // [2] 遍历处理每一个配置
        // configs.forEach(config => {
        //     // [3] 去掉 auditors 为空的阶段 step
        //     config.steps = config.steps.filter(step => step.auditors.length > 0);

        //     // [4] 调整 step 的阶段值
        //     config.steps.forEach((step, index) => {
        //         step.step = index + 1;
        //     });
        // });
        // 只提示 auditors 为空
        configs.forEach(config => {
            config.steps.forEach(step => {
                if (step.auditors.length === 0) {
                    Message.warning(`阶段 <b>${step.desc}</b> 的审批员为空`);
                }
            });
        });

        return configs;
    }

    /**
     * 查询指定阶段的审批项
     *
     * @param {JSON}} audit 审批
     * @param {Int} step 审批阶段
     * @return {JSON} 返回查询到的审批项，查询不到返回空对象 {}
     */
    static findAuditItem(audit, step) {
        if (audit.items) {
            return audit.items.filter(item => item.step === step)[0] || {};
        } else {
            return {};
        }
    }

    /**
     * 合并审批的数据，方便使用
     *
     * @param {JSON} audit 审批 (其中包含了审批配置、审批项等)
     * @return 无
     */
    static mergeAuditConfigToAuditItem(audit) {
        audit.config.steps.forEach(step => {
            const auditItem = AuditUtils.findAuditItem(audit, step.step);

            // 审批项的说明
            auditItem.desc = step.desc;

            // 合并审批员的名字到审批项里
            step.auditors.filter(auditor => auditor.userId === auditItem.auditorId).forEach(auditor => {
                auditItem.auditorNickname = auditor.nickname;
            });
        })
    }
}
