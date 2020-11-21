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
        };
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
        // 1. 获取订单的审批
        // 2. 校验订单的审批
        //    2.1 如果不存在，则创建默认的审批
        //    2.2 如果已存在，但是必须有 4 个阶段，如果不对，则恢复初始值

        // [1] 获取订单的审批
        const orderAuditConfig = configs.filter(config => config.type === window.TYPE_ORDER)[0];

        // [2] 校验订单的审批
        if (!orderAuditConfig) {
            // [2.1] 如果不存在，则创建默认的审批
            configs.push(AuditUtils.newOrderAuditConfig());
        } else if (orderAuditConfig.steps.length !== 4) {
            // [2.2] 如果已存在，但是必须有 4 个阶段，如果不对，则恢复初始值
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
        configs.forEach(config => {
            // [3] 去掉 auditors 为空的阶段 step
            config.steps = config.steps.filter(step => step.auditors.length > 0);

            // [4] 调整 step 的阶段值
            config.steps.forEach((step, index) => {
                step.step = index + 1;
            });
        });

        // 只提示 auditors 为空
        // configs.forEach(config => {
        //     config.steps.forEach(step => {
        //         if (step.auditors.length === 0) {
        //             Message.warning(`阶段 <b>${step.desc}</b> 的审批员为空`);
        //         }
        //     });
        // });

        return configs;
    }

    /**
     * 把审批配置中的信息合并到审批项里，方便审批项的使用
     *
     * @param {JSON} audit 审批 (其中包含了审批配置、审批项等)
     * @return 无
     */
    static mergeAuditConfigToAuditItem(audit) {
        // 1. 查询审批项对应阶段的配置
        // 2. 设置审批项的描述
        // 3. 合并数据到审批项

        audit.items.forEach(item => {
            // [1] 查询审批项对应阶段的配置
            const configStep = audit.config.steps.find(t => t.step === item.step);

            if (!configStep) {
                console.error(`审批流程 [${item.step}] 的配置不存在`);
                return;
            }

            // [2] 设置审批项的描述
            item.desc = configStep.desc;

            // [3] 合并数据到审批项
            configStep.auditors.filter(auditor => auditor.userId === item.auditorId).forEach(auditor => {
                item.auditorNickname = auditor.nickname;
            });
        });
    }
}
