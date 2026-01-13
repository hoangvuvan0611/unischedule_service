package com.example.unischeduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleNewsVnua {
    private String id;
    private String ky_hieu;
    private Long so_luong;
    private Boolean is_hien_thi;
    private String tieu_de;
    private String tom_tat;
    private LocalDateTime ngay_dang_tin;
    private String ngay_hieu_chinh;
    private Long do_uu_tien;
    private Boolean is_news;
    private Boolean kieu_hien_thi_ngang;
}
