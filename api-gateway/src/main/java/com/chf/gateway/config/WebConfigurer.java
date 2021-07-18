package com.chf.gateway.config;

import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.reactive.ResourceHandlerRegistrationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.WebExceptionHandler;
import org.zalando.problem.spring.webflux.advice.ProblemExceptionHandler;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;

import com.chf.core.config.database.H2ConfigurationHelper;
import com.chf.core.config.properties.ConfigProperties;
import com.chf.core.constants.SystemConstants;
import com.chf.core.web.filter.reactive.CachingHttpHeadersFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebConfigurer implements WebFluxConfigurer {

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    private final ConfigProperties configProperties;

    public WebConfigurer(Environment env, ConfigProperties configProperties) {
        this.configProperties = configProperties;

        if (env.acceptsProfiles(Profiles.of(SystemConstants.PROFILE_DEVELOPMENT))) {
            H2ConfigurationHelper.initH2Console();
        }
    }
     
    @Bean
    public CorsWebFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = configProperties.getCors();
        if (!CollectionUtils.isEmpty(config.getAllowedOrigins())
                || !CollectionUtils.isEmpty(config.getAllowedOriginPatterns())) {
            log.debug("Registering CORS filter");
            source.registerCorsConfiguration("/api/**", config);
            source.registerCorsConfiguration("/management/**", config);
            source.registerCorsConfiguration("/v2/api-docs", config);
            source.registerCorsConfiguration("/v3/api-docs", config);
            source.registerCorsConfiguration("/swagger-resources", config);
            source.registerCorsConfiguration("/swagger-ui/**", config);
            source.registerCorsConfiguration("/*/api/**", config);
            source.registerCorsConfiguration("/services/*/api/**", config);
            source.registerCorsConfiguration("/*/management/**", config);
        }
        return new CorsWebFilter(source);
    }

    // TODO: remove when this is supported in spring-boot
    @Bean
    HandlerMethodArgumentResolver reactivePageableHandlerMethodArgumentResolver() {
        return new ReactivePageableHandlerMethodArgumentResolver();
    }

    // TODO: remove when this is supported in spring-boot
    @Bean
    HandlerMethodArgumentResolver reactiveSortHandlerMethodArgumentResolver() {
        return new ReactiveSortHandlerMethodArgumentResolver();
    }

    @Bean
    @Order(-2) // The handler must have precedence over WebFluxResponseStatusExceptionHandler
               // and Spring Boot's ErrorWebExceptionHandler
    public WebExceptionHandler problemExceptionHandler(ObjectMapper mapper, ProblemHandling problemHandling) {
        return new ProblemExceptionHandler(mapper, problemHandling);
    }

    @Bean
    ResourceHandlerRegistrationCustomizer registrationCustomizer() {
        // Disable built-in cache control to use our custom filter instead
        return registration -> registration.setCacheControl(null);
    }

    @Bean
    @Profile(SystemConstants.PROFILE_PRODUCTION)
    public CachingHttpHeadersFilter cachingHttpHeadersFilter() {
        // Use a cache filter that only match selected paths
        return new CachingHttpHeadersFilter(
                TimeUnit.DAYS.toMillis(configProperties.getHttp().getCache().getTimeToLiveInDays()));
    }
    
}
