package com.yoki.forum.service;

import com.yoki.forum.data.model.Tag;

import java.util.List;

public interface TagService {

    List<Tag> getTags();

    Tag addTag(Tag tag);
}
