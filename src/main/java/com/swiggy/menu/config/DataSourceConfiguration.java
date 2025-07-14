package com.swiggy.menu.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.hikari.connectionTimeout}")
    private long connectionTimeout;

    @Value("${spring.hikari.idleTimeout}")
    private long idleTimeout;

    @Value("${spring.hikari.maxLifetime}")
    private long maxLifetime;

    @Value("${spring.hikari.minimumIdle}")
    private int minimumIdle;

    @Value("${spring.hikari.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${spring.hikari.autoCommit}")
    private boolean autoCommit;

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfiguration.class);

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);

        // HikariCP specific configurations
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setMinimumIdle(minimumIdle);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setAutoCommit(autoCommit);

        log.info("Configuring HikariCP with maximumPoolSize: {}, minimumIdle: {}", maximumPoolSize, minimumIdle);

        return new HikariDataSource(config);
    }
}