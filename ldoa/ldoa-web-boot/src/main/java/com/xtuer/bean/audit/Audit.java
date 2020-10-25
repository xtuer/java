package com.xtuer.bean.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
     * 是否审批通过
     */
    private boolean passed;

    /**
     * 提交审批的时间
     */
    private Date createdAt;

    /**
     * 审批的简要描述
     */
    private String desc;

    /**
     * 审批项 (审批可能需要多个人进行审核，每个人的就是一项)
     */
    private List<AuditItem> items = new LinkedList<>();

    /**
     * 审批的配置
     */
    private AuditConfig config;
}
