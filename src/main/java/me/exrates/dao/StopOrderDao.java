package me.exrates.dao;

import me.exrates.model.CurrencyPair;
import me.exrates.model.PagingData;
import me.exrates.model.StopOrder;
import me.exrates.model.dto.OrderBasicInfoDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminStopOrderFilterData;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;

import java.util.List;
import java.util.Locale;

/**
 * Created by maks on 20.04.2017.
 */
public interface StopOrderDao {
    boolean setStatus(int orderId, OrderStatus status);

    Integer create(StopOrder order);

    boolean setStatusAndChildOrderId(int orderId, Integer childOrderId, OrderStatus status);

    List<StopOrder> getOrdersBypairId(List<Integer> pairIds, OrderStatus opened);

    OrderCreateDto getOrderById(Integer orderId, boolean forUpdate);

    List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, OrderStatus status, OperationType operationType, String scope, Integer offset, Integer limit, Locale locale);

    List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, List<OrderStatus> statuses,
                                                OperationType operationType,
                                                String scope, Integer offset, Integer limit, Locale locale);

    PagingData<List<OrderBasicInfoDto>> searchOrders(AdminStopOrderFilterData adminOrderFilterData, DataTableParams dataTableParams, Locale locale);

    OrderInfoDto getStopOrderInfo(int orderId, Locale locale);
}
