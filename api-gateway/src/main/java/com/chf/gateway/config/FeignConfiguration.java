package com.chf.gateway.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import feign.Logger.Level;

@Configuration
@EnableFeignClients(basePackages = "com.chf.gateway")
@Import(FeignClientsConfiguration.class)
public class FeignConfiguration {

    @Bean
    Level feignLoggerLevel() {
        return Level.BASIC;
    }
}
