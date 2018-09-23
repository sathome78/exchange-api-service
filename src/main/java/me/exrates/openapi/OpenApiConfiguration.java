package me.exrates.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.configurations.DatabaseConfiguration;
import me.exrates.openapi.configurations.MailConfiguration;
import me.exrates.openapi.configurations.RestTemplateConfiguration;
import me.exrates.openapi.configurations.TwitterConfiguration;
import me.exrates.openapi.configurations.WebSecurityConfiguration;
import me.exrates.openapi.configurations.ZeroMQConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@ComponentScan
@Import({
        DatabaseConfiguration.class,
        WebSecurityConfiguration.class,
        TwitterConfiguration.class,
        ZeroMQConfiguration.class,
        MailConfiguration.class,
        RestTemplateConfiguration.class
})
public class OpenApiConfiguration implements WebMvcConfigurer {

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
