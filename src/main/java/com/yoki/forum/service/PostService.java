package com.yoki.forum.service;

import com.yoki.forum.data.model.Post;
import com.yoki.forum.dto.model.TPost;
import com.yoki.forum.security.UserPrincipal;

import java.util.List;

public interface PostService {

    List<TPost> getAll(UserPrincipal currentUser);

    List<TPost> getAllByTopic(Long id, UserPrincipal currentUser);

    List<TPost> getAllCreatedBy(String username, UserPrincipal currentUser);

    TPost getPost(String slug, UserPrincipal currentUser);

    Post getOPost(String slug);

    Post addPost(TPost request);

    void vote(String slug, UserPrincipal currentUser, boolean up);

    void updatePost(TPost post, String slug, UserPrincipal current);
}

