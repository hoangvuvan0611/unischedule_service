package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.FullInfoReportOverviewDTO;
import com.example.unischeduleservice.models.Account;

import java.util.List;

/**
 * @author vuvanhoang
 * @created 25/10/25 07:35
 * @project unischedule_service
 */
public interface AccountService {
    Account getRandom();
    Account findByUsername(String username);
    void saveNewAccount(Account account);
    List<Account> getAccountsByNumRecord(int num);
    FullInfoReportOverviewDTO fullInfoReportOverview();
}
