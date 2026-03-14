package com.example.unischeduleservice.dto;

import lombok.*;

import java.util.List;

/**
 * @author vuvanhoang
 * @created 14/03/26 10:20
 * @project unischedule_service
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBatchSummaryDTO {
    private long totalAccounts;
    private long totalBatches;
    private String largestBatch;
    private String smallestBatch;
    private List<AccountBatchReportDTO> batchReports;
}
