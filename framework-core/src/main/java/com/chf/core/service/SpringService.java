package com.chf.core.service;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

public class SpringService implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Object getBean(String beanName) {
        return applicationContext == null ? null
                : (applicationContext.containsBean(beanName) ? applicationContext.getBean(beanName) : null);
    }

    public <T> T getBean(Class<T> beanClass) {
        return applicationContext == null ? null : applicationContext.getBean(beanClass);
    }

    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return applicationContext == null ? null : applicationContext.getBeansWithAnnotation(annotationType);
    }

    public void publishEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }

}
