package org.ziwenxie.leafer.service;

import org.ziwenxie.leafer.model.ArticleTag;

public interface IArticleTagService {

    boolean insertOneArticleTag(ArticleTag articleTag, String username);

    boolean deleteOneArticleTag(ArticleTag articleTag, String username);
}
