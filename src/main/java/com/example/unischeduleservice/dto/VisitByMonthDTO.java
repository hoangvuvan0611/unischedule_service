package com.example.unischeduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitByMonthDTO {
    Integer  month;
    Integer year;
    Long totalVisit;
}
