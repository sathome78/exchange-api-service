package me.exrates.service;

import me.exrates.model.*;
import me.exrates.model.dto.RefFilterData;
import me.exrates.model.dto.RefsListContainer;
import me.exrates.model.dto.onlineTableDto.MyReferralDetailedDto;
import me.exrates.model.enums.ReferralTransactionStatusEnum;
import me.exrates.model.vo.CacheData;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ReferralService {

    String generateReferral(String userEmail);

    Optional<Integer> reduceReferralRef(String ref);

    void processReferral(ExOrder exOrder, final BigDecimal commissionAmount, Currency currency, int userId);

    List<ReferralTransaction> findAll(int userId);

    List<ReferralLevel> findAllReferralLevels();

    String getParentEmail(int childId);

    Integer getReferralParentId(int childId);

    int updateReferralLevel(int level, int oldLevelId, BigDecimal percent);

    void bindChildAndParent(int childUserId, int parentUserId);

    /**
     * Returns the list of commissions charges for user
     * Used for displaying in History page
     * @param cacheData stores the cach params and is used for caching result
     * @param email is user email. Used as the user identifier
     * @param offset used for pagination
     * @param limit used for pagination
     * @param locale used for formatting number
     * @return list of commissions
     */
    List<MyReferralDetailedDto> findAllMyReferral(CacheData cacheData, String email, Integer offset, Integer limit, Locale locale);

    List<MyReferralDetailedDto> findAllMyReferral(String email, Integer offset, Integer limit, Locale locale);

    List<Integer> getChildrenForParentAndBlock(Integer parentId);

    void updateReferralParentForChildren(User user);

    RefsListContainer getRefsContainerForReq(String action, Integer userId, int profitUserId,
                                             int onPage, int page, RefFilterData refFilterData);

    List<String> getRefsListForDownload(int profitUser, RefFilterData filterData);

    @Transactional
    void setRefTransactionStatus(ReferralTransactionStatusEnum status, int refTransactionId);
}
