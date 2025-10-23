package com.example.unischeduleservice.config.base;

import com.example.unischeduleservice.constants.Const;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

/**
 * @author vuvanhoang
 * @created 23/10/25 23:52
 * @project unischedule_service
 */
@Configuration
public class SysConfig {
    @PostConstruct
    public void  init() {
        TimeZone.setDefault(TimeZone.getTimeZone(Const.TIME_ZONE));
    }
}
