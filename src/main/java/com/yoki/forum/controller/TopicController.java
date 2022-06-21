package com.yoki.forum.controller;

import com.yoki.forum.data.model.Topic;
import com.yoki.forum.data.model.User;
import com.yoki.forum.dto.Response.ApiResponse;
import com.yoki.forum.dto.model.TPost;
import com.yoki.forum.dto.model.TTopic;
import com.yoki.forum.security.CurrentUser;
import com.yoki.forum.security.RoleUser;
import com.yoki.forum.security.UserPrincipal;
import com.yoki.forum.service.PostService;
import com.yoki.forum.service.TopicService;
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
import java.util.Set;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    static private Logger logger = LoggerFactory.getLogger(PostController.class);
    static private String PREFIX = StringUtils.inBracket("TopicController");
    private StringBuilder sb = new StringBuilder(PREFIX);


    @Autowired
    private TopicService topicService;

    @Autowired
    private PostService postService;

    @GetMapping
    public List<TTopic> getAll(@CurrentUser UserPrincipal currentUser) {
        LogUtils.info("getAll", logger);
        return topicService.getAll(currentUser);
    }

    @GetMapping("/{id}")
    public Topic getTopic(@PathVariable(name = "id") Long id) {
        LogUtils.info( "getTopic", logger);
        return topicService.getTopic(id);
    }

    @GetMapping("/{name}/n")
    public TTopic getTopicByName(@PathVariable(name = "name") String name,
                                 @CurrentUser UserPrincipal currentUser) {
        LogUtils.info( "getTopicByName", logger);
        return topicService.getTopicByName(name, currentUser);
    }

    @GetMapping("/{id}/posts")
    public List<TPost> getTopicPosts(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser) {
        LogUtils.info("getTopicPosts", logger);
        return postService.getAllByTopic(id, currentUser);
    }

    @GetMapping("/{id}/moderators")
    public Set<User> getTopicModerators(@PathVariable(name = "id") Long id) {
        LogUtils.info("getTopicModerators", logger);
        return topicService.getTopicModerator(id);
    }

    @GetMapping("/{id}/subscribers")
    public Set<User> getTopicSubscribers(@PathVariable(name = "id") Long id) {
        LogUtils.info("getTopicSubscribers", logger);
        return topicService.getTopicSubscriber(id);
    }

    @PutMapping("/{id}/sub")
    @RoleUser
    public ResponseEntity<?> subscribe(@PathVariable(name = "id") Long id,
                                       @CurrentUser UserPrincipal currentUser) {
        LogUtils.info("subscribe", logger);

        topicService.subsribe(id, currentUser);

        return ResponseEntity.ok(new ApiResponse(true, "Subscribtion successfully"));
    }

    @PutMapping("/{id}/unsub")
    @RoleUser
    public ResponseEntity<?> unsubscribe(@PathVariable(name = "id") Long id,
                                       @CurrentUser UserPrincipal currentUser) {
        LogUtils.info("subscribe", logger);

        topicService.unsubsribe(id, currentUser);

        return ResponseEntity.ok(new ApiResponse(true, "Unsubscribtion successfully"));
    }

    @PostMapping
    @RoleUser
    public ResponseEntity<?> addTopic(@Valid @RequestBody Topic topic,
                                      @CurrentUser UserPrincipal currentUser) {
        LogUtils.info( "addTopic", logger);

        Topic result = topicService.addTopic(topic, currentUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/topics/{id}")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Topic registered successfully"));
    }

}
