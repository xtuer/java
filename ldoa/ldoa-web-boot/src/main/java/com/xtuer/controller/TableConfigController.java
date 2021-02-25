package com.xtuer.controller;

import com.xtuer.bean.Result;
import com.xtuer.bean.TableConfig;
import com.xtuer.bean.Urls;
import com.xtuer.mapper.TableConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 表格配置的控制器
 */
@RestController
public class TableConfigController {
    @Autowired
    private TableConfigMapper configMapper;

    /**
     * 查询指定表格的某个用户的配置
     *
     * 网址: http://localhost:8080/api/tables/{tableName}/users/{userId}/config
     * 参数: 无
     *
     * @param tableName 表名
     * @param userId    用户 ID
     * @return payload 为表格的配置
     */
    @GetMapping(Urls.API_TABLE_CONFIG_BY_TABLE_NAME_AND_USER)
    public Result<TableConfig> findTableConfig(@PathVariable String tableName, @PathVariable long userId) {
        return Result.single(configMapper.findTableConfigByTableNameAndUserId(tableName, userId));
    }

    /**
     * 更新或者插入表格的配置
     *
     * 网址: http://localhost:8080/api/tables/{tableName}/users/{userId}/config
     * 参数: config (必要): 配置
     *
     * @param config 表格的配置
     */
    @PutMapping(Urls.API_TABLE_CONFIG_BY_TABLE_NAME_AND_USER)
    public Result<Boolean> upsertTableConfig(@PathVariable String tableName,
                                             @PathVariable long userId,
                                             @RequestParam String config) {
        TableConfig tc = new TableConfig();
        tc.setTableName(tableName);
        tc.setUserId(userId);
        tc.setConfig(config);
        configMapper.upsertTableConfig(tc);

        return Result.ok();
    }
}
