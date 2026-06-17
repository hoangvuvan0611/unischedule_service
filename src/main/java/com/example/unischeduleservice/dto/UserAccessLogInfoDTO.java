package com.example.unischeduleservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin lịch sử truy cập của user")
public class UserAccessLogInfoDTO {
    @Schema(description = "ID của bản ghi log", example = "686ea1d0f8f3a7a1a0b1c235")
    private String id;

    @Schema(description = "Tên đăng nhập", example = "6656485")
    private String username;

    @Schema(description = "Thông tin hệ điều hành hoặc thiết bị", example = "Android 14")
    private String osOfDevice;

    @Schema(description = "Thời điểm truy cập")
    private LocalDateTime createdAt;
}
