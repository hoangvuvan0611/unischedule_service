package com.example.unischeduleservice.dto;

import lombok.*;

import java.util.List;

/**
 * @author vuvanhoang
 * @created 14/03/26 10:36
 * @project unischedule_service
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBatchStatsSummaryDTO {
    private long totalAccounts;
    private long totalBatches;
    private String largestBatch;
    private String smallestBatch;
    private long totalActiveAccounts;
    private long totalInactiveAccounts;
    private long totalDormantAccounts;
    private long totalUnknownAccounts;
    private List<AccountBatchStatsDTO> batchStats;
}
