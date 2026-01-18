package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.LoginInfoDTO;
import com.example.unischeduleservice.dto.LoginRequest;

public interface NotiService {
    /**
     * Ham lay thong tin thong bao cua chuong trinh dai hoc
     */
    void sendMailNotiNewsVnua();

    /**
     * Ham lay thong tin thong bao cua quan tri vien den sinh vien
     */
    void sendMailNotiNewsFromAdminVnua();

    /**
     * Ham dang nhap lay thong tin token cua user
     * @return: thong tin token
     */
    String getTokenVnua(LoginRequest loginRequest);

    void sendMailNotiNewsFromAdminVnuaWithEachUser(LoginInfoDTO loginInfoDTO);
}
