package com.xtuer.mapper;

import com.xtuer.bean.audit.AuditConfig;
import com.xtuer.bean.audit.AuditType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 审批的 Mapper
 */
@Mapper
public interface AuditMapper {
    /**
     * 获取所有的审批配置
     *
     * @return 返回审批配置的数组
     */
    List<AuditConfig> findAuditConfigs();

    /**
     * 查询指定类型的审批配置
     *
     * @param type 审批类型
     * @return 返回查询到的审批配置，查询不到返回 null
     */
    AuditConfig findAuditConfigByType(AuditType type);

    /**
     * 插入或者更新审批配置
     *
     * @param config 审批配置
     */
    void upsertAuditConfig(AuditConfig config);
}
