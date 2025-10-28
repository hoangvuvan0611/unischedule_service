package com.example.unischeduleservice.dto;

import lombok.*;

/**
 * @author vuvanhoang
 * @created 28/10/25 21:56
 * @project unischedule_service
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String id;
    private String token;
    private String username;
    private String fullname;
    private String email;
    private String roles;
    private String idpc;
}
