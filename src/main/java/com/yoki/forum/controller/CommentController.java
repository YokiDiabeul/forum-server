package com.yoki.forum.controller;

import com.yoki.forum.dto.Response.ApiResponse;
import com.yoki.forum.dto.model.TComment;
import com.yoki.forum.security.CurrentUser;
import com.yoki.forum.security.RoleUser;
import com.yoki.forum.security.UserPrincipal;
import com.yoki.forum.service.CommentService;
import com.yoki.forum.util.LogUtils;
import com.yoki.forum.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    static private Logger LOGGER = LoggerFactory.getLogger(CommentController.class);
    static private String PREFIX = StringUtils.inBracket("CommentController");
    private StringBuilder sb = new StringBuilder(PREFIX);

    @Autowired
    private CommentService commentService;

    @PutMapping("/{id}/reply")
    @RoleUser
    public ResponseEntity<?> addReply(@Valid @RequestBody TComment comment, @PathVariable(value = "id") Long id) {
        LogUtils.info("addReply", LOGGER);

        commentService.addReply(comment, id);

        return ResponseEntity.ok(new ApiResponse(true, "Reply added successfully"));
    }

    @PutMapping("/{id}/upvote")
    @RoleUser
    public ResponseEntity<?> upvote(@PathVariable(value = "id") Long id,
                                    @CurrentUser UserPrincipal currentUser) {
        LogUtils.info("vote", LOGGER);
        commentService.vote(id, currentUser, true);
        return ResponseEntity.ok(new ApiResponse(true, "Vote successful"));
    }

    @PutMapping("/{id}/downvote")
    @RoleUser
    public ResponseEntity<?> downvote(@PathVariable(value = "id") Long id,
                                      @CurrentUser UserPrincipal currentUser) {
        LogUtils.info("vote", LOGGER);
        commentService.vote(id, currentUser, false);
        return ResponseEntity.ok(new ApiResponse(true, "Vote successful"));
    }

}