package com.xtuer.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建数据源
 */
@Configuration
public class DataSourceConfig {
    // 数据库连接池的配置
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    // 数据源一
    public DataSource masterDataSource(HikariConfig hikariConfig) {
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("root");
        hikariConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/qt?useUnicode=true&characterEncoding=UTF-8&useSSL=false");
        HikariDataSource ds = new HikariDataSource(hikariConfig);

        return ds;
    }

    // 数据源二
    public DataSource slaveDataSource(HikariConfig hikariConfig) {
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("root");
        hikariConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&useSSL=false");
        HikariDataSource ds = new HikariDataSource(hikariConfig);

        return ds;
    }

    // 在 Spring IoC 容器中创建一个 AbstractRoutingDataSource 实现的对象 routingDataSource
    @Bean
    public DataSource routingDataSource(HikariConfig hikariConfig) {
        // [1] 实际的物理数据源
        DataSource masterDataSource = this.masterDataSource(hikariConfig);
        DataSource slaveDataSource  = this.slaveDataSource(hikariConfig);

        // [2] 给每个物理数据源绑定一个名字，把它们传递给 RoutingDataSource
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource);
        targetDataSources.put("slave", slaveDataSource);

        RoutingDataSource rds = new RoutingDataSource();
        rds.setTargetDataSources(targetDataSources);

        // [3] 如果 determineCurrentLookupKey 返回的 key 找不到对应的数据源，则使用这里设置的默认数据源
        rds.setDefaultTargetDataSource(masterDataSource);

        return rds;
    }
}
