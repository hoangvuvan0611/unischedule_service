package com.example.unischeduleservice.controller;

import com.example.unischeduleservice.models.Account;
import com.example.unischeduleservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author vuvanhoang
 * @created 25/10/25 07:35
 * @project unischedule_service
 */
@RestController
@RequestMapping(path = "/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping(path = "/random")
    public Account getRandom() {
        return accountService.getRandom();
    }
}
