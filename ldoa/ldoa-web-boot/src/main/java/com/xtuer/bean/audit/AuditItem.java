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
    private static final String[] STATUS_LABELS = { "初始化", "待审批", "拒绝", "通过" };

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
    private long auditItemId;

    /**
     * 审批申请人的 ID
     */
    private long applicantId;

    /**
     * 审批目标的 ID
     */
    private long targetId;

    /**
     * 审批目标对象的 JSON 内容，方便前端转为响应对象进行展示 (查询时从 Audit 中关联得到)
     */
    private String targetJson;

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

    /**
     * 获取审批状态 Label
     *
     * @return 返回订单状态的 Label
     */
    public String getStatusLabel() {
        if (status >= 0 && status < STATUS_LABELS.length) {
            return STATUS_LABELS[status];
        } else {
            return "未知";
        }
    }
}
