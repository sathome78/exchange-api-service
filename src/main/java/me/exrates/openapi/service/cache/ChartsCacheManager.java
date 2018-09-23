package me.exrates.openapi.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.component.StompMessenger;
import me.exrates.openapi.model.chart.ChartTimeFrame;
import me.exrates.openapi.model.dto.CandleChartItemDto;
import me.exrates.openapi.model.dto.CandleDto;
import me.exrates.openapi.service.OrderService;
import me.exrates.openapi.service.events.ChartCacheUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Log4j2(topic = "cache")
@Component
public class ChartsCacheManager {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private StompMessenger stompMessenger;
    @Autowired
    private ObjectMapper objectMapper;


    private Map<Integer, Map<String, ChartCacheUnit>> cacheMap = new ConcurrentHashMap<>();

    @Async
    public void onUpdateEvent(int pairId) {
        List<ChartTimeFrame> allIntervals = orderService.getChartTimeFrames();
        allIntervals.forEach(p -> setNeedUpdate(pairId, p));
    }


    private void setNeedUpdate(Integer pairId, ChartTimeFrame timeFrame) {
        ChartCacheUnit cacheUnit = getRequiredCache(pairId, timeFrame);
        cacheUnit.setNeedToUpdate();
    }

    private ChartCacheUnit getRequiredCache(Integer pairId, ChartTimeFrame timeFrame) {
        return cacheMap.computeIfAbsent(pairId, p -> {
            Map<String, ChartCacheUnit> map = new ConcurrentHashMap<>();
            orderService.getChartTimeFrames().forEach(i -> {
                map.put(i.getResolution().toString(), new ChartCacheUnit(pairId,
                        i,
                        orderService,
                        eventPublisher)
                );
            });
            return map;
        }).get(timeFrame.getResolution().toString());
    }


    @Async
    @EventListener
    void handleChartUpdate(ChartCacheUpdateEvent event) {
        List<CandleChartItemDto> data = (List<CandleChartItemDto>) event.getSource();
        String dataToSend = prepareDataToSend(data);
        stompMessenger.sendChartData(event.getPairId(),
                event.getTimeFrame().getResolution().toString(),
                dataToSend);
    }

    private String prepareDataToSend(List<CandleChartItemDto> data) {
        List<CandleDto> resultData = data.stream().map(CandleDto::new).collect(Collectors.toList());
        try {
            return objectMapper.writeValueAsString(resultData);
        } catch (JsonProcessingException e) {
            log.error(e);
            return null;
        }
    }
}
