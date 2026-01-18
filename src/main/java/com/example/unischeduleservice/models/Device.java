package com.example.unischeduleservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("devices")
public class Device {
    @Id
    private String id;
    private String username;
    private String password;
    private String os;
    private String deviceName;
    private LocalDateTime createdAt;
}
