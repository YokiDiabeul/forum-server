package com.yoki.forum.controller;

import com.yoki.forum.data.model.Tag;
import com.yoki.forum.dto.Response.ApiResponse;
import com.yoki.forum.security.RoleAdmin;
import com.yoki.forum.security.RoleUser;
import com.yoki.forum.service.TagService;
import com.yoki.forum.util.LogUtils;
import com.yoki.forum.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private static Logger LOGGER = LoggerFactory.getLogger(TagController.class);
    private static String PREFIX = StringUtils.inBracket("TagController");
    private StringBuilder sb = new StringBuilder(PREFIX);

    @Autowired
    private TagService tagService;

    @GetMapping
    public List<Tag> getAllTag() {
        LogUtils.info( "getAllTag", LOGGER);
        return tagService.getTags();
    }

    @PostMapping
    @RoleUser
    public ResponseEntity<?> addTag(@Valid @RequestBody Tag tag) {
        LogUtils.start(sb, "addTag", LOGGER);

        Tag result = tagService.addTag(tag);

        LOGGER.info(sb + "tag saved " + result);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/tags/{name}")
                .buildAndExpand(result.getName()).toUri();

        sb = LogUtils.reset(PREFIX);
        return ResponseEntity.created(location).body(new ApiResponse(true, "Tag registered successfully"));
    }

}
