package com.xtuer.controller;

import com.xtuer.bean.Page;
import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.bean.User;
import com.xtuer.bean.audit.Audit;
import com.xtuer.bean.audit.AuditConfig;
import com.xtuer.bean.audit.AuditStep;
import com.xtuer.bean.audit.AuditType;
import com.xtuer.mapper.AuditMapper;
import com.xtuer.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审批的控制器
 */
@RestController
public class AuditController {
    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private AuditService auditService;

    /**
     * 获取所有的审批配置
     *
     * 网址: http://localhost:8080/api/audit-configs
     * 参数: 无
     *
     * @return payload 为审批配置的数组
     */
    @GetMapping(Urls.API_AUDIT_CONFIGS)
    public Result<List<AuditConfig>> findAuditConfigs() {
        return Result.ok(auditMapper.findAuditConfigs());
    }

    /**
     * 查询指定类型的审批配置
     *
     * 网址: http://localhost:8080/api/audit-configs/of-type/{type}
     * 参数: 无
     *
     * @param type 审批的类型
     * @return payload 为查询到的审批配置
     */
    @GetMapping(Urls.API_AUDIT_CONFIGS_BY_TYPE)
    public Result<AuditConfig> findAuditConfigByType(@PathVariable AuditType type) {
        AuditConfig config = auditMapper.findAuditConfigByType(type);
        return Result.single(config);
    }

    /**
     * 插入或者更新审批配置
     *
     * 网址: http://localhost:8080/api/audit-configs
     * 参数: 无
     * 请求体: 审批配置的数组
     *
     * @param configs 审批配置数组
     */
    @PutMapping(Urls.API_AUDIT_CONFIGS)
    public Result<Boolean> upsertAuditConfigs(@RequestBody List<AuditConfig> configs) {
        auditService.upsertAuditConfigs(configs);
        return Result.ok();
    }

    /**
     * 查询审批
     *
     * 网址: http://localhost:8080/api/audits/{auditId}
     * 参数: 无
     *
     * @param auditId  审批 ID
     * @return payload 为审批
     */
    @GetMapping(Urls.API_AUDITS_BY_ID)
    public Result<Audit> findAudit(@PathVariable long auditId) {
        return Result.single(auditService.findAudit(auditId));
    }

    /**
     * 查询审批目标的审批
     *
     * 网址: http://localhost:8080/api/audits/of-target/{targetId}
     * 参数: 无
     *
     * @param targetId 审批目标的 ID
     * @return payload 为审批
     */
    @GetMapping(Urls.API_AUDITS_BY_TARGET)
    public Result<Audit> findAuditOfTarget(@PathVariable long targetId) {
        return Result.single(auditService.findAuditOfTarget(targetId));
    }

    /**
     * 查询审批:
     *      * 审批申请人 ID 大于 0，则查询此审批申请人发起的审批
     *      * state 为 -1 时查询所有符合条件的审批，否则查询此状态的审批
     *
     * 网址: http://localhost:8080/api/audits
     * 参数:
     *      applicantId [可选]: 审批申请人 ID
     *      state       [可选]: 审批状态
     *      pageNumber  [可选]: 页码
     *      pageSize    [可选]: 数量
     *
     * @param applicantId 审批申请人 ID
     * @param state       审批状态
     * @param page        分页对象
     * @return payload 为审批数组
     */
    @GetMapping(Urls.API_AUDITS)
    public Result<List<Audit>> findAudits(
            @RequestParam(required = false, defaultValue = "0") long applicantId,
            @RequestParam(required = false, defaultValue = "-1") int state,
            Page page) {
        return Result.ok(auditMapper.findAuditsByApplicantIdAndState(applicantId, state, page));
    }

