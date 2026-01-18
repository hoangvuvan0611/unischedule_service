package com.example.unischeduleservice.service;

import com.example.unischeduleservice.models.ArticleNews;
import com.example.unischeduleservice.repository.ArticleNewsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleNewsServiceImpl implements ArticleNewsService {
    private static final Logger logger = LoggerFactory.getLogger(ArticleNewsServiceImpl.class);
    private final ArticleNewsRepository articleNewsRepository;


    @Override
    public List<ArticleNews> getArticleNews() {
        return articleNewsRepository.findAll();
    }

    @Override
    public ArticleNews getArticleNewsByNotiIdAndDeviceToken(String notiId, String deviceToken) {
        return articleNewsRepository.getArticleNewsByNotiIdAndDeviceToken(notiId, deviceToken);
    }

    @Override
    public ArticleNews addNew(ArticleNews articleNews) {
        return articleNewsRepository.save(articleNews);
    }

    @Override
    public void addNewList(List<ArticleNews> articleNewsList) {
        articleNewsRepository.saveAll(articleNewsList);
    }

    @Override
    public void removeArticleNewsByNotiIdAndDeviceToken(String notiId, String deviceToken) {
        articleNewsRepository.removeArticleNewsByNotiIdAndDeviceToken(notiId, deviceToken);
    }
}
