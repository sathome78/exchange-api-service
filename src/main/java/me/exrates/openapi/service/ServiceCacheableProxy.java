package me.exrates.openapi.service;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.NewsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.openapi.dao.NewsDao;
import me.exrates.openapi.dao.OrderDao;
import me.exrates.openapi.service.persistence.dao.NewsDao;
import me.exrates.openapi.service.persistence.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Component
@Log4j2
@Transactional(propagation = Propagation.MANDATORY)
public class ServiceCacheableProxy {

  @Autowired
  private OrderDao orderDao;

  @Autowired
  private NewsDao newsDao;

  @Autowired
  private Twitter twitter;

  @CacheEvict(cacheNames = "currencyPairStatistics", key = "#root.methodName", condition = "#evictCache", beforeInvocation = true)
  @Cacheable(cacheNames = "currencyPairStatistics", key = "#root.methodName")
  public List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairs(Boolean evictCache) {
    log.debug("\nevictCache: " + evictCache);
    return orderDao.getOrderStatisticByPairs();
  }

  @CacheEvict(cacheNames = "orderAccepted", key = "#currencyPair.id", condition = "''.equals(#email) && #evictCache", beforeInvocation = true)
  @Cacheable(cacheNames = "orderAccepted", key = "#currencyPair.id", condition = "''.equals(#email)")
  public List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(
      String email,
      BackDealInterval backDealInterval,
      Integer limit,
      CurrencyPair currencyPair,
      Boolean evictCache) {
    log.debug(String.format("\n%s evictCache: %s", currencyPair, evictCache));
    return orderDao.getOrderAcceptedForPeriod(email, backDealInterval, limit, currencyPair);
  }

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

  @Cacheable(cacheNames = "newsBrief", key = "#locale")
  public List<NewsDto> getNewsBriefList(Integer offset, Integer limit, Locale locale) {
    log.debug(String.format("\nlocale: %s", locale));
    return newsDao.getNewsBriefList(offset, limit, locale);
  }

  @Cacheable(cacheNames = "singlePairStatistics", key = "{#currencyPair.id, #backDealInterval}")
  public ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
    log.debug(String.format("\n%s backDealInterval: %s", currencyPair, backDealInterval));
    return orderDao.getOrderStatistic(currencyPair, backDealInterval);
  }

  @Cacheable(cacheNames = "candleChart", key = "{#currencyPair.id, #backDealInterval}")
  public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
    log.debug(String.format("\n%s backDealInterval: %s", currencyPair, backDealInterval));
    return orderDao.getDataForCandleChart(currencyPair, backDealInterval);
  }

  @Transactional(propagation = Propagation.NEVER)
  @Cacheable(cacheNames = "twitter", key = "#size")
  public List<Tweet> getTwitterTimeline(Integer size) {
    return twitter.timelineOperations().getUserTimeline(size);
  }

}
