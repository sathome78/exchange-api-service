package me.exrates.service.api;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import me.exrates.service.exception.ExchangeApiException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

@PropertySource(value = {"classpath:/external-apis.properties"})
@Slf4j
@Component
public class ExchangeApi {

    private final String url;

    private final RestTemplate restTemplate;

    @Autowired
    public ExchangeApi(@Value("${api.exchange.url}") String url,
                       @Value("${api.exchange.username}") String username,
                       @Value("${api.exchange.password}") String password) {
        this.url = url;
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
    }

    public Map<String, Pair<BigDecimal, BigDecimal>> getRates() {
        ResponseEntity<ExchangeData> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(url, ExchangeData.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ExchangeApiException("Exchange server is not available");
            }
        } catch (Exception ex) {
            log.warn("Exchange service did not return valid data: server not available");
            return Collections.emptyMap();
        }
        ExchangeData body = responseEntity.getBody();
        return nonNull(body) && nonNull(body.rates) && !body.rates.isEmpty()
                ? body.rates.entrySet().stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        entry -> Pair.of(BigDecimal.valueOf(entry.getValue().usdRate), BigDecimal.valueOf(entry.getValue().btcRate))))
                : Collections.emptyMap();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class ExchangeData {

        Map<String, Rates> rates = Maps.newTreeMap();

        @JsonAnySetter
        void setRates(String key, Rates value) {
            rates.put(key, value);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Rates {

        @JsonProperty("usd_rate")
        double usdRate;
        @JsonProperty("btc_rate")
        double btcRate;
    }
}
