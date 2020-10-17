package com.xtuer.bean.audit;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

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
     * 审批 ID
     */
    private long auditId;

    /**
     * 审批项 ID
     */
    private int auditItemId;

    /**
     * 审批申请人的 ID
     */
    private long applicantId;

    /**
     * 审批目标的 ID
     */
    private long targetId;

    /**
     * 审批人员的 ID
     */
    private long auditorId;

    /**
     * 审批的阶段，每个审批可能需要多阶段，多个人进行审批
     */
    private int step;

    /**
     * 审批状态: 0 (初始化), 1 (待审批), 2 (拒绝), 3 (通过)
     */
    private int status;

    /**
     * 处理时间
     */
    private Date processedAt;
}
