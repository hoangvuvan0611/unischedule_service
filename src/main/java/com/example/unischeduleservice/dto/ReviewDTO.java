package com.example.unischeduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private String title;               // Tieu de cua phan hoi
    private String message;             // Noi dung cua phan hoi
    private String username;            // Thong tin ten nguoi dung
    private String userCode;            // Thong tin ma nguoi dung (sinh vien)
}
