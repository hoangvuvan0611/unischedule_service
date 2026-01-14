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
public class ArticleNewsFromAdminVnua {
    private String id;
    private String doi_tuong_search;
    private String doi_tuong;
    private String phan_cap_search;
    private String phan_cap_sinh_vien;
    private String tieu_de;
    private String noi_dung;
    private Boolean is_phai_xem;
    private LocalDateTime ngay_gui;
    private String nguoi_gui;
    private Boolean is_da_doc;
    private String phan_hoi;
    private Boolean is_xem_phan_hoi;
    private String ngay_xem;
}
