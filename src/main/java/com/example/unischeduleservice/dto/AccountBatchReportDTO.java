package com.example.unischeduleservice.dto;

import lombok.*;

import java.time.LocalDateTime;
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
public class AccountBatchReportDTO {
    private String batchCode; // Khoá học (VD: 20, 21, 22, ...)
    private String batchName; // Tên khoá học (VD: "Khoá 2020", "Khoá 2021")
    private long totalAccounts;
    private long activeAccounts;
    private long inactiveAccounts;
    private long dormantAccounts;
    private long unknownAccounts;
    private LocalDateTime oldestAccountInBatch;
    private LocalDateTime newestAccountInBatch;
    private List<AccountReportDTO> accounts;
}
