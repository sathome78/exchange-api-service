package me.exrates.openapi.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zeromq.ZMQ;

@Configuration
public class ZeroMQConfiguration {

    @Bean
    public ZMQ.Context zmqContext() {
        return ZMQ.context(1);
    }
}
