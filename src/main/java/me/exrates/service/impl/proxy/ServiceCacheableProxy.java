package me.exrates.service.impl.proxy;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.OrderDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Log4j2
@Transactional(propagation = Propagation.MANDATORY)
public class ServiceCacheableProxy {

  @Autowired
  private OrderDao orderDao;

  @CacheEvict(cacheNames = "orderBuy", key = "#currencyPair.id", condition = "#evictCache", beforeInvocation = true)
  @Cacheable(cacheNames = "orderBuy", key = "#currencyPair.id")
  public List<OrderListDto> getAllBuyOrders(
          CurrencyPair currencyPair,
          UserRole filterRole, Boolean evictCache) {
    log.debug(String.format("\n%s evictCache: %s", currencyPair, evictCache));
    return orderDao.getOrdersBuyForCurrencyPair(currencyPair, filterRole);
  }

  @CacheEvict(cacheNames = "orderSell", key = "#currencyPair.id", condition = "#evictCache", beforeInvocation = true)
  @Cacheable(cacheNames = "orderSell", key = "#currencyPair.id")
  public List<OrderListDto> getAllSellOrders(
          CurrencyPair currencyPair,
          UserRole filterRole, Boolean evictCache) {
    log.debug(String.format("\n%s evictCache: %s", currencyPair, evictCache));
    return orderDao.getOrdersSellForCurrencyPair(currencyPair, filterRole);
  }

}
