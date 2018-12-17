package me.exrates.openapi.dao;

import me.exrates.openapi.model.Currency;
import me.exrates.openapi.model.CurrencyPair;
import me.exrates.openapi.model.ExOrder;
import me.exrates.openapi.model.PagingData;
import me.exrates.openapi.model.dto.*;
import me.exrates.openapi.model.dto.dataTable.DataTableParams;
import me.exrates.openapi.model.dto.filterData.AdminOrderFilterData;
import me.exrates.openapi.model.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.openapi.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.openapi.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.openapi.model.dto.onlineTableDto.OrderListDto;
import me.exrates.openapi.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.openapi.model.dto.openAPI.*;
import me.exrates.openapi.model.enums.*;
import me.exrates.openapi.model.vo.BackDealInterval;
import me.exrates.openapi.model.vo.OrderRoleInfoForDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public interface OrderDao {

    int createOrder(ExOrder order);

    Optional<BigDecimal> getLastOrderPriceByCurrencyPairAndOperationType(int currencyPairId, int operationTypeId);

    Optional<BigDecimal> getLowestOpenOrderPriceByCurrencyPairAndOperationType(int currencyPairId, int operationTypeId);

    ExOrder getOrderById(int orderid);

    boolean setStatus(int orderId, OrderStatus status);

    boolean updateOrder(ExOrder exOrder);

    List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair, UserRole filterRole);

    void postAcceptedOrderToDB(ExOrder exOrder);

    List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair, UserRole filterRole);

    List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval, LocalDateTime endTime);

    List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, LocalDateTime startTime, int resolutionValue, String resolutionType);

    ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    List<ExOrderStatisticsShortByPairsDto> getOrderStatisticByPairs();

    List<ExOrderStatisticsShortByPairsDto> getOrderStatisticForSomePairs(List<Integer> pairsIds);

    List<CoinmarketApiDto> getCoinmarketData(String currencyPairName);

    OrderInfoDto getOrderInfo(int orderId, Locale locale);

    int searchOrderByAdmin(Integer currencyPair, Integer orderType, String orderDate, BigDecimal orderRate, BigDecimal orderVolume);

    List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(String email, BackDealInterval backDealInterval, Integer limit, CurrencyPair currencyPair);

    OrderCommissionsDto getCommissionForOrder(UserRole userRole);

    CommissionsDto getAllCommissions(UserRole userRole);

    List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, OrderStatus status,
                                                OperationType operationType,
                                                String scope, Integer offset, Integer limit, Locale locale);

    List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, List<OrderStatus> statuses,
                                                OperationType operationType,
                                                String scope, Integer offset, Integer limit, Locale locale);

    OrderCreateDto getMyOrderById(int orderId);

    WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                    OperationType operationType, UserRole userRole);

    boolean lockOrdersListForAcception(List<Integer> ordersList);

    PagingData<List<OrderBasicInfoDto>> searchOrders(AdminOrderFilterData adminOrderFilterData, DataTableParams dataTableParams, Locale locale);

    List<ExOrder> selectTopOrders(Integer currencyPairId, BigDecimal exrate, OperationType orderType, boolean sameRoleOnly, Integer userAcceptorRoleId, OrderBaseType orderBaseType);

    List<UserSummaryOrdersByCurrencyPairsDto> getUserSummaryOrdersByCurrencyPairList(Integer requesterUserId, String startDate, String endDate, List<Integer> roles);

    OrderRoleInfoForDelete getOrderRoleInfo(int orderId);

    List<OrderBookItem> getOrderBookItemsForType(Integer currencyPairId, OrderType orderType);

    List<OrderBookItem> getOrderBookItems(Integer currencyPairId);

    List<OpenOrderDto> getOpenOrders(Integer currencyPairId, OrderType orderType);

    List<TradeHistoryDto> getTradeHistory(Integer currencyPairId, LocalDateTime fromDate, LocalDateTime toDate, Integer limit);

    List<UserOrdersDto> getUserOpenOrders(Integer userId, Integer currencyPairId);

    List<UserOrdersDto> getUserOrdersByStatus(Integer userId, Integer currencyPairId, OrderStatus status, int limit, int offset);

    List<UserTradeHistoryDto> getUserTradeHistoryByCurrencyPair(Integer userId, Integer currencyPairId, LocalDateTime fromDate, LocalDateTime toDate, Integer limit);

    List<ExOrder> getAllOpenedOrdersByUserId(Integer userId);

    List<ExOrder> getOpenedOrdersByCurrencyPair(Integer userId, String currencyPair);

    List<TransactionDto> getOrderTransactions(Integer userId, Integer orderId);

    List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> roles);
}
