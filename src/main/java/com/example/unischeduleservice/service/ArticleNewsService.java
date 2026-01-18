package com.example.unischeduleservice.service;

import com.example.unischeduleservice.models.ArticleNews;

import java.util.List;

public interface ArticleNewsService {
    List<ArticleNews> getArticleNews();
    ArticleNews getArticleNewsByNotiIdAndDeviceToken(String notiId, String deviceToken);
    ArticleNews addNew(ArticleNews articleNews);
    void addNewList(List<ArticleNews> articleNewsList);
    void removeArticleNewsByNotiIdAndDeviceToken(String notiId, String deviceToken);
}
