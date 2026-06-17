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
@Schema(description = "Thông tin account phục vụ truy vấn cho frontend")
public class AccountInfoDTO {
    @Schema(description = "ID của account", example = "686ea1d0f8f3a7a1a0b1c234")
    private String id;

    @Schema(description = "Tên đăng nhập", example = "6656485")
    private String username;

    @Schema(description = "Thời điểm tạo account")
    private LocalDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật gần nhất")
    private LocalDateTime updatedAt;
}
