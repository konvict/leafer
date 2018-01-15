package org.ziwenxie.leafer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ziwenxie.leafer.db.TagMapper;
import org.ziwenxie.leafer.model.Tag;
import org.ziwenxie.leafer.util.IdWorker;

import java.util.Date;
import java.util.List;

@Service("tagService")
public class TagServiceImpl implements ITagService {

    private TagMapper tagMapper;

    private IdWorker idWorker;

    private Logger logger;

    public final String allTagsKey = "allTagsKey";

    public final String oneTagKey = "oneTagKey";

    @Autowired
    public TagServiceImpl(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
        this.idWorker = new IdWorker(1);
        this.logger = LoggerFactory.getLogger(TagServiceImpl.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "getAllTags", key = "#root.target.allTagsKey + #username")
    public Tag insertOneTag(String tagName, String username) {
        Tag isTag = getOneTagByName(tagName, username);

        // 标签已经存在
        if (isTag != null ) {
            return isTag;
        }

        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setUsername(username);
        tag.setId(idWorker.nextId());
        tag.setCreatedTime(new Date());
        tag.setModifiedTime(new Date());

        tagMapper.insertOneTag(tag);

        logger.info(username + " insert tag " + tag.getId() + " successfully");
        return tag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "getOneTagById", key = "#root.target.oneTagKey + #tagId"),
            @CacheEvict(value = "getAllTags", key = "#root.target.allTagsKey + #username")
    })
    public boolean deleteOneTagById(long tagId, String username) {
        tagMapper.deleteOneTagById(tagId);

        logger.info(username + " delete tag " + tagId + " successfully");
        return true;
    }

    @Override
    @Cacheable(value = "getOneTagById#300#30", key = "#root.target.oneTagKey + #tagId")
    public Tag getOneTagById(long tagId) {
        return tagMapper.getOneTagById(tagId);
    }

    @Override
    @Cacheable(value = "getOneTagByName#300#30", key = "#tagName + #username")
    public Tag getOneTagByName(String tagName, String username) {
        return tagMapper.getOneTagByName(tagName, username);
    }

    @Override
    @Cacheable(value = "getAllTags#300#30", key = "#root.target.allTagsKey + #username")
    public List<Tag> getAllTags(String username) {
        return tagMapper.getAllTags(username);
    }

}
