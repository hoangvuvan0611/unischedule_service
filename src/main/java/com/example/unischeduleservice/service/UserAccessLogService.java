package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.UserAccessLogInfoDTO;
import com.example.unischeduleservice.dto.base.PageResponseDTO;
import com.example.unischeduleservice.models.UserAccessLogs;

import java.time.LocalDateTime;

public interface UserAccessLogService {
    UserAccessLogs findById(String id);
    UserAccessLogs findByUsername(String username);
    PageResponseDTO<UserAccessLogInfoDTO> queryUserAccessLogs(
            String username,
            String osOfDevice,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );
}
