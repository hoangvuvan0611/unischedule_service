package com.example.unischeduleservice.utils;

import com.example.unischeduleservice.config.lang.TranslatorConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author vuvanhoang
 * @created 28/10/25 22:10
 * @project unischedule_service
 */
@Component
public class TranslatorUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static String toLocale(String message, Object... args) {
        return TranslatorConfig.toLocale(message, args);
    }
}
