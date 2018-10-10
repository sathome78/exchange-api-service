package me.exrates.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.configurations.CacheConfiguration;
import me.exrates.openapi.configurations.ResourcesServerConfiguration;
import me.exrates.openapi.configurations.SwaggerConfiguration;
import me.exrates.openapi.configurations.WebSecurityConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Slf4j
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableFeignClients
@ComponentScan
@Import({
        WebSecurityConfiguration.class,
        ResourcesServerConfiguration.class,
        CacheConfiguration.class,
        SwaggerConfiguration.class
})
public class OpenApiConfiguration {

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
