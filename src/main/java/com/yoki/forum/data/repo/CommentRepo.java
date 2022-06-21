package com.yoki.forum.data.repo;


import com.yoki.forum.data.model.Comment;
import com.yoki.forum.data.model.Post;
import com.yoki.forum.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Long> {

    List<Comment> findAllByCreatedBy(Long user);

    List<Comment> findAllByPost(Post p);
}
