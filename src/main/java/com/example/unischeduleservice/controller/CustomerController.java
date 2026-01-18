package com.example.unischeduleservice.controller;

import com.example.unischeduleservice.dto.ReviewDTO;
import com.example.unischeduleservice.dto.base.ResponseData;
import com.example.unischeduleservice.exceptions.CustomException;
import com.example.unischeduleservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(path = "/sendReview")
    public ResponseData<?> sendReviewUniScheduleApp(@RequestBody ReviewDTO review) {
        try {
            customerService.sendReviewUniScheduleApp(review);
            return ResponseData.success(null);
        } catch (CustomException ce) {
            return ResponseData.error(HttpStatus.BAD_REQUEST.value(), ce.getMessage());
        }
    }
}
