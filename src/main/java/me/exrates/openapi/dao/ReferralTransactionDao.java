package me.exrates.openapi.dao;

import me.exrates.openapi.model.ReferralTransaction;
import me.exrates.openapi.model.dto.onlineTableDto.MyReferralDetailedDto;
import me.exrates.openapi.model.enums.ReferralTransactionStatusEnum;

import java.util.List;
import java.util.Locale;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ReferralTransactionDao {

    List<ReferralTransaction> findAll(int userId);

    ReferralTransaction create(ReferralTransaction referralTransaction);

    List<MyReferralDetailedDto> findAllMyRefferal(String email, Integer offset, Integer limit, Locale locale);

    void setRefTransactionStatus(ReferralTransactionStatusEnum status, int refTransactionId);
}
