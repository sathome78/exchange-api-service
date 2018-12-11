package me.exrates.service.stopOrder;

import me.exrates.model.ExOrder;
import me.exrates.model.dto.StopOrderSummaryDto;

import java.math.BigDecimal;
import java.util.NavigableSet;

/**
 * Created by maks on 24.04.2017.
 */
public interface StopOrdersHolder {
    NavigableSet<StopOrderSummaryDto> getSellOrdersForPairAndStopRate(int pairId, BigDecimal rate);

    NavigableSet<StopOrderSummaryDto> getBuyOrdersForPairAndStopRate(int pairId, BigDecimal rate);

    void delete(int pairId, StopOrderSummaryDto summaryDto);

    void addOrder(ExOrder exOrder);
}