    /**
     * 查询审批阶段:
     *     * 审批员 ID 大于 0，则查询此审批员收到的审批
     *     * 申请人 ID 大于 0，则查询此人发起的审批
     *     * state 为 -1 时查询所有符合条件的审批，否则查询此状态的审批
     *
     * 网址: http://localhost:8080/api/audit-steps?auditorId=1
     * 参数:
     *      applicantId [可选]: 审批申请人 ID
     *      auditorId   [可选]: 审批员 ID
     *      state       [可选]: 审批项状态
     *      pageNumber  [可选]: 页码
     *      pageSize    [可选]: 数量
     *
     * @return payload 为审批阶段数组
     */
    @GetMapping(Urls.API_AUDIT_STEPS)
    public Result<List<AuditStep>> findAuditSteps(
            @RequestParam(required = false, defaultValue = "0") long applicantId,
            @RequestParam(required = false, defaultValue = "0") long auditorId,
            @RequestParam(required = false, defaultValue = "-1") int state,
            Page page) {
        // 查询审批
        List<AuditStep> items = auditMapper.findAuditStepsByApplicantIdOrAuditorIdAndState(applicantId, auditorId, state, page);
        return Result.ok(items);
    }

    /**
     * 审批: 通过或者拒绝审批阶段
     *
     * 网址: http://localhost:8080/api/audits/{auditId}/steps/{step}/accept
     * 参数:
     *      accepted     (必要): 为 true 表示通过，false 表示拒绝
     *      comment      [可选]: 审批意见
     *      attachmentId [可选]: 附件 ID
     *      nextStepAuditorId [必要]: 下一阶段审批员 ID
     *
     * @param auditId  审批 ID
     * @param step     审批阶段
     * @param accepted true 为通过审批，false 为拒绝审批
     * @param comment  审批意见
     * @param attachmentId 附件 ID
     * @param nextStepAuditorId 下一阶段审批员 ID
     */
    @PutMapping(Urls.API_AUDIT_STEPS_ACCEPT)
    public Result<Boolean> acceptAuditStep(@PathVariable long auditId,
                                           @PathVariable int  step,
                                           @RequestParam boolean accepted,
                                           @RequestParam(required = false) String comment,
                                           @RequestParam(required = false) long attachmentId,
                                           @RequestParam long nextStepAuditorId) {
        auditService.acceptAuditStep(auditId, step, accepted, comment, attachmentId, nextStepAuditorId);
        return Result.ok();
    }

    /**
     * 审批: 撤销审批阶段
     *
     * 网址: http://localhost:8080/api/audits/{auditId}/steps/{step}/recall
     * 参数: 无
     *
     * @param auditId  审批 ID
     * @param step     审批阶段
     */
    @PutMapping(Urls.API_AUDIT_STEPS_RECALL)
    public Result<Boolean> recallAuditStep(@PathVariable long auditId, @PathVariable int  step) {
        auditService.recallAuditStep(auditId, step);
        return Result.ok();
    }

    /**
     * 查询指定审批类型第 step 阶段的审批员
     *
     * 网址: http://localhost:8080/api/auditors
     * 参数:
     *      type (必要): 审批类型
     *      step (必要): 审批阶段
     *
     * @param type 审批类型
     * @param step 审批阶段
     * @return payload 为审批员数组
     */
    @GetMapping(Urls.API_AUDITORS)
    public Result<List<User>> findAuditors(@RequestParam("type") AuditType type, int step) {
        return Result.ok(auditService.findAuditorsByTypeAndStep(type, step));
    }

    /**
     * 统计待传入的用户审批阶段数量
     *
     * 网址: http://localhost:8080API_WAITING_AUDIT_STEPS_COUNT_BY_USER_ID
     * 参数: 无
     *
     * @param userId 用户 ID
     * @return payload 为待审批阶段数量
     */
    @GetMapping(Urls.API_WAITING_AUDIT_STEPS_COUNT_BY_USER_ID)
    public Result<Integer> countWaitingAuditStepsByUserId(@PathVariable long userId) {
        return Result.ok(auditMapper.countWaitingAuditStepsByUserId(userId));
    }
}
