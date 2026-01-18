package com.example.unischeduleservice.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("reviews")
public class Review {
    @Id
    private String id;
    private String username;
    private String userCode;
    private String title;
    private String message;
    private LocalDateTime createdAt;
}
