package com.yoki.forum.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yoki.forum.data.model.Topic;
import com.yoki.forum.data.model.User;
import com.yoki.forum.data.repo.TopicRepo;
import com.yoki.forum.dto.model.TTopic;
import com.yoki.forum.exception.ConflictRequestException;
import com.yoki.forum.exception.ResourceNotFoundException;
import com.yoki.forum.security.UserPrincipal;
import com.yoki.forum.util.StringUtils;


@Service
public class TopicServiceI implements TopicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceI.class);
    private static final String PREFIX = StringUtils.inBracket("TopicService");

    private StringBuilder sb = new StringBuilder(PREFIX);


    @Autowired
    private TopicRepo topicRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mapper;

    @Override
    public List<TTopic> getAll(UserPrincipal currentUser) {
        return topicRepo.findAll().stream()
                .map(t -> toDTO(t, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public Topic getTopic(Long id) {

        return topicRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("topic", "id", id));
    }

    @Override
    public TTopic getTopicByName(String name, UserPrincipal currentUser) {
        return toDTO(topicRepo.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("topic", "name", name)), currentUser);
    }

    @Override
    public Topic addTopic(Topic topic, UserPrincipal currentUser) {

        if(topicRepo.findById(topic.getId()).isPresent()) {
            throw new ConflictRequestException("Topic already exists");
        }

        if(topicRepo.findByName(topic.getName()).isPresent()) {
            throw new ConflictRequestException("Topic with this name already exists");
        }

        topic.getSubscribers().add(userService.getUser(currentUser.getUsername()));

        return topicRepo.save(topic);
    }

    @Override
    public Set<User> getTopicModerator(Long id) {
        return getTopic(id).getModerators();
    }

    @Override
    public Set<User> getTopicSubscriber(Long id) {
        return getTopic(id).getSubscribers();
    }

    @Override
    public void subsribe(Long id, UserPrincipal currentUser) {

        User user = userService.getUser(currentUser.getUsername());

        Topic topic = getTopic(id);

        if(topic.getSubscribers().contains(user)) {
            throw new ConflictRequestException("Already subs");
        }

        topic.getSubscribers().add(user);
        topicRepo.save(topic);
    }

    @Override
    public void unsubsribe(Long id, UserPrincipal currentUser) {

        User user = userService.getUser(currentUser.getUsername());

        Topic topic = getTopic(id);

        if(!topic.getSubscribers().contains(user)) {
            throw new ConflictRequestException("Not subs");
        }

        topic.getSubscribers().remove(user);
        topicRepo.save(topic);
    }

    @Override
    public TTopic toDTO(Topic topic, UserPrincipal currentUser) {
        TTopic t = mapper.map(topic, TTopic.class);
        t.setNbSubs(topic.getSubscribers().size());

        t.setSub(false);
        if(currentUser != null) {
            User user = userService.getUser(currentUser.getUsername());
            t.setSub(topicRepo.existsByIdAndSubscribersContains(topic.getId(), user));
        }

        return t;
    }
}
