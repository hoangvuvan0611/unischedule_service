package com.example.unischeduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Doi tuong tra ve thong tin account (hoat dong cua tai khoan)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullInfoReportOverviewDTO {
    private TotalVisitsDTO totalVisits;                             // Thong tin ve tong luot truy cap
    private VisitsDayInfoDTO visitsDayInfoDTO;                      // Thong tin luot truy cap trong ngay hom nay
}
