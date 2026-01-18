package com.example.unischeduleservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("article_news")
public class ArticleNews {
    @Id
    private String id;
    @Indexed(unique = true)
    private String notiId;
    private String deviceToken;
    private String subject;
    private String content;
    private Boolean isSent;
    private LocalDateTime createdAt;
}
