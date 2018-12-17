package me.exrates.openapi.dao.impl;


import me.exrates.openapi.dao.YandexMoneyMerchantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class YandexMoneyMerchantDaoImpl implements YandexMoneyMerchantDao {

    @Autowired
    @Qualifier("masterHikariDataSource")
    private DataSource dataSource;

    private static final String YMONEY_TABLE = "YANDEX_MONEY_MERCHANT";


}