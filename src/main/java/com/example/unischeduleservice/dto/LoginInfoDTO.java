package com.example.unischeduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfoDTO {
    private String username;
    private String password;
    private String deviceToken;
    private String osOfDevice;
    private String deviceName;
}
