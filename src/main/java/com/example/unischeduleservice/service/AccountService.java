package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.AccountBatchReportDTO;
import com.example.unischeduleservice.dto.AccountBatchStatsDTO;
import com.example.unischeduleservice.dto.AccountBatchStatsSummaryDTO;
import com.example.unischeduleservice.dto.AccountBatchSummaryDTO;
import com.example.unischeduleservice.dto.AccountReportDTO;
import com.example.unischeduleservice.dto.AccountStatisticsDTO;
import com.example.unischeduleservice.models.Account;

import java.time.LocalDateTime;
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
    
    // Report methods
    AccountStatisticsDTO getAccountStatistics();
    List<AccountReportDTO> getAccountsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<AccountReportDTO> getRecentAccounts(int limit);
    List<AccountReportDTO> getAllAccountsForReport();
    
    // Batch reporting methods (with details)
    AccountBatchSummaryDTO getAccountBatchSummary();
    AccountBatchReportDTO getAccountBatchByCode(String batchCode);
    List<AccountBatchReportDTO> getAllAccountBatches();
    List<String> getAllBatchCodes();
    
    // Batch stats methods (statistics only, no records)
    AccountBatchStatsSummaryDTO getAccountBatchStatsSummary();
    List<AccountBatchStatsDTO> getAllAccountBatchStats();
    AccountBatchStatsDTO getAccountBatchStatsByCode(String batchCode);
}
