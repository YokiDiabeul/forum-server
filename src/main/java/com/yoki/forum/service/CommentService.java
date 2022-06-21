package com.yoki.forum.service;

import com.yoki.forum.data.model.Comment;
import com.yoki.forum.data.model.Response;
import com.yoki.forum.dto.model.TComment;
import com.yoki.forum.security.UserPrincipal;

import java.util.List;

public interface CommentService {

    Comment addComment(TComment comment, String slug);

    List<TComment> getTComments(String slug, UserPrincipal currentUser);

    Response addReply(TComment response, Long id);

    void vote(Long id, UserPrincipal currentUser, boolean up);

    List<TComment> getCommentsCreatedBy(String username, UserPrincipal currentUser);
}
