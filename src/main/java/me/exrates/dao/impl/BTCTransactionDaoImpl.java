package me.exrates.dao.impl;

import me.exrates.dao.BTCTransactionDao;
import me.exrates.dao.UserDao;
import me.exrates.model.BTCTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class BTCTransactionDaoImpl implements BTCTransactionDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private UserDao userDao;

    private final RowMapper<BTCTransaction> btcTransactionRowMapper = (resultSet, i) -> {
        BTCTransaction btcTransaction = new BTCTransaction();
        btcTransaction.setHash(resultSet.getString("hash"));
        btcTransaction.setAmount(resultSet.getBigDecimal("amount"));
        btcTransaction.setTransactionId(resultSet.getInt("transaction_id"));
        Timestamp acceptanceTimeResult = resultSet.getTimestamp("acceptance_time");
        LocalDateTime acceptanceTime = acceptanceTimeResult == null ? null : acceptanceTimeResult.toLocalDateTime();
        btcTransaction.setAcceptance_time(acceptanceTime);
        int acceptance_user_id = resultSet.getInt("acceptance_user_id");
        if (acceptance_user_id != 0){
            btcTransaction.setAcceptanceUser(userDao.getUserById(resultSet.getInt("acceptance_user_id")));
        }

        return btcTransaction;
    };

}