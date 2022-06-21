package com.yoki.forum.data.repo;

import com.yoki.forum.data.model.Post;
import com.yoki.forum.data.model.Topic;
import com.yoki.forum.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepo extends JpaRepository<Post, Long> {


    List<Post> findAllByCreatedBy(Long user);

    List<Post> findAllByTopic(Topic topic);

    Optional<Post> findBySlug(String slug);
}
