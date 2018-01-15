package org.ziwenxie.leafer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ziwenxie.leafer.db.ArticleTagMapper;
import org.ziwenxie.leafer.model.ArticleTag;

@Service("articleTagService")
public class ArticleTagServiceImpl implements IArticleTagService {

    private ArticleTagMapper articleTagMapper;

    private Logger logger;

    public final String oneArticleKey = "oneArticleKey";

    public final String allTagsKey = "allTagsKey";

    public final String oneTagKey = "oneTagKey";

    @Autowired
    public ArticleTagServiceImpl(ArticleTagMapper articleTagMapper) {
        this.articleTagMapper = articleTagMapper;
        this.logger = LoggerFactory.getLogger(ArticleServiceImpl.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "getOneArticleById", key = "#root.target.oneArticleKey + #articleTag.articleId"),
            @CacheEvict(value = "getOneTagById", key = "#root.target.oneTagKey + #tagId"),
            @CacheEvict(value = "getAllTags", key = "#root.target.allTagsKey + #username")
    })
    public boolean insertOneArticleTag(ArticleTag articleTag, String username) {
        articleTagMapper.insertOneArticleTag(articleTag);
        logger.info(username + " insert articleTag " + articleTag.toString() + " successfully");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "getOneArticleById", key = "#root.target.oneArticleKey + #articleTag.articleId"),
            @CacheEvict(value = "getOneTagById", key = "#root.target.oneTagKey + #tagId"),
            @CacheEvict(value = "getAllTags", key = "#root.target.allTagsKey + #username")
    })
    public boolean deleteOneArticleTag(ArticleTag articleTag, String username) {
        articleTagMapper.deleteOneArticleTag(articleTag);
        logger.info(username + " delete articleTag " + articleTag.toString() + " successfully");
        return true;
    }

}
