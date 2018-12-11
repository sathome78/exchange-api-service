package me.exrates.dao.impl;

import me.exrates.dao.DashboardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class DashboardDaoImpl implements DashboardDao {

    @Autowired
    @Qualifier("masterHikariDataSource")
    DataSource dataSource;

}
