package com.example.unischeduleservice.controller;

import com.example.unischeduleservice.dto.AccountBatchReportDTO;
import com.example.unischeduleservice.dto.AccountBatchStatsDTO;
import com.example.unischeduleservice.dto.AccountBatchStatsSummaryDTO;
import com.example.unischeduleservice.dto.AccountBatchSummaryDTO;
import com.example.unischeduleservice.dto.AccountReportDTO;
import com.example.unischeduleservice.dto.AccountStatisticsDTO;
import com.example.unischeduleservice.dto.ReportDashboardDTO;
import com.example.unischeduleservice.dto.base.ResponseData;
import com.example.unischeduleservice.service.AccountService;
import com.example.unischeduleservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author vuvanhoang
 * @created 14/03/26 09:29
 * @project unischedule_service
 */
@RestController
@RequestMapping(path = "/report")
@RequiredArgsConstructor
public class ReportController {
    
    private final AccountService accountService;
    private final ReportService reportService;

    @GetMapping("/accounts/statistics")
    public ResponseData<AccountStatisticsDTO> getAccountStatistics() {
        Pageable pageable = PageRequest.of(0, 1); // lấy 1 bản ghi duy nhất
        AccountStatisticsDTO statistics = accountService.getAccountStatistics();
        return ResponseData.success(statistics);
    }

    @GetMapping("/accounts/recent")
    public ResponseData<List<AccountReportDTO>> getRecentAccounts(
            @RequestParam(defaultValue = "10") int limit) {
        List<AccountReportDTO> accounts = accountService.getRecentAccounts(limit);
        return ResponseData.success(accounts);
    }

    @GetMapping("/accounts/all")
    public ResponseData<List<AccountReportDTO>> getAllAccountsForReport() {
        List<AccountReportDTO> accounts = accountService.getAllAccountsForReport();
        return ResponseData.success(accounts);
    }

