package com.xtuer.controller;

import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.bean.audit.Audit;
import com.xtuer.bean.audit.AuditConfig;
import com.xtuer.bean.audit.AuditItem;
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
     * 网址: http://localhost:8080/api/audit-configs/{type}
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
     * 查询审批项:
     *     * 审批员 ID 大于 0，则查询此审批员收到的审批
     *     * 申请人 ID 大于 0，则查询此人发起的审批
     *     * status 为 -1 时查询所有符合条件的审批，否则查询此状态的审批
     *
     * 网址: http://localhost:8080/api/audit-items?auditorId=1
     * 参数:
     *      applicantId [可选]: 审批申请人 ID
     *      auditorId   [可选]: 审批员 ID
     *      status      [可选]: 审批项状态
     *
     * @return payload 为审批项数组
     */
    @GetMapping(Urls.API_AUDIT_ITEMS)
    public Result<List<AuditItem>> findAuditItemsByAuditorIdAndStatus(
            @RequestParam(required = false, defaultValue = "0") long applicantId,
            @RequestParam(required = false, defaultValue = "0") long auditorId,
            @RequestParam(required = false, defaultValue = "-1") int status) {
        // 审批员 ID 大于 0，则查询此审批员收到的审批
        if (auditorId > 0) {
            return Result.ok(auditMapper.findAuditItemsByAuditorIdAndStatus(auditorId, status));
        }

        // 申请人 ID 大于 0，则查询此人发起的审批
        if (applicantId > 0) {
            return Result.fail();
        }

        return Result.ok();
    }

    /**
     * 审批: 通过或者拒绝审批项
     *
     * @param auditItemId 审批项 ID
     * @param accepted    true 为通过审批，false 为拒绝审批
     */
    @PutMapping(Urls.API_AUDIT_ITEMS_ACCEPT)
    public Result<Boolean> acceptAuditItem(@PathVariable long auditItemId, @RequestParam boolean accepted) {
        auditService.acceptAuditItem(auditItemId, accepted);
        return Result.ok();
    }
}
