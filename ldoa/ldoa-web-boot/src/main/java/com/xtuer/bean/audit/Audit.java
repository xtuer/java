package com.xtuer.bean.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xtuer.util.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 审批
 */
@Getter
@Setter
@Accessors(chain = true)
@JsonIgnoreProperties({"contentJson"}) // 很重要，如果没有会导致递归
public class Audit {
    private static final String[] STATE_LABELS = { "初始化", "拒绝", "通过" };

    public static final int STATUS_INIT     = 0;
    public static final int STATUS_REJECTED = 1;
    public static final int STATUS_ACCEPTED = 2;

    /**
     * 审批类型
     */
    private AuditType type;

    /**
     * 审批 ID
     */
    private long auditId;

    /**
     * 审批申请人的 ID
     */
    private long applicantId;

    /**
     * 审批目标的 ID
     */
    private long targetId;

    /**
     * 审批目标对象的 JSON 内容，方便前端转为响应对象进行展示
     */
    private String targetJson;

    /**
     * 审批状态: 0 (初始化), 1 (拒绝), 2 (通过)
     */
    private int state;

    /**
     * 提交审批的时间
     */
    private Date createdAt;

    /**
     * 审批的简要描述
     */
    private String desc;

    /**
     * 审批阶段 (每个阶段有多个候选审批员，但只会选择一个来审批)
     */
    private List<AuditStep> steps = new LinkedList<>();

    /**
     * 审批的配置
     */
    private AuditConfig config;

    /**
     * 获取审批状态 Label
     *
     * @return 返回订单状态的 Label
     */
    public String getStateLabel() {
        return Utils.getStateLabel(STATE_LABELS, state);
    }
}
