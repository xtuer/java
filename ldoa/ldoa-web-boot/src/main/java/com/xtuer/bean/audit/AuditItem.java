package com.xtuer.bean.audit;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 审批项目
 */
@Getter
@Setter
@Accessors(chain = true)
public class AuditItem {
    /**
     * 审批的类型，例如订单
     */
    private AuditType type;

    /**
     * 审批人员的 ID
     */
    private long auditorId;

    /**
     * 审批目标的 ID
     */
    private long targetId;

    /**
     * 审批所属阶段
     */
    private int step;

    /**
     * 审批状态: 0 (已创建), 1 (等待审批), 2 (拒绝), 3 (通过)
     */
    private int status;
}
