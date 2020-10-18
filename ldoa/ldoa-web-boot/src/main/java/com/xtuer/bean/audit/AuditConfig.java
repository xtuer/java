package com.xtuer.bean.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xtuer.bean.User;
import com.xtuer.util.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 审批配置，某个业务的审批，有多个审批阶段
 */
@Getter
@Setter
@Accessors(chain = true)
@JsonIgnoreProperties({"contentJson"})
public class AuditConfig {
    /**
     * 审批类型
     */
    private AuditType type;

    /**
     * 每个审批有多个阶段
     */
    private List<AuditConfigStep> steps = new LinkedList<>();

    /**
     * 获取审的 JSON 内容
     *
     * @return 返回审批的 JSON 内容
     */
    public String getContentJson() {
        return Utils.toJson(this);
    }

    /**
     * 使用 JSON 字符串重构审批对象
     *
     * @param contentJson 审批的 JSON 字符串
     */
    public void setContentJson(String contentJson) {
        try {
            AuditConfig temp = Utils.fromJson(contentJson, AuditConfig.class);
            BeanUtils.copyProperties(temp, this, "contentJson");
        } catch (Exception ignored) {}
    }

    /**
     * 审批配置的步骤
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class AuditConfigStep {
        /**
         * 审批的阶段
         */
        private int step;

        /**
         * 此步骤的审批员
         */
        private List<User> auditors = new LinkedList<>();
    }
}

/*
JSON 格式:
{
    type: 'ORDER',
    steps: [
        {
            step: 1,
            auditors: [
                userId: 123,
                username: 'Alice'
            ]
        },
    ]
}
*/
