package com.bhahi.hrmodule.databasemapping;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:database.properties")
public class DataSourceConfig {


    @Bean
    @ConfigurationProperties("spring.datasource.adm")
    public DataSource adminDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(3);
        ds.setMinimumIdle(0);
        ds.setIdleTimeout(500_000);
        ds.setMaxLifetime(550_000);
        ds.setKeepaliveTime(120_000);
        ds.setConnectionTimeout(10_000);
        return ds;
    }

    @Bean
    @ConfigurationProperties("spring.datasource.dev")
    public DataSource devDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(3);
        ds.setMinimumIdle(0);
        ds.setIdleTimeout(500_000);
        ds.setMaxLifetime(550_000);
        ds.setKeepaliveTime(120_000);
        ds.setConnectionTimeout(10_000);
        return ds;
    }


    @Bean
    @Primary
    public RoutingDataSource routingDataSource(
            @Qualifier("adminDataSource") DataSource defaultDataSource) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();

        targetDataSources.put("default", defaultDataSource);

        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }


    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new JdbcTemplate(routingDataSource);
    }


    @Bean
    public JdbcTemplate adminjdbcTemplate(@Qualifier("adminDataSource") DataSource adminDs) {
        return new JdbcTemplate(adminDs);
    }

    @Bean
    public JdbcTemplate devjdbcTemplate(@Qualifier("devDataSource") DataSource devDS) {
        return new JdbcTemplate(devDS);
    }

}
