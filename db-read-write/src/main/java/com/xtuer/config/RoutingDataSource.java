package com.xtuer.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源
 */
public class RoutingDataSource extends AbstractRoutingDataSource {
    private int count = 0;

    // 使用此 lookupKey (数据源的名字) 查找对应的数据源
    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceName = count % 2 == 0 ? "master" : "slave";
        System.out.println("获取数据源: " + dataSourceName);
        count++;

        return dataSourceName;
    }
}
