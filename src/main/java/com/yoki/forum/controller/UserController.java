package com.yoki.forum.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yoki.forum.data.model.Trophy;
import com.yoki.forum.data.model.User;
import com.yoki.forum.dto.Response.ApiResponse;
import com.yoki.forum.dto.model.TComment;
import com.yoki.forum.dto.model.TPost;
import com.yoki.forum.security.CurrentUser;
import com.yoki.forum.security.RoleAdmin;
import com.yoki.forum.security.RoleUser;
import com.yoki.forum.security.UserPrincipal;
import com.yoki.forum.service.CommentService;
import com.yoki.forum.service.PostService;
import com.yoki.forum.service.UserService;
import com.yoki.forum.util.LogUtils;
import com.yoki.forum.util.StringUtils;

@RestController
@RequestMapping("/api/users")
public class UserController {

    static private Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    static private String PREFIX = StringUtils.inBracket("UserController");
    private StringBuilder sb = new StringBuilder(PREFIX);

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @GetMapping
    @RoleAdmin
    public List<User> getUsers() {
        LogUtils.info( "getUsers", LOGGER);
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    @RoleUser
    public User getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        LogUtils.info( "getCurrentUser", LOGGER);
        return userService.getUser(currentUser.getUsername());
    }

    @GetMapping("/{username}")
    public User getUser(@PathVariable(value = "username") String username) {
        LogUtils.info("getUser", LOGGER);
        return userService.getUser(username);
    }

    @PutMapping
    @RoleUser
    public ResponseEntity<?> updateUser(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody User request){
        LogUtils.info( "updateUser", LOGGER);
        userService.updateUser(currentUser, request);
        return ResponseEntity.ok(new ApiResponse(true, "User updated successfully"));
    }

    @GetMapping("/me/posts")
    @RoleUser
    public List<TPost> getMyPosts(@CurrentUser UserPrincipal currentUser) {
        LogUtils.info("getMyPosts", LOGGER);
        return postService.getAllCreatedBy(currentUser.getUsername(), currentUser);
    }

    @GetMapping("/{username}/posts")
    public List<TPost> getPostsCreatedBy(@PathVariable(value = "username") String username,
                                         @CurrentUser UserPrincipal currentUser) {
        LogUtils.info("getPostsCreatedBy", LOGGER);
        return postService.getAllCreatedBy(username, currentUser);
    }

    @GetMapping("/{username}/comments")
    public List<TComment> getCommentsCreatedBy(@PathVariable(value = "username") String username,
                                               @CurrentUser UserPrincipal currentUser) {
        LogUtils.info("getPostsCreatedBy", LOGGER);
        return commentService.getCommentsCreatedBy(username, currentUser);
    }

    @PostMapping("/{username}/trophies")
    @RoleAdmin
    public ResponseEntity<?> addTrophy(@PathVariable(value = "username") String username,
                                       @Valid @RequestBody Trophy trophy) {
        LogUtils.info("addTrophy", LOGGER);
        userService.addTrophy(username, trophy);
        return ResponseEntity.ok(new ApiResponse(true, "Trophy added successfully"));
    }


    @GetMapping("/{username}/trophies")
    @RoleUser
    public List<Trophy> getTrophies(@PathVariable(value = "username") String username,
                                    @CurrentUser UserPrincipal currentUser) {
        LogUtils.info("getPostsCreatedBy", LOGGER);
        return userService.getUserTrophy(username, currentUser);
    }
}
