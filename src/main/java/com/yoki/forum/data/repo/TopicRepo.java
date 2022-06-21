package com.yoki.forum.data.repo;

import com.yoki.forum.data.model.Topic;
import com.yoki.forum.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;


public interface TopicRepo extends JpaRepository<Topic, Long> {

    Optional<Topic> findByName(String name);

    boolean existsByIdAndSubscribersContains(Long id, User user);
}
