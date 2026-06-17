package com.example.unischeduleservice.controller;

import com.example.unischeduleservice.dto.UserAccessLogInfoDTO;
import com.example.unischeduleservice.dto.base.PageResponseDTO;
import com.example.unischeduleservice.dto.base.ResponseData;
import com.example.unischeduleservice.models.UserAccessLogs;
import com.example.unischeduleservice.service.UserAccessLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/user-access-logs")
@RequiredArgsConstructor
@Tag(name = "User Access Log", description = "API truy vấn lịch sử truy cập của user")
public class UserAccessLogController {

    private final UserAccessLogService userAccessLogService;

    @GetMapping("/{id}")
    @Operation(summary = "Lấy log theo ID")
    public ResponseData<UserAccessLogs> getById(
            @Parameter(description = "ID của log") @PathVariable String id) {
        UserAccessLogs log = userAccessLogService.findById(id);
        return log == null ? ResponseData.error(404, "Không tìm thấy log") : ResponseData.success(log);
    }

    @GetMapping("/by-username/{username}")
    @Operation(summary = "Lấy log đầu tiên theo username")
    public ResponseData<UserAccessLogs> getByUsername(
            @Parameter(description = "Tên đăng nhập") @PathVariable String username) {
        UserAccessLogs log = userAccessLogService.findByUsername(username);
        return log == null ? ResponseData.error(404, "Không tìm thấy log") : ResponseData.success(log);
    }

    @GetMapping
    @Operation(summary = "Truy vấn danh sách log có phân trang và lọc")
    public ResponseData<PageResponseDTO<UserAccessLogInfoDTO>> query(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String osOfDevice,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseData.success(userAccessLogService.queryUserAccessLogs(
                username,
                osOfDevice,
                createdFrom,
                createdTo,
                page,
                size,
                sortBy,
                sortDirection
        ));
    }

    @GetMapping("/recent")
    @Operation(summary = "Lấy các log gần nhất")
    public ResponseData<java.util.List<UserAccessLogInfoDTO>> getRecent(
            @RequestParam(defaultValue = "10") int limit) {
        PageResponseDTO<UserAccessLogInfoDTO> page = userAccessLogService.queryUserAccessLogs(
                null,
                null,
                null,
                null,
                0,
                limit,
                "createdAt",
                "desc"
        );
        return ResponseData.success(page.getItems());
    }
}
