package com.example.unischeduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Aggregated report payload for dashboard screens.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDashboardDTO {
    private LocalDateTime generatedAt;
    private AccountStatisticsDTO accountStatistics;
    private AccountBatchSummaryDTO accountBatchSummary;
    private AccountBatchStatsSummaryDTO accountBatchStatsSummary;
    private long totalReviews;
    private long totalDevices;
    private long totalAccessLogs;
    private List<AccountReportDTO> recentAccounts;
    private List<AccountReportDTO> allAccounts;
    private List<AccountBatchReportDTO> accountBatches;
    private List<AccountBatchStatsDTO> accountBatchStats;
    private List<CategoryCountDTO> accountStatusChart;
    private List<CategoryCountDTO> accountCreationChart;
    private List<CategoryCountDTO> accessLogOsChart;
    private List<CategoryCountDTO> deviceOsChart;
    private List<CategoryCountDTO> reviewTimelineChart;
}
