package com.xtuer.bean.audit;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 审批
 */
@Getter
@Setter
@Accessors(chain = true)
public class Audit {
    /**
     * 审批类型
     */
    private AuditType type;

    /**
     * 审批 ID
     */
    private long auditId;

    /**
     * 申请审批人员 ID
     */
    private long applicantId;

    /**
     * 是否审批通过
     */
    private boolean passed;

    /**
     * 审批项 (审批可能需要多个人进行审核，每个人的就是一项)
     */
    private List<AuditItem> items;
}
