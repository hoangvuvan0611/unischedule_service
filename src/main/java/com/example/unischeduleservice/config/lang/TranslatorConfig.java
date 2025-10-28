package com.example.unischeduleservice.config.lang;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author vuvanhoang
 * @created 28/10/25 22:09
 * @project unischedule_service
 */
@Component
public class TranslatorConfig {
    private static ResourceBundleMessageSource messageSource;

    public TranslatorConfig(@Autowired ResourceBundleMessageSource messageSource) {
        TranslatorConfig.messageSource = messageSource;
    }

    public static String toLocale(String message, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(message, args, locale);
    }
}
