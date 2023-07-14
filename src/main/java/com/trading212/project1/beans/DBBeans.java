package com.trading212.project1.beans;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
public class DBBeans {

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.main")
    public HikariDataSource hikariDataSource() {
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        return new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    @Bean
    public JdbcTemplate jdbcTemplate(HikariDataSource hikariDataSource) {
        return new JdbcTemplate(hikariDataSource);
    }


    @Value("${spring.influx.url}")
    private String influxUrl;

    @Value("${spring.influx.token}")
    private String token;

    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(influxUrl, token.toCharArray());
    }


}
