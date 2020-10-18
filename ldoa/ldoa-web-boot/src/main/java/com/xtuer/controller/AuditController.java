package com.xtuer.controller;

import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.bean.audit.AuditConfig;
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
     * 网址: http://localhost:8080/api/audits
     * 参数: 无
     *
     * @return payload 为审批配置的数组
     */
    @GetMapping(Urls.API_AUDITS)
    public Result<List<AuditConfig>> findAuditConfigs() {
        return Result.ok(auditMapper.findAuditConfigs());
    }

    /**
     * 查询指定类型的审批配置
     *
     * 网址: http://localhost:8080/api/audits/{type}
     * 参数: 无
     *
     * @param type 审批的类型
     * @return payload 为查询到的审批配置
     */
    @GetMapping(Urls.API_AUDITS_BY_TYPE)
    public Result<AuditConfig> findAuditConfigByType(@PathVariable AuditType type) {
        AuditConfig config = auditMapper.findAuditConfigByType(type);
        return Result.single(config);
    }

    /**
     * 插入或者更新审批配置
     *
     * 网址: http://localhost:8080/api/audits
     * 参数: 无
     * 请求体: 审批配置的数组
     *
     * @param configs 审批配置数组
     */
    @PutMapping(Urls.API_AUDITS)
    public Result<Boolean> upsertAuditConfigs(@RequestBody List<AuditConfig> configs) {
        auditService.upsertAuditConfigs(configs);
        return Result.ok();
    }
}
