package com.chf.service1.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.chf.core.repository.support.JpaExtRepositoryFactoryBean;

@Configuration
@EnableJpaRepositories(basePackages = {
        "com.chf.service1.repository" }, repositoryFactoryBeanClass = JpaExtRepositoryFactoryBean.class)
@EntityScan({ "com.chf.service1", "org.springframework.data.jpa.convert.threeten" })
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {

}