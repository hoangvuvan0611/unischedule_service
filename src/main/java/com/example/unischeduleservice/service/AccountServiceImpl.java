package com.example.unischeduleservice.service;

import com.example.unischeduleservice.models.Account;
import com.example.unischeduleservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * @author vuvanhoang
 * @created 25/10/25 07:36
 * @project unischedule_service
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account getRandom() {
        Random random = new Random();
        List<Account> accounts = accountRepository.findAll();
        return accounts.get(random.nextInt(accounts.size()));
    }
}
