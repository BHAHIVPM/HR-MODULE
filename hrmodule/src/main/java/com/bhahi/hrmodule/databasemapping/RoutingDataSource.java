package com.bhahi.hrmodule.databasemapping;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RoutingDataSource extends AbstractRoutingDataSource {

    private final Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return DatabaseContextHolder.get();
    }

    public void addDataSource(String key, DataSource dataSource) {

        if (!targetDataSources.containsKey(key)) {
            targetDataSources.put(key, dataSource);
            super.setTargetDataSources(targetDataSources);
            super.afterPropertiesSet();
        }

    }
}
