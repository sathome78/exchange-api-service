package me.exrates.openapi.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

@Configuration
public class SecurityConfig {
    @Bean(name = "ExratesSessionRegistry")
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
