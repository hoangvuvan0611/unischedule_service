package com.example.unischeduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Thong tin ve tong luot truy cap (co thong tin chi tiet ve moi thang)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalVisitsDTO {
    long total;                                     // So luong tong luot truy cap
    List<VisitByMonthDTO> visitByMonth;             // Thong tin di kem la danh sach thong tin cac thang
}
