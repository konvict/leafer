package org.ziwenxie.leafer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ziwenxie.leafer.db.ArticleMapper;
import org.ziwenxie.leafer.model.Article;
import org.ziwenxie.leafer.model.Tag;
import org.ziwenxie.leafer.util.IdWorker;

import java.util.Date;
import java.util.List;

@Service("articleService")
public class ArticleServiceImpl implements IArticleService {

    private ArticleMapper articleMapper;

    private IdWorker idWorker;

    private Logger logger;

    private CacheManager cacheManager;

    public final String oneArticleKey = "oneArticleKey";

    public final String articleCountKey = "articleCountKey";

    public final String articleTagKey = "articleTagKey";

    @Autowired
    public ArticleServiceImpl(ArticleMapper articleMapper, CacheManager cacheManager) {
        this.articleMapper = articleMapper;
        this.cacheManager = cacheManager;
        this.idWorker = new IdWorker(1);
        this.logger = LoggerFactory.getLogger(ArticleServiceImpl.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "getOneArticleById", key =  "#root.target.oneArticleKey + #article.id"),
            @CacheEvict(value = "getArticlesCount", key = "#root.target.articleCountKey + #username")
    })
    public long insertOneArticle(Article article) {
        long articleId = idWorker.nextId();
        article.setId(articleId);
        article.setCreatedTime(new Date());  // add the created time
        article.setModifiedTime(new Date());  // add the modified time

        articleMapper.insertOneArticle(article);

        logger.info(article.getUsername() + " insert article " + article.getId() + " successfully");

        deleteAllPagesCache(article.getUsername());

        return articleId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "getOneArticleById", key = "#root.target.oneArticleKey + #articleId"),
            @CacheEvict(value = "getArticlesCount", key = "#root.target.articleCountKey + #username")
    })
    public boolean deleteOneArticleById(long articleId, String username) {
        articleMapper.deleteOneArticleById(articleId);
        logger.info(username + " delete article " + articleId + " successfully");

        deleteAllPagesCache(username); // delete all pages' cache

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "getOneArticleById", key = "#root.target.oneArticleKey + #article.id"),
            @CacheEvict(value = "getArticlesOfOnePage", key = "#username + #page")
    })
    public boolean updateOneArticle(Article article, String username, int page) {
        // note: article object only contains id, title, body property
        article.setModifiedTime(new Date());

        articleMapper.updateOneArticle(article);
        logger.info(username + " update article " + article.getId() + " successfully");

        return true;
    }

    @Override
    @Cacheable(value = "getArticlesOfOnePage#300#30", key = "#username + #page")
    public List<Article> getArticlesOfOnePage(String username, int page) {
        return articleMapper.getArticlesOfOnePage(username, (page -1)*10);
    }

    @Override
    @Cacheable(value = "getOneArticleById#300#30", key = "#root.target.oneArticleKey + #id")
    public Article getOneArticleById(long id, String username) {
        return articleMapper.getOneArticleById(id);
    }

    @Override
    @Cacheable(value = "getAllTagsOfOneArticle#300#30", key = "#root.target.articleTagKey + #id")
    public List<Tag> getAllTagsOfOneArticle(long id) {
        return articleMapper.getAllTagsOfOneArticle(id);
    }

    @Override
    @Cacheable(value = "getArticlesCount#300#30", key = "#root.target.articleCountKey + #username")
    public long getArticlesCount(String username) {
        return articleMapper.getArticlesCount(username);
    }

    @Override
    public int getArticlePage(String username, long articleId) {
        long articleOrder = articleMapper.getArticleOrder(username, articleId) - 1;
        int page;
        if (articleOrder < 0) {  // first article
            page = 1;
        } else {
            page = (int) (articleOrder/10 + 1);
        }

        return page;
    }

    private void deleteAllPagesCache(String username) {
        long pages = articleMapper.getArticlesCount(username)/10 + 1;
        for (int page=1; page<=pages; page++) {
            cacheManager.getCache("getArticlesOfOnePage").evict(username + page);
        }
    }

}
