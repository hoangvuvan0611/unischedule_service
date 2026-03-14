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
public class AccountReportDTO {
    private String id;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long daysSinceCreated;
    private String accountStatus;
}
