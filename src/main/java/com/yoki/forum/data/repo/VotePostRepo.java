package com.yoki.forum.data.repo;

import com.yoki.forum.data.model.Post;
import com.yoki.forum.data.model.User;
import com.yoki.forum.data.model.VotePost;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface VotePostRepo extends JpaRepository<VotePost, Long> {

    Optional<VotePost> findByPostAndUser(Post post, User user);

    int countAllByPostAndUp(Post post, boolean up);

    boolean existsByPostAndUser(Post post, User user);
}
