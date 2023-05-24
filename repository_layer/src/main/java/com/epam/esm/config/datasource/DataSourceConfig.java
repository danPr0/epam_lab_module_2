package com.epam.esm.config.datasource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Base class for configuring data source.
 *
 * @author Danylo Proshyn
 */

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Value("${datasource.driver-class-name}")
    private String driverClassName;
    @Value("${datasource.url}")
    private String url;
    @Value("${datasource.username}")
    private String username;
    @Value("${datasource.password}")
    private String password;

    @Bean()
    public DataSource mysqlDataSource() {

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        dataSource.setMaxTotal(20);

        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {

        return new JdbcTemplate(mysqlDataSource());
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {

        return new NamedParameterJdbcTemplate(mysqlDataSource());
    }

    @Bean
    public DataSourceTransactionManager txManager() {

        return new DataSourceTransactionManager(mysqlDataSource());
    }
}