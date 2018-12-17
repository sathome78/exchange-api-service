package me.exrates.openapi.service.cache;

import me.exrates.openapi.model.dto.CandleChartItemDto;

import java.util.List;

/**
 * Created by Maks on 29.01.2018.
 */
public interface ChartsCacheInterface {

    List<CandleChartItemDto> getData();

    List<CandleChartItemDto> getLastData();

    void setNeedToUpdate();
}
