package me.exrates.openapi.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.servlet.http.HttpServletResponse;

@EnableResourceServer
@Configuration
public class ResourcesServerConfiguration extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "api-service";

    @Autowired
    private JedisConnectionFactory connectionFactory;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID).tokenStore(new RedisTokenStore(connectionFactory));
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/public/**").anonymous()
                .antMatchers("/user/**").hasAuthority("TRADE")
                .antMatchers("/orders/**").hasAuthority("TRADE")
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                .accessDeniedHandler((request, response, ex) -> response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                .and()
                .csrf().disable();
    }
}