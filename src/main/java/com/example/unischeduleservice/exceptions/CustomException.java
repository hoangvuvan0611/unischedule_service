package com.example.unischeduleservice.exceptions;

import com.example.unischeduleservice.utils.TranslatorUtil;
import lombok.Getter;

/**
 * @author vuvanhoang
 * @created 28/10/25 22:06
 * @project unischedule_service
 */
@Getter
public class CustomException extends RuntimeException {
    private final String errorCode;

    public CustomException(String errorCode, String message, Object... params) {
        super(TranslatorUtil.toLocale(message, params));
        this.errorCode = errorCode;
    }
}