    @GetMapping("/accounts/between")
    public ResponseData<List<AccountReportDTO>> getAccountsCreatedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseData.error(-1, "Ngày bắt đầu không thể lớn hơn ngày kết thúc");
        }
        
        List<AccountReportDTO> accounts = accountService.getAccountsCreatedBetween(startDate, endDate);
        return ResponseData.success(accounts);
    }

    @GetMapping("/accounts/summary")
    public ResponseEntity<?> getAccountSummary() {
        Pageable pageable = PageRequest.of(0, 1); // lấy 1 bản ghi duy nhất
        AccountStatisticsDTO stats = accountService.getAccountStatistics();
        
        String summary = String.format(
            "Tổng số tài khoản: %d\n" +
            "Tài khoản tạo hôm nay: %d\n" +
            "Tài khoản tạo tuần này: %d\n" +
            "Tài khoản tạo tháng này: %d\n" +
            "Tài khoản tạo năm nay: %d",
            stats.getTotalAccounts(),
            stats.getAccountsCreatedToday(),
            stats.getAccountsCreatedThisWeek(),
            stats.getAccountsCreatedThisMonth(),
            stats.getAccountsCreatedThisYear()
        );
        
        return ResponseEntity.ok(summary);
    }

    // Batch reporting endpoints (with details)
    @GetMapping("/accounts/batches/summary")
    public ResponseData<AccountBatchSummaryDTO> getAccountBatchSummary() {
        AccountBatchSummaryDTO summary = accountService.getAccountBatchSummary();
        return ResponseData.success(summary);
    }

    @GetMapping("/accounts/batches/all")
    public ResponseData<List<AccountBatchReportDTO>> getAllAccountBatches() {
        List<AccountBatchReportDTO> batches = accountService.getAllAccountBatches();
        return ResponseData.success(batches);
    }

    @GetMapping("/accounts/batches/codes")
    public ResponseData<List<String>> getAllBatchCodes() {
        List<String> batchCodes = accountService.getAllBatchCodes();
        return ResponseData.success(batchCodes);
    }

    @GetMapping("/accounts/batches/{batchCode}")
    public ResponseData<AccountBatchReportDTO> getAccountBatchByCode(
            @PathVariable String batchCode) {
        
        if (batchCode == null || batchCode.length() != 2) {
            return ResponseData.error(-1, "Mã khoá học phải có đúng 2 ký tự");
        }
        
        AccountBatchReportDTO batch = accountService.getAccountBatchByCode(batchCode.toUpperCase());
        return ResponseData.success(batch);
    }

    @GetMapping("/accounts/batches/{batchCode}/summary")
    public ResponseEntity<?> getBatchSummary(@PathVariable String batchCode) {
        if (batchCode == null || batchCode.length() != 2) {
            return ResponseEntity.badRequest().body("Mã khoá học phải có đúng 2 ký tự");
        }
        
        AccountBatchReportDTO batch = accountService.getAccountBatchByCode(batchCode.toUpperCase());
        
        String summary = String.format(
            "Khoá học: %s (%s)\n" +
            "Tổng số tài khoản: %d\n" +
            "Tài khoản Active: %d\n" +
            "Tài khoản Inactive: %d\n" +
            "Tài khoản Dormant: %d\n" +
            "Tài khoản Unknown: %d\n" +
            "Tài khoản cũ nhất: %s\n" +
            "Tài khoản mới nhất: %s",
            batch.getBatchCode(),
            batch.getBatchName(),
            batch.getTotalAccounts(),
            batch.getActiveAccounts(),
            batch.getInactiveAccounts(),
            batch.getDormantAccounts(),
            batch.getUnknownAccounts(),
            batch.getOldestAccountInBatch() != null ? batch.getOldestAccountInBatch().toString() : "N/A",
            batch.getNewestAccountInBatch() != null ? batch.getNewestAccountInBatch().toString() : "N/A"
        );
        
        return ResponseEntity.ok(summary);
    }

    // Batch stats endpoints (statistics only, no records)
    @GetMapping("/accounts/batches/stats/summary")
    public ResponseData<AccountBatchStatsSummaryDTO> getAccountBatchStatsSummary() {
        AccountBatchStatsSummaryDTO summary = accountService.getAccountBatchStatsSummary();
        return ResponseData.success(summary);
    }

    @GetMapping("/accounts/batches/stats/all")
    public ResponseData<List<AccountBatchStatsDTO>> getAllAccountBatchStats() {
        List<AccountBatchStatsDTO> batchStats = accountService.getAllAccountBatchStats();
        return ResponseData.success(batchStats);
    }

    @GetMapping("/accounts/batches/stats/{batchCode}")
    public ResponseData<AccountBatchStatsDTO> getAccountBatchStatsByCode(
            @PathVariable String batchCode) {
        
        if (batchCode == null || batchCode.length() != 2) {
            return ResponseData.error(-1, "Mã khoá học phải có đúng 2 ký tự");
        }
        
        AccountBatchStatsDTO batchStats = accountService.getAccountBatchStatsByCode(batchCode.toUpperCase());
        return ResponseData.success(batchStats);
    }

    @GetMapping("/accounts/batches/stats/{batchCode}/summary")
    public ResponseEntity<?> getBatchStatsSummary(@PathVariable String batchCode) {
        if (batchCode == null || batchCode.length() != 2) {
            return ResponseEntity.badRequest().body("Mã khoá học phải có đúng 2 ký tự");
        }
        
        AccountBatchStatsDTO batch = accountService.getAccountBatchStatsByCode(batchCode.toUpperCase());
        
        String summary = String.format(
            "Khoá học: %s (%s)\n" +
            "Tổng số tài khoản: %d\n" +
            "Tài khoản Active: %d\n" +
            "Tài khoản Inactive: %d\n" +
            "Tài khoản Dormant: %d\n" +
            "Tài khoản Unknown: %d\n" +
            "Tài khoản cũ nhất: %s\n" +
            "Tài khoản mới nhất: %s",
            batch.getBatchCode(),
            batch.getBatchName(),
            batch.getTotalAccounts(),
            batch.getActiveAccounts(),
            batch.getInactiveAccounts(),
            batch.getDormantAccounts(),
            batch.getUnknownAccounts(),
            batch.getOldestAccountInBatch() != null ? batch.getOldestAccountInBatch().toString() : "N/A",
            batch.getNewestAccountInBatch() != null ? batch.getNewestAccountInBatch().toString() : "N/A"
        );
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/dashboard")
    public ResponseData<ReportDashboardDTO> getDashboard() {
        return ResponseData.success(reportService.getDashboard());
    }
}
