package org.ziwenxie.leafer.service;

import org.ziwenxie.leafer.model.Tag;

import java.util.List;

public interface ITagService {

    Tag insertOneTag(String tagName, String username);

    boolean deleteOneTagById(long tagId, String username);

    Tag getOneTagById(long tagId);

    Tag getOneTagByName(String tagName, String username);

    List<Tag> getAllTags(String username);
}
