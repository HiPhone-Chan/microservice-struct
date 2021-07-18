package com.chf.service1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import com.chf.core.config.properties.ConfigProperties;
import com.chf.core.constants.AuthoritiesConstants;
import com.chf.core.security.SpringSecurityAuditorAware;
import com.chf.core.security.jwt.JWTConfigurer;
import com.chf.core.security.jwt.TokenProvider;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;

    private final SecurityProblemSupport problemSupport;

    private final ConfigProperties configProperties;

    public SecurityConfiguration(CorsFilter corsFilter, SecurityProblemSupport problemSupport,
            ConfigProperties configProperties) {
        super();
        this.corsFilter = corsFilter;
        this.problemSupport = problemSupport;
        this.configProperties = configProperties;
    }

    @Bean
    public SpringSecurityAuditorAware springSecurityAuditorAware() {
        return new SpringSecurityAuditorAware();
    }

    @Bean
    public TokenProvider tokenProvider(ConfigProperties configProperties) {
        return new TokenProvider(configProperties);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**").antMatchers("/h2-console/**").antMatchers("/test/**")
                .antMatchers("/pages/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(problemSupport).accessDeniedHandler(problemSupport).and()
                .headers().frameOptions().disable().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers("/resource/**").permitAll().antMatchers("/openapi/**").permitAll()
                .antMatchers("/api/authenticate").permitAll().antMatchers("/api/**").authenticated()
                .antMatchers("/management/health").permitAll().antMatchers("/management/info").permitAll()
                .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN).and()
                .apply(new JWTConfigurer(tokenProvider(configProperties)));
    }

}
