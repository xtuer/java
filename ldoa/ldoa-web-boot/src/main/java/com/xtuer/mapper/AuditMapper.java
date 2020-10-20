package com.xtuer.mapper;

import com.xtuer.bean.audit.Audit;
import com.xtuer.bean.audit.AuditConfig;
import com.xtuer.bean.audit.AuditItem;
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

    /**
     * 插入审批
     *
     * @param audit 审批
     */
    void insertAudit(Audit audit);

    /**
     * 审批通过
     *
     * @param auditId 审批 ID
     */
    void passAudit(long auditId);

    /**
     * 插入审批项
     *
     * @param item 审批项
     */
    void insertAuditItem(AuditItem item);

    /**
     * 更新审批项的状态
     *
     * @param auditItemId 审批项的 ID
     * @param status      审批项的状态
     */
    void updateAuditItemStatus(long auditItemId, int status);

    /**
     * 删除指定 targetId 和类型的审批
     *
     * @param targetId 审批目标 ID
     * @param type     审批类型
     */
    void deleteAuditByTargetIdAndType(long targetId, AuditType type);

    /**
     * 删除指定 targetId 和类型的审批项
     *
     * @param targetId 审批目标 ID
     * @param type     审批类型
     */
    void deleteAuditItemsByTargetIdAndType(long targetId, AuditType type);

    /**
     * 查询审批员需要审批的审批项 (status 为 -1 时查询全部)
     *
     * @param auditorId 审批员 ID
     * @param status    审批项的状态
     * @return 返回审批项的数组
     */
    List<AuditItem> findAuditItemsByAuditorIdAndStatus(long auditorId, int status);
}
