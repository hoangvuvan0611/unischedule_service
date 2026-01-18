package com.example.unischeduleservice.repository;

import com.example.unischeduleservice.models.ArticleNews;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleNewsRepository extends MongoRepository<ArticleNews, String> {
    ArticleNews getArticleNewsByNotiIdAndDeviceToken(String notiId, String deviceToken);
    ArticleNews removeArticleNewsByNotiIdAndDeviceToken(String notiId, String deviceToken);
}
