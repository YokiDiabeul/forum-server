package com.yoki.forum.controller;

import com.yoki.forum.data.model.Post;
import com.yoki.forum.dto.Response.ApiResponse;
import com.yoki.forum.dto.model.TComment;
import com.yoki.forum.dto.model.TPost;
import com.yoki.forum.security.CurrentUser;
import com.yoki.forum.security.RoleAdmin;
import com.yoki.forum.security.RoleUser;
import com.yoki.forum.security.UserPrincipal;
import com.yoki.forum.service.CommentService;
import com.yoki.forum.service.PostService;
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
@RequestMapping("/api/posts")
public class PostController {

    static private Logger LOGGER = LoggerFactory.getLogger(PostController.class);
    static private String PREFIX = StringUtils.inBracket("PostController");
    private StringBuilder sb = new StringBuilder(PREFIX);

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @GetMapping
    public List<TPost> getPosts(@CurrentUser UserPrincipal currentUser) {
        LogUtils.info("getPosts", LOGGER);
        return postService.getAll(currentUser);
    }

    @GetMapping("/{slug}")
    public TPost getPostBySlug(@PathVariable(value = "slug") String slug, @CurrentUser UserPrincipal currentUser) {
        LogUtils.info("getPostBySlug", LOGGER);
        return postService.getPost(slug, currentUser);
    }

    @PostMapping
    @RoleUser
    public ResponseEntity<?> addPost(@Valid @RequestBody TPost post) {
        LogUtils.info("addPost", LOGGER);

        Post result = postService.addPost(post);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/posts/{slug}")
                .buildAndExpand(result.getSlug()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Post posted successfully"));
    }

    @PutMapping("/{slug}")
    @RoleAdmin
    public ResponseEntity<?> updatePost(@PathVariable(value = "slug") String slug, @Valid @RequestBody TPost post,  @CurrentUser UserPrincipal current) {
        LogUtils.info("updatePost", LOGGER);

        postService.updatePost(post, slug, current);

        return ResponseEntity.ok(new ApiResponse(true, "post updated successfully"));
    }

    @PutMapping("/{slug}/comment")
    @RoleUser
    public ResponseEntity<?> addComment(@Valid @RequestBody TComment comment, @PathVariable(value = "slug") String slug) {
        LogUtils.info("addPost", LOGGER);

        commentService.addComment(comment, slug);

        return ResponseEntity.ok(new ApiResponse(true, "Comment added successfully"));
    }

    @PutMapping("/{slug}/upvote")
    @RoleUser
    public ResponseEntity<?> upvote(@PathVariable(value = "slug") String slug, @CurrentUser UserPrincipal currentUser) {
        LogUtils.info("vote", LOGGER);
        postService.vote(slug, currentUser, true);
        return ResponseEntity.ok(new ApiResponse(true, "Vote successful"));
    }

    @PutMapping("/{slug}/downvote")
    @RoleUser
    public ResponseEntity<?> downvote(@PathVariable(value = "slug") String slug, @CurrentUser UserPrincipal currentUser) {
        LogUtils.info("vote", LOGGER);
        postService.vote(slug, currentUser, false);
        return ResponseEntity.ok(new ApiResponse(true, "Vote successful"));
    }

}
