package com.xtuer.service;

import com.xtuer.bean.User;
import com.xtuer.bean.audit.AuditConfig;
import com.xtuer.bean.audit.AuditType;
import com.xtuer.mapper.AuditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 审核服务
 */
@Service
public class AuditService {
    @Autowired
    private AuditMapper auditMapper;

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

    /**
     * 插入或者更新审批配置
     *
     * @param configs 审批配置数组
     */
    @Transactional(rollbackFor = Exception.class)
    public void upsertAuditConfigs(List<AuditConfig> configs) {
        configs.forEach(config -> {
            auditMapper.upsertAuditConfig(config);
        });
    }
}
