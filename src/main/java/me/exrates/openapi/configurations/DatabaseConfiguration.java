package me.exrates.openapi.configurations;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@Configuration
public class DatabaseConfiguration {

//    @Bean(name = "masterHikariDataSource")
//    public DataSource masterHikariDataSource() {
//        HikariConfig hikariConfig = new HikariConfig();
//        hikariConfig.setDriverClassName(dbMasterClassname);
//        hikariConfig.setJdbcUrl(dbMasterUrl);
//        hikariConfig.setUsername(dbMasterUser);
//        hikariConfig.setPassword(dbMasterPassword);
//        hikariConfig.setMaximumPoolSize(50);
//        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
//        Flyway flyway = new Flyway();
//        flyway.setDataSource(dataSource);
//        flyway.setBaselineOnMigrate(true);
//        flyway.migrate();
//        return dataSource;
//    }
//
//    @Bean(name = "slaveHikariDataSource")
//    public DataSource slaveHikariDataSource() {
//        HikariConfig hikariConfig = new HikariConfig();
//        hikariConfig.setDriverClassName(dbSlaveClassname);
//        hikariConfig.setJdbcUrl(dbSlaveUrl);
//        hikariConfig.setUsername(dbSlaveUser);
//        hikariConfig.setPassword(dbSlavePassword);
//        hikariConfig.setMaximumPoolSize(50);
//        hikariConfig.setReadOnly(true);
//        return new HikariDataSource(hikariConfig);
//    }
//
//    @Primary
//    @DependsOn("masterHikariDataSource")
//    @Bean(name = "masterTemplate")
//    public NamedParameterJdbcTemplate masterNamedParameterJdbcTemplate(@Qualifier("masterHikariDataSource") DataSource dataSource) {
//        return new NamedParameterJdbcTemplate(dataSource);
//    }
//
//    @DependsOn("slaveHikariDataSource")
//    @Bean(name = "slaveTemplate")
//    public NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate(@Qualifier("slaveHikariDataSource") DataSource dataSource) {
//        return new NamedParameterJdbcTemplate(dataSource);
//    }
//
//    @Primary
//    @DependsOn("masterHikariDataSource")
//    @Bean
//    public JdbcTemplate jdbcTemplate(@Qualifier("masterHikariDataSource") DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }
//
//    @Primary
//    @Bean(name = "masterTxManager")
//    public PlatformTransactionManager masterPlatformTransactionManager() {
//        return new DataSourceTransactionManager(masterHikariDataSource());
//    }
//
//    @Bean(name = "slaveTxManager")
//    public PlatformTransactionManager slavePlatformTransactionManager() {
//        return new DataSourceTransactionManager(slaveHikariDataSource());
//    }
}
