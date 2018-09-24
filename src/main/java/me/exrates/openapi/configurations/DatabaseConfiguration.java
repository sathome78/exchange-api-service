package me.exrates.openapi.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
