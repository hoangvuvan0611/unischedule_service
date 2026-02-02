package com.example.unischeduleservice.controller;

import com.example.unischeduleservice.dto.base.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/report")
@RequiredArgsConstructor
public class ReportController {

    @GetMapping(path = "/findAccountByUsernameAndCreatedAt")
    public ResponseData<?> findAccountByUsernameAndCreatedAt(@RequestParam(required = false) String username,
                                                             @RequestParam LocalDateTime fromDateTime,
                                                             @RequestParam LocalDateTime toDateTime,
                                                             @RequestParam(required = false, defaultValue = "10") Long recordLimit,
                                                             @RequestParam(required = false, defaultValue = "true") Boolean desc) {
        return null;
    }

    @PostMapping
    public ResponseData<?> findInfoAccountInDay() {
        return null;
    }
}
