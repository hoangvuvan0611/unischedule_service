package com.example.unischeduleservice.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * @author vuvanhoang
 * @created 14/03/26 09:29
 * @project unischedule_service
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatisticsDTO {
    private long totalAccounts;
    private long accountsCreatedToday;
    private long accountsCreatedThisWeek;
    private long accountsCreatedThisMonth;
    private long accountsCreatedThisYear;
    private LocalDateTime lastAccountCreated;
    private LocalDateTime oldestAccountCreated;
}
