package com.xtuer.service;

import com.xtuer.bean.Result;
import com.xtuer.bean.User;
import com.xtuer.bean.audit.*;
import com.xtuer.exception.ApplicationException;
import com.xtuer.mapper.AuditMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 审核服务
 */
@Service
@Slf4j
public class AuditService extends BaseService {
    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private AuditServiceHelper helper;

    /**
     * 查询指定审批类型第 step 阶段的审批员
     *
     * @param type 审批类型
     * @param step 审批阶段
     * @return 返回审批员数组
     */
    public List<User> findAuditorsByTypeAndStep(AuditType type, int step) {
        // 1. 查询审批类型 type 对应的审批配置
        // 2. 返回第 step 阶段的审批员
        // 3. 如果没有对应的数据，返回空集合

        // [1] 查询审批类型 type 对应的审批配置
        AuditConfig config = auditMapper.findAuditConfigByType(type);

        if (config == null) {
            return Collections.emptyList();
        }

        for (AuditConfigStep s : config.getSteps()) {
            // [2] 返回第 step 阶段的审批员
            if (s.getStep() == step) {
                return s.getAuditors();
            }
        }

        // [3] 如果没有对应的数据，返回空集合
        return Collections.emptyList();
    }

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
        // 2. 查询审批阶段
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

        // [2] 查询审阶段
        List<AuditStep> steps = auditMapper.findAuditStepsByAuditId(audit.getAuditId());
        audit.setSteps(steps);

        // [3] 查询审批配置
        AuditConfig config = auditMapper.findAuditConfigByType(audit.getType());
        audit.setConfig(config);

