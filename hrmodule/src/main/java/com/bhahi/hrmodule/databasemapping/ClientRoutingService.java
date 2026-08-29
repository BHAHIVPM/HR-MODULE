package com.bhahi.hrmodule.databasemapping;


import com.bhahi.hrmodule.Utils.AESUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientRoutingService {

    private final JdbcTemplate adminJdbcTemplate;

    private final Map<String, DataSource> dataSourceCache = new ConcurrentHashMap<>();

    public ClientRoutingService(
            @Qualifier("adminjdbcTemplate") JdbcTemplate adminJdbcTemplate) {
        this.adminJdbcTemplate = adminJdbcTemplate;
    }

    public DataSource getClientDataSource(String clientId) {

        return dataSourceCache.computeIfAbsent(clientId, this::createDataSource);
    }

    private DataSource createDataSource(String clientId) {
        if (Objects.equals(clientId, "1000")){
            DataSource datasource=adminJdbcTemplate.getDataSource();
            HikariDataSource hikariDataSource = (HikariDataSource) datasource;

            hikariDataSource.setMaximumPoolSize(3);
            hikariDataSource.setMinimumIdle(0);
            hikariDataSource.setIdleTimeout(300000);
            hikariDataSource.setMaxLifetime(550000);
            hikariDataSource.setConnectionTimeout(10000);
            return hikariDataSource;

        }else {

        String sql = """
            SELECT db_domain_url,
                   db_user_name,
                   db_database_password
            FROM client_domain
            WHERE db_client_id = ?
            """;

        Map<String, Object> row = adminJdbcTemplate.queryForMap(sql, clientId);

        String encryptedPassword = (String) row.get("db_database_password");

        String decryptedPassword;
        try {
            decryptedPassword = AESUtils.decrypt(encryptedPassword);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt DB password", e);
        }

        HikariDataSource ds = new HikariDataSource();

        ds.setJdbcUrl((String) row.get("db_domain_url"));
        ds.setUsername((String) row.get("db_user_name"));
        ds.setPassword(decryptedPassword);

        // Pool configuration (IMPORTANT)
        ds.setMaximumPoolSize(3);
        ds.setMinimumIdle(0);
        ds.setIdleTimeout(300000);
        ds.setMaxLifetime(550000);
        ds.setConnectionTimeout(10000);

        return ds;
    }}
}
