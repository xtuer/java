package com.xtuer.service;

import com.xtuer.bean.User;
import com.xtuer.bean.audit.AuditType;
import org.springframework.stereotype.Service;

/**
 * 审核服务
 */
@Service
public class AuditService {
    /**
     * 创建审批
     *
     * @param applicant 申请人
     * @param type      审批类型
     * @param targetId  审批对象的 ID
     */
    public void createAudit(User applicant, AuditType type, long targetId) {
        // 1. 查询审批配置
        // 2. 创建审批
        // 3. 创建审批项
        // 4. 第一级审批项状态为待审批
    }
}