        return audit;
    }

    /**
     * 更新或者创建审批
     *
     * @param applicant 申请人
     * @param type      审批类型
     * @param targetId  审批对象的 ID
     * @param desc      审批的简要描述
     * @param firstStepAuditorId 第 1 阶段审批员 ID
     * @return 返回操作结果
     * @exception ApplicationException 审批配置无效时抛异常
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Audit> upsertAudit(User applicant,
                                     AuditType type,
                                     long targetId,
                                     String targetJson,
                                     String desc,
                                     long firstStepAuditorId) {
        // 1. 查询审批配置
        // 2. 校验审批配置，如果审批配置无效则返回
        // 3. 查询审批，不存在则创建
        // 4. 创建审批阶段
        // 5. 保存审批到数据库
        // 6. 删除已经存在的审批阶段，插入新创建的审批阶段
        // 7. 设置第 1 阶段为当前审批阶段

        // [1] 查询审批配置
        AuditConfig config = auditMapper.findAuditConfigByType(type);

        // [2] 校验审批配置，如果审批配置无效则返回
        Result<String> checkResult = checkAuditConfig(config, type);
        if (!checkResult.isSuccess()) {
            throw new ApplicationException(checkResult.getMessage(), 10);
        }

        // [3] 查询审批，不存在则创建
        Audit audit = auditMapper.findAuditByTargetId(targetId);

        if (audit == null) {
            audit = new Audit()
                    .setType(type)
                    .setAuditId(super.nextId())
                    .setApplicantId(applicant.getUserId())
                    .setTargetId(targetId);
        }

        // 设置审批的目标、描述和状态
        audit.setTargetJson(targetJson);
        audit.setDesc(desc);
        audit.setState(Audit.STATUS_AUDITING);

        // [4] 创建审阶段
        final Audit back = audit;
        config.getSteps().forEach(step -> {
            AuditStep item = new AuditStep()
                    .setType(type)
                    .setAuditId(back.getAuditId())
                    .setApplicantId(applicant.getUserId())
                    .setTargetId(targetId)
                    .setStep(step.getStep())
                    .setState(AuditStep.STATE_INIT)
                    .setAuditorId(0);
            back.getSteps().add(item); // 新创建的审批阶段添加到审批中
        });

        log.info("[开始] 创建审批, 用户: [{}], 类型: [{}], 审批对象 ID: [{}]", applicant.getNickname(), type, targetId);

        // [5] 保存审批到数据库
        auditMapper.upsertAudit(audit);

        // [6] 删除已经存在的审批阶段，插入新创建的审批阶段
        auditMapper.deleteAuditSteps(audit.getAuditId());
        auditMapper.insertAuditSteps(audit.getSteps());

        // [7] 设置第 1 阶段为当前审批阶段
        auditMapper.changeCurrentAuditStep(audit.getAuditId(), 1, firstStepAuditorId);

        log.info("[成功] 创建审批, 用户: [{}], 类型: [{}], 审批对象 ID: [{}]", applicant.getNickname(), type, targetId);

        return Result.ok(audit);
    }

    /**
     * 校验审批配置，审批配置有效返回 Result.ok()，无效返回 Result.fail()
     *
     * @param config 审批配置
     * @param type   审批类型
     * @return 审批配置有效返回 Result.ok()，无效返回 Result.fail()
     */
    public Result<String> checkAuditConfig(AuditConfig config, AuditType type) {
        // 1. 检查审批配置是否不存在
        // 2. 检查是否配置了审批流程
        // 3. 每个流程中都必须配置审批员

        if (config == null) {
            return Result.fail("审批配置不存在: " + type);
        }

        if (config.getSteps().size() == 0) {
            return Result.fail("没有配置审批流程: " + type);
        }

        for (AuditConfigStep step : config.getSteps()) {
            if (step.getAuditors().size() == 0) {
                return Result.fail("审批的流程中有的没有配置审批员，请核查对应的审批配置");
            }
        }

        return Result.ok();
    }

    /**
     * 设置第 step 阶段为当前阶段
     *
     * @param auditId   审批 ID
     * @param step      审批阶段
     * @param auditorId 审批员 ID
     */
    public void changeCurrentAuditStep(long auditId, int step, long auditorId) {
        auditMapper.changeCurrentAuditStep(auditId, step, auditorId);
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

    /**
     * 审批: 通过或者拒绝审批阶段
     *
     * @param auditId  审批 ID
     * @param step     审批阶段
     * @param accepted true 为通过审批，false 为拒绝审批
     * @param comment  审批意见
     * @param attachmentId 附件 ID
     * @param nextStepAuditorId 下一阶段审批员 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void acceptAuditStep(long auditId, int step, boolean accepted, String comment, long attachmentId, long nextStepAuditorId) {
        // 审批阶段状态: 0 (初始化), 1 (待审批), 2 (拒绝), 3 (通过)
        // 1. 查询所有审批阶段，计算最大阶段数，前一个阶段数，且判断当前阶段的状态为待审批状态 1 才能继续处理
        // 2. 移动附件的临时文件到文件仓库
        // 3. 如果审批通过:
        //    3.1 更新当前审批阶段的状态为通过
        //    3.2 如果是最后一阶段，则审批通过
        //    3.2 如果不是最后一阶段，则修改下一阶段的状态为待审批，且设置其审批员
        // 4. 如果审批被拒绝:
        //    4.1 更新当前审批项的状态为拒绝
        //    4.2 如果是第一阶段，则审批不通过
        //    4.3 如果不是第一阶段，则修改上一阶段审批项的状态为待审批

        //    2.2 统计最大的阶段数和当前阶段的审批项数量、通过的审批项数量、审批员数量
        //    2.3 当前阶段的审批项都为通过时:
        //        2.3.1 如果是最后一阶段，则审批通过
        //        2.3.2 如果不是最后一阶段，则修改下一阶段的所有审批项的状态为待审批
        // 3. 如果审批被拒绝:
        //    3.1 更新当前审批项的状态为拒绝
        //    3.2 如果是第一阶段，则审批不通过
        //    3.3 如果不是第一阶段，更新当前阶段待审批状态的审批项状态为初始化, 上一阶段审批项的状态为待审批

        // [1] 查询所有审批阶段，计算最大阶段数，前一个阶段数，且判断当前阶段的状态为待审批状态 1 才能继续处理
        List<AuditStep> allSteps = auditMapper.findAuditStepsByAuditId(auditId);
        AuditStep currentStep = allSteps.stream().filter(s -> s.getStep() == step).findFirst().orElse(null);
        int maxStep  = allSteps.stream().mapToInt(AuditStep::getStep).max().orElse(Integer.MAX_VALUE);
        int prevStep = step - 1;
        int nextStep = step + 1;

        if (currentStep == null) {
            log.warn("[结束] 审批审批阶段: 审批 [{}], 阶段 [{}]，阶段不存在", auditId, step);
            return;
        }

        // 当前阶段的状态为待审批状态 1 才能继续处
        if (currentStep.getState() != AuditStep.STATE_WAIT) {
            log.warn("[结束] 审批审批阶段: 审批 [{}], 阶段 [{}]，状态不为待审批", auditId, step);
            return;
        }

        log.info("[开始] 审批审批阶段: 审批 [{}], 阶段 [{}]", auditId, step);

        // [2] 移动附件的临时文件到文件仓库
        repoFileService.moveTempFileToRepo(attachmentId);

        if (accepted) {
            // [3] 如果审批通过
            // [3.1] 更新当前审批项的状态为通过
            auditMapper.acceptOrRejectAuditStep(auditId, step, AuditStep.STATE_ACCEPTED, comment, attachmentId);
            log.info("[通过] 审批审批阶段，通过: 审批 [{}], 阶段 [{}]", auditId, step);

            if (step == maxStep) {
                // [3.2] 如果是最后一阶段，则审批通过
                auditMapper.updateAuditState(auditId, Audit.STATUS_ACCEPTED);
                helper.acceptTarget(auditId, currentStep.getTargetId(), currentStep.getType());
                log.info("[通过] 审批审批阶段，通过审批: 审批 [{}]", auditId);
            } else {
                // [3.3] 如果不是最后一阶段，则修改下一阶段的状态为待审批，且设置其审批员
                auditMapper.updateAuditStepState(auditId, nextStep, AuditStep.STATE_WAIT);
                auditMapper.updateAuditStepAuditor(auditId, nextStep, nextStepAuditorId);
                log.info("[通过] 审批审批阶段，下一阶段状态修改为待审批: 审批 [{}], 阶段 [{}]", auditId, nextStep);
            }
        } else {
            // [4] 如果审批被拒绝
            // [4.1] 更新当前审批项的状态为拒绝
            auditMapper.acceptOrRejectAuditStep(auditId, step, AuditStep.STATE_REJECTED, comment, attachmentId);
            log.info("[通过] 审批审批阶段，拒绝: 审批 [{}], 阶段 [{}]", auditId, step);

            if (step == 1) {
                // [4.2] 如果是第一阶段，则审批不通过
                auditMapper.updateAuditState(auditId, Audit.STATUS_REJECTED);
                helper.rejectTarget(auditId, currentStep.getTargetId(), currentStep.getType());
                log.info("[拒绝] 审批审批阶段，通过审批: 审批 [{}]", auditId);
            } else {
                // [4.3] 如果不是第一阶段，则修改上一阶段审批项的状态为待审批
                auditMapper.updateAuditStepState(auditId, prevStep, AuditStep.STATE_WAIT);
                log.info("[拒绝] 审批审批项，前一阶段审批项状态修改为待审批: 审批 [{}], 阶段 [{}]，前一阶段: [{}]", auditId, step, prevStep);
            }
        }

        log.info("[结束] 审批审批项: 审批 [{}], 阶段 [{}]", auditId, step);
    }

    /**
     * 撤销审批阶段，要求被撤销阶段的状态为已通过，且下一阶段的状态为待审批
     *
     * @param auditId 审批 ID
     * @param step    审批阶段
     */
    @Transactional(rollbackFor = Exception.class)
    public void recallAuditStep(long auditId, int step) {
        // 1. 查询所有审批阶段，取得当前和下一个审批阶段
        // 2. 满足条件时进行撤销: 被撤销阶段的状态为已通过，且下一阶段的状态为待审批
        //    2.1 修改被撤销阶段的状态为待审批
        //    2.2 下一阶段的状态为初始化，审批员为 0

        // [1] 查询所有审批阶段，取得当前和下一个审批阶段
        List<AuditStep> allSteps = auditMapper.findAuditStepsByAuditId(auditId);
        AuditStep currStep = allSteps.stream().filter(s -> s.getStep() == step).findFirst().orElse(null);
        AuditStep nextStep = allSteps.stream().filter(s -> s.getStep() == step + 1).findFirst().orElse(null);

        if (currStep == null || nextStep == null) {
            return;
        }

        // [2] 满足条件时进行撤销: 被撤销阶段的状态为已通过，且下一阶段的状态为待审批
        if (currStep.getState() == AuditStep.STATE_ACCEPTED && nextStep.getState() == AuditStep.STATE_WAIT) {
            log.info("[开始] 撤回审批: 审批 [{}], 阶段 [{}]", auditId, step);

            // [2.1] 修改被撤销阶段的状态为待审批
            auditMapper.updateAuditStepState(auditId, currStep.getStep(), AuditStep.STATE_WAIT);

            // [2.2] 下一阶段的状态为初始化，审批员为 0
            auditMapper.updateAuditStepState(auditId, nextStep.getStep(), AuditStep.STATE_INIT);
            auditMapper.updateAuditStepAuditor(auditId, nextStep.getStep(), 0L);

            log.info("[结束] 撤回审批: 审批 [{}], 阶段 [{}]", auditId, step);
        }
    }
}
