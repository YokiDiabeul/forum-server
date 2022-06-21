package com.yoki.forum.data.repo;

import com.yoki.forum.data.model.Comment;
import com.yoki.forum.data.model.User;
import com.yoki.forum.data.model.VoteComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteCommentRepo extends JpaRepository<VoteComment, Long> {

    int countAllByCommentAndUp(Comment comment, boolean up);

    Optional<VoteComment> findByCommentAndUser(Comment comment, User user);
}
