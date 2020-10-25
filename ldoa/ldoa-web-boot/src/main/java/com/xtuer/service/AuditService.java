package com.xtuer.service;

import com.xtuer.bean.Order;
import com.xtuer.bean.Result;
import com.xtuer.bean.User;
import com.xtuer.bean.audit.Audit;
import com.xtuer.bean.audit.AuditConfig;
import com.xtuer.bean.audit.AuditItem;
import com.xtuer.bean.audit.AuditType;
import com.xtuer.mapper.AuditMapper;
import com.xtuer.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 审核服务
 */
@Service
@Slf4j
public class AuditService extends BaseService {
    @Autowired
    private AuditMapper auditMapper;

    /**
     * 查询审批
     *
     * @param auditId 审批 ID
     * @return 返回查询到的审批，查询不到返回 null
     */
    public Audit findAudit(long auditId) {
        return findAuditByAuditIdOrTargetId(auditId, 0);
    }

    /**
     * 查询目标对象的审批 (例如在订单详情页查看订单的审批，不需要直接指定审批 ID)
     *
     * @param targetId 审批对象 ID
     * @return 返回查询到的审批，查询不到返回 null
     */
    public Audit findAuditOfTarget(long targetId) {
        return findAuditByAuditIdOrTargetId(0, targetId);
    }

    /**
     * 查询指定 ID 的审批或者审批目标的审批:
     * * auditId 不为 0 时查询指定 ID 的审批
     * * auditId 等于 0 时查询审批目标的审批
     *
     * @param auditId  审批 ID
     * @param targetId 审批目标的 ID
     * @return 返回查询到的审批，查询不到返回 null
     */
    private Audit findAuditByAuditIdOrTargetId(long auditId, long targetId) {
        // 1. 查询审批
        // 2. 查询审批项
        // 3. 查询审批配置

        // [1] 查询审批
        Audit audit;

        if (auditId > 0) {
            audit = auditMapper.findAuditById(auditId);
        } else {
            audit = auditMapper.findAuditByTargetId(targetId);
        }

        if (audit == null) {
            return null;
        }

        // [2] 查询审批项
        List<AuditItem> items = auditMapper.findAuditItemsByAuditId(audit.getAuditId());
        audit.setItems(items);

        // [3] 查询审批配置
        AuditConfig config = auditMapper.findAuditConfigByType(audit.getType());
        audit.setConfig(config);

        return audit;
    }

    /**
     * 更新或者创建订单的审批
     *
     * @param applicant 申请人
     * @param order     订单
     * @return 返回操作结果
     */
    public Result<String> upsertOrderAudit(User applicant, Order order) {
        Objects.requireNonNull(order, "订单不能为空");
        String desc = "客户: " + order.getCustomerCompany();

        return upsertAudit(applicant, AuditType.ORDER, order.getOrderId(), Utils.toJson(order), desc);
    }

    /**
     * 更新或者创建审批
     *
     * @param applicant 申请人
     * @param type      审批类型
     * @param targetId  审批对象的 ID
     * @param desc      审批的简要描述
     * @return 返回操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> upsertAudit(User applicant, AuditType type, long targetId, String targetJson, String desc) {
        // 1. 查询审批配置
        // 2. 查询审批，不存在则创建
        // 3. 创建审批项
        // 4. 设置 step 为 1 的审批项的状态为 1 (待审批)
        // 5. 删除同一个 targetId + type 的审批项，例如审批被拒绝后重新提交审批
        // 6. 保存到数据库

        // [1] 查询审批配置
        AuditConfig config = auditMapper.findAuditConfigByType(type);

        if (config == null) {
            return Result.fail("审批配置不存在: " + type);
        }
        if (config.getSteps().size() == 0) {
            return Result.fail("没有配置审批流程: " + type);
        }

        // [2] 查询审批，不存在则创建
        Audit audit = auditMapper.findAuditByTargetId(targetId);

        if (audit == null) {
            audit = new Audit()
                    .setType(type)
                    .setAuditId(super.nextId())
                    .setApplicantId(applicant.getUserId())
                    .setTargetId(targetId);
        }

        audit.setTargetJson(targetJson);
        audit.setDesc(desc);

        // [3] 创建审批项
        final Audit back = audit;
        config.getSteps().forEach(step -> {
            step.getAuditors().forEach(auditor -> {
               AuditItem item = new AuditItem()
                       .setType(type)
                       .setAuditId(back.getAuditId())
                       .setAuditItemId(super.nextId())
                       .setApplicantId(applicant.getUserId())
                       .setTargetId(targetId)
                       .setAuditorId(auditor.getUserId())
                       .setStep(step.getStep())
                       .setStatus(0);
                back.getItems().add(item);
            });
        });

        // [4] 设置 step 为 1 的审批项的状态为 1 (待审批)
        audit.getItems()
                .stream()
                .filter(item -> item.getStep() == 1)
                .forEach(item -> item.setStatus(1));

        log.info("[开始] 创建审批, 用户: [{}], 类型: [{}], 审批对象 ID: [{}]", applicant.getNickname(), type, targetId);

        // [5] 删除同一个 targetId 的审批项，例如审批被拒绝后重新提交审批
        auditMapper.deleteAuditItemsByTargetId(targetId);

        // [6] 保存到数据库
        // 保存审批
        auditMapper.upsertAudit(audit);

        // 保存审批项
        for (AuditItem item : audit.getItems()) {
            auditMapper.insertAuditItem(item);
        }

        return Result.ok();
    }

    /**
     * 审批
     *
     * @param auditItemId 审批项 ID
     * @param accepted    true 为通过，false 为拒绝
     */
    public void processAuditItem(long auditItemId, boolean accepted) {
        // 审批状态: 0 (初始化), 1 (待审批), 2 (拒绝), 3 (通过)
        // 1. 如果是拒绝，当前审批项的状态为拒绝，且修改上一级审批项的状态为待审批，如果是第一层，则审批不通过
        // 2. 如果是通过，当前审批项的状态为通过，当当前层都为通过时，需要修改下一层审批项的状态为待审批，如果是最后一层，则审批通过
        // 3. 审批拒绝时，订单状态修改为审批不通过，审批通过时修改订单为审批通过

        auditMapper.updateAuditItemStatus(auditItemId, accepted ? 3 : 2);
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
