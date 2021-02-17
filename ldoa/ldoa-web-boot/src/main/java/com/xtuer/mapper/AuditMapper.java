package com.xtuer.mapper;

import com.xtuer.bean.Page;
import com.xtuer.bean.audit.Audit;
import com.xtuer.bean.audit.AuditConfig;
import com.xtuer.bean.audit.AuditStep;
import com.xtuer.bean.audit.AuditType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 审批的 Mapper
 */
@Mapper
public interface AuditMapper {
    /*===============================================================================
     *                                     审批配置
     *=============================================================================*/
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

    /*===============================================================================
     *                                       审批
     *=============================================================================*/
    /**
     * 查询审批
     *
     * @param auditId 审批的 ID
     * @return 返回审批
     */
    Audit findAuditById(long auditId);

    /**
     * 查询审批目标的审批
     *
     * @param targetId 审批目标的 ID
     * @return 返回审批
     */
    Audit findAuditByTargetId(long targetId);

    /**
     * 查询审批
     * * 审批申请人 ID 大于 0，则查询此审批申请人发起的审批
     * * state 为 -1 时查询所有符合条件的审批，否则查询此状态的审批
     *
     * @param applicantId 审批申请人 ID
     * @param state       审批状态
     * @param page        分页对象
     * @return 返回审批的数组
     */
    List<Audit> findAuditsByApplicantIdAndState(long applicantId, int state, Page page);

    /**
     * 插入审批
     *
     * @param audit 审批
     */
    void upsertAudit(Audit audit);

    /**
     * 更新审批状态
     *
     * @param auditId 审批 ID
     * @param state   审批状态
     */
    void updateAuditState(long auditId, int state);

    /**
     * 删除指定 targetId 的审批
     *
     * @param targetId 审批目标 ID
     */
    void deleteAuditByTargetId(long targetId);

    /*===============================================================================
     *                                     审批阶段
     *=============================================================================*/
    /**
     * 查询审批项
     *
     * @param auditItemId 审批项 ID
     * @return 返回审批项数
     */
    AuditStep findAuditItemByAuditItemId(long auditItemId);

    /**
     * 查询审批的阶段 (同时查询了附件)
     *
     * @param auditId 审批 ID
     * @return 返回审批阶段数组
     */
    List<AuditStep> findAuditStepsByAuditId(long auditId);

    /**
     * 查询审批员需要审批的审批阶段 (state 为 -1 时查询全部的)
     *
     * @param applicantId 审批申请人 ID
     * @param auditorId   审批员 ID
     * @param state       审批项的状态
     * @param page        分页
     * @return 返回审批阶段的数组
     */
    List<AuditStep> findAuditStepsByApplicantIdOrAuditorIdAndState(long applicantId, long auditorId, int state, Page page);

    /**
     * 插入审批阶段
     *
     * @param items 审批阶段的数组
     */
    void insertAuditSteps(List<AuditStep> items);

    /**
     * 通过或者拒绝审批项
     *
     * @param auditId 审批项 ID
     * @param step    阶段
     * @param state   状态
     * @param comment 审批意见
     * @param attachmentId 附件 ID
     */
    void acceptOrRejectAuditStep(long auditId, int step, int state, String comment, long attachmentId);

    /**
     * 更新审批阶段的状态
     *
     * @param auditId 审批 ID
     * @param step    阶段
     * @param state   阶段的状态
     */
    void updateAuditStepState(long auditId, int step, int state);

    /**
     * 更新审批阶段的审批员
     *
     * @param auditId   审批 ID
     * @param step      阶段
     * @param auditorId 审批员 ID
     */
    void updateAuditStepAuditor(long auditId, int step, long auditorId);

    /**
     * 删除审批的审批阶段
     *
     * @param auditId 审批 ID
     */
    void deleteAuditSteps(long auditId);

    /**
     * 设置第 step 阶段为当前阶段
     *
     * @param auditId   审批 ID
     * @param step      审批阶段
     * @param auditorId 审批员 ID
     */
    void changeCurrentAuditStep(long auditId, int step, long auditorId);

    /**
     * 统计待传入的用户审批阶段数量
     *
     * @param userId 用户 ID
     * @return 返回待审批阶段的数量
     */
    int countWaitingAuditStepsByUserId(long userId);
}
