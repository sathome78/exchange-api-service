package me.exrates.dao;

import me.exrates.model.CurrencyPair;
import me.exrates.model.StopOrder;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderInfoDto;
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


    OrderInfoDto getStopOrderInfo(int orderId, Locale locale);
}
