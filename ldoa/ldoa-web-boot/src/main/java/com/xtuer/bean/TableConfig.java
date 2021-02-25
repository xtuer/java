package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 表格的配置，例如记录例如某个列的宽度。
 * 为了区分每个用户的表格配置，tableName + userId 作为唯一标志。
 */
@Getter
@Setter
public class TableConfig {
    /**
     * 表名
     */
    private String tableName;

    /**
     * 用户 ID
     */
    private long userId;

    /**
     * 配置: 是一个 JSON 数组的字符串，例如
     * [{ index: 1, width: 100 }, { index: 2, width: 120 }]
     */
    private String config;
}
