package com.xtuer.bean.audit;

import com.xtuer.bean.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;

/**
 * 审批配置的步骤
 */
@Getter
@Setter
@Accessors(chain = true)
public class AuditConfigStep {
    /**
     * 审批的阶段
     */
    private int step;

    /**
     * 审批阶段的说明
     */
    private String desc;

    /**
     * 此步骤的审批员
     */
    private List<User> auditors = new LinkedList<>();

    /**
     * 是否上传附件
     */
    private boolean attachment;

    /**
     * 审批内容的模板
     */
    private String commentTemplate;
}
