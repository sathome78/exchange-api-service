package me.exrates.service.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by maks on 20.06.2017.
 */
@Service
public class RateLimitService  {


    private static final int TIME_LIMIT_SECONDS = 3600;

    private Map<String, CopyOnWriteArrayList<LocalDateTime>> map = new ConcurrentHashMap<>();

    @Scheduled(cron = "* 5 * * * *")
    public void clearExpiredRequests() {
        new HashMap<>(map).forEach((k, v)-> {
            if (v.stream().filter(p->p.isAfter(LocalDateTime.now().minusSeconds(TIME_LIMIT_SECONDS))).count() == 0) {
                map.remove(k);
            }
        });
    }


}