package me.exrates.openapi.configurations;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfiguration {

    public static final String CACHE_COIN_MARKET = "cache.coin.market";

    @Bean
    @Qualifier(CACHE_COIN_MARKET)
    public Cache cacheCoinmarket() {
        return new CaffeineCache(CACHE_COIN_MARKET, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }
}
