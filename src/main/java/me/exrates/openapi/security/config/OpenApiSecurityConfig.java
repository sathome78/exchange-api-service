package me.exrates.openapi.security.config;

import me.exrates.openapi.security.entryPoint.OpenApiAuthenticationEntryPoint;
import me.exrates.openapi.security.filter.OpenApiAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@Order(value = 2)
@EnableWebSecurity
public class OpenApiSecurityConfig extends WebSecurityConfigurerAdapter {


    @Bean(name = "openApiEntryPoint")
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new OpenApiAuthenticationEntryPoint();
    }

    @Bean
    public Filter openApiAuthenticationFilter() throws Exception {
        return new OpenApiAuthenticationFilter("/**", openApiAuthenticationManagerBean());
    }

    public AuthenticationManager openApiAuthenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/openapi/v1/**").authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .addFilterAfter(openApiAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .httpBasic();

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(GET, "/openapi/v1/public/**");
    }
}
