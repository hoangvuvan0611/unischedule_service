package com.example.unischeduleservice.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author vuvanhoang
 * @created 28/10/25 22:03
 * @project unischedule_service
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData<T> {
    private int status;
    private String message;
    private T Data;

    public static <T> ResponseData<T> success(T data) {
        return new ResponseData<>(200, "success", data);
    }

    public static <T> ResponseData<T> error(int status, String message) {
        return new ResponseData<>(status, message, null);
    }
}
