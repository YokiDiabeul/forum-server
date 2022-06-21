package com.yoki.forum.service;

import com.yoki.forum.data.model.Topic;
import com.yoki.forum.data.model.User;
import com.yoki.forum.dto.model.TTopic;
import com.yoki.forum.security.UserPrincipal;

import java.util.List;
import java.util.Set;

public interface TopicService {

    List<TTopic> getAll(UserPrincipal currentUser);

    Topic addTopic(Topic topic, UserPrincipal currentUser);

    Topic getTopic(Long id);

    TTopic getTopicByName(String name, UserPrincipal currentUser);

    Set<User> getTopicModerator(Long id);

    Set<User> getTopicSubscriber(Long id);

    void subsribe(Long id, UserPrincipal currentUser);

    void unsubsribe(Long id, UserPrincipal currentUser);

    TTopic toDTO(Topic topic, UserPrincipal currentUser);

}
