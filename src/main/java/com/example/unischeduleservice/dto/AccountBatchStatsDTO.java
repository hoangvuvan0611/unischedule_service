package com.example.unischeduleservice.dto;

import lombok.*;

import java.time.LocalDateTime;

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
public class AccountBatchStatsDTO {
    private String batchCode;
    private String batchName;
    private long totalAccounts;
    private long activeAccounts;
    private long inactiveAccounts;
    private long dormantAccounts;
    private long unknownAccounts;
    private LocalDateTime oldestAccountInBatch;
    private LocalDateTime newestAccountInBatch;
}
