package com.xtuer.mapper;

import com.xtuer.bean.TableConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 表格配置的 Mapper
 */
@Mapper
public interface TableConfigMapper {
    /**
     * 更新或者插入表格的配置
     *
     * @param config 配置
     */
    void upsertTableConfig(TableConfig config);

    /**
     * 查询指定表格的某个用户的配置
     *
     * @param tableName 表名
     * @param userId    用户 ID
     * @return 返回查询到的配置，查询不到返回 null
     */
    TableConfig findTableConfigByTableNameAndUserId(String tableName, long userId);
}
