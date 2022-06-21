package com.yoki.forum.service;

import com.yoki.forum.data.model.Tag;
import com.yoki.forum.data.repo.TagRepo;
import com.yoki.forum.exception.ConflictRequestException;
import com.yoki.forum.util.LogUtils;
import com.yoki.forum.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceI implements TagService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceI.class);
    private static final String PREFIX = StringUtils.inBracket("TagService");

    private StringBuilder sb = new StringBuilder(PREFIX);

    @Autowired
    private TagRepo tagRepo;

    @Override
    public List<Tag> getTags() {
        return tagRepo.findAll().stream().map(this::toTag).collect(Collectors.toList());
    }

    @Override
    public Tag addTag(Tag tag) {
        if(tagRepo.existsById(tag.getId())) {
            throw new ConflictRequestException("This tag already exists");
        }
        return tagRepo.save(tag);
    }

    private Tag toTag(Tag tag) {
        return tag;
    }
}
