package me.exrates.openapi.dao;

import me.exrates.openapi.model.ReferralLevel;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ReferralLevelDao {

    List<ReferralLevel> findAll();

    ReferralLevel findById(int id);

    BigDecimal getTotalLevelsPercent();

    int create(ReferralLevel level);

}
