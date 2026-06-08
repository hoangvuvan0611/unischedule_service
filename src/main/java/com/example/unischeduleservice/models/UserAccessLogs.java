package com.example.unischeduleservice.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Class luu thong tin lich su dang nhap cua user
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("user_access_logs")
public class UserAccessLogs {
    @Id
    private String id;
    private String username;
    private String osOfDevice;
    private LocalDateTime createdAt;
}
