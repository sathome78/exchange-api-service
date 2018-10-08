package me.exrates.openapi.api;

import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class FeignConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            int status = response.status();
            if (status == 400) {
                String body = "Bad request";
                try {
                    body = IOUtils.toString(response.body().asReader());
                } catch (Exception ignored) {}
                HttpHeaders httpHeaders = new HttpHeaders();
                response.headers().forEach((k, v) -> httpHeaders.add("feign-" + k, StringUtils.join(v,",")));
                return new FeignBadResponseWrapper(status, httpHeaders, body);
            } else {
                return new RuntimeException("Response Code " + status);
            }
        };
    }
}
