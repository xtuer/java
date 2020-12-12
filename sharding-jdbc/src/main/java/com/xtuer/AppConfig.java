package com.xtuer;

import org.apache.shardingsphere.orchestration.center.config.OrchestrationConfiguration;
import org.apache.shardingsphere.shardingjdbc.orchestration.api.OrchestrationShardingDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.orchestration.api.yaml.YamlOrchestrationShardingDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@Configuration
public class AppConfig {
    @Bean
    public DataSource dataSource() throws IOException, SQLException {
        File yamlCfg = new File(new ClassPathResource("sharding-jdbc.yml").getURI());
        DataSource dataSource = YamlOrchestrationShardingDataSourceFactory.createDataSource(yamlCfg);

        return dataSource;
    }
}
