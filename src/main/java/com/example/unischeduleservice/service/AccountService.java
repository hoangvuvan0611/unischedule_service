package com.example.unischeduleservice.service;

import com.example.unischeduleservice.models.Account;

/**
 * @author vuvanhoang
 * @created 25/10/25 07:35
 * @project unischedule_service
 */
public interface AccountService {
    Account getRandom();
    Account findByUsername(String username);
    void saveNewAccount(Account account);
}
