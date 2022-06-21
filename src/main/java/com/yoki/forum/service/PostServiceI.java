package com.yoki.forum.service;

import com.yoki.forum.data.model.Post;
import com.yoki.forum.data.model.User;
import com.yoki.forum.data.model.VotePost;
import com.yoki.forum.data.repo.PostRepo;
import com.yoki.forum.data.repo.VotePostRepo;
import com.yoki.forum.dto.model.TPost;
import com.yoki.forum.exception.AppException;
import com.yoki.forum.exception.ResourceNotFoundException;
import com.yoki.forum.security.UserPrincipal;
import com.yoki.forum.util.StringUtils;
import com.yoki.forum.util.TextUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceI implements PostService {

    static private Logger LOGGER = LoggerFactory.getLogger(PostServiceI.class);
    static private String PREFIX = StringUtils.inBracket("PostService");
    public static final String DATE_FORMAT = "MMMM dd, yyyy";

    private StringBuilder sb = new StringBuilder(PREFIX);

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private VotePostRepo votePostRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private ModelMapper mapper;

    @Override
    public List<TPost> getAll(UserPrincipal currentUser) {
        return postRepo.findAll().stream()
                .sorted(Comparator.comparing(Post::getCreatedAt, Comparator.reverseOrder()))
                .map(p -> toDTO(p, currentUser)).collect(Collectors.toList());
    }

    @Override
    public Post getOPost(String slug) {
        return get(slug);
    }

    @Override
    public TPost getPost(String slug, UserPrincipal currentUser) {
        return toDTO(get(slug), currentUser);
    }

    @Override
    public List<TPost> getAllByTopic(Long id, UserPrincipal currentUser) {

        return postRepo.findAllByTopic(topicService.getTopic(id)).stream()
                .sorted(Comparator.comparing(Post::getCreatedAt, Comparator.reverseOrder()))
                .map(p -> toDTO(p, currentUser)).collect(Collectors.toList());
    }

    @Override
    public List<TPost> getAllCreatedBy(String username, UserPrincipal currentUser) {

        return postRepo.findAllByCreatedBy(userService.getUser(username).getId()).stream()
                .sorted(Comparator.comparing(Post::getCreatedAt, Comparator.reverseOrder()))
                .map(p -> toDTO(p, currentUser)).collect(Collectors.toList());
    }

    @Override
    public Post addPost(TPost request) {

        Post p = mapper.map(request, Post.class);
        p.setSlug(TextUtils.toSlug(p.getTitle()));
        p.setBody(request.getBody().getBytes());
        p.setSummary(TextUtils.toSummary(request.getBody()));

        p.setTopic(topicService.getTopic(request.getTopic().getId()));
        p.setTags(request.getTags());

        return postRepo.save(p);
    }

    @Override
    public void updatePost(TPost request, String slug, UserPrincipal current) {
    	
        Post post = postRepo.findBySlug(request.getSlug())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", request.getId()));
        
        User user = userService.getUser(current.getUsername());
        
        if(!user.getId().equals(post.getCreatedBy())) {
        	throw new AppException("You don't have the right to update this post!");    	
        }

        Post p = mapper.map(request, Post.class);
        if(!p.getId().equals(post.getId())) {
        	throw new AppException("the ids doesn't match");
        }

        p.setSlug(TextUtils.toSlug(p.getTitle()));
        p.setBody(request.getBody().getBytes());
        p.setSummary(TextUtils.toSummary(request.getBody()));

        p.setTopic(topicService.getTopic(request.getTopic().getId()));
        p.setTags(request.getTags());

        postRepo.save(p);
    }

    @Override
    public void vote(String slug, UserPrincipal currentUser, boolean up) {

        User user = userService.getUser(currentUser.getUsername());
        Post post = get(slug);

        Optional<VotePost> vpo = votePostRepo.findByPostAndUser(post, user);
        if(vpo.isPresent()) {
            VotePost vp = vpo.get();
            if(vp.isUp() != up) { // 1&0 0&1
                votePostRepo.delete(vp);
            } // else 0&0 1&1 so do nothing
            return;
        }

        VotePost vp = new VotePost();
        vp.setPost(post);
        vp.setUser(user);
        vp.setUp(up);

        votePostRepo.save(vp);
    }

    private int setVoted(Post post, UserPrincipal currentUser) {

        if (currentUser == null) {
            return 0;
        }

        User user = userService.getUser(currentUser.getUsername());

        Optional<VotePost> vpo = votePostRepo.findByPostAndUser(post, user);
        if(vpo.isPresent()) {
            if(vpo.get().isUp()) return 1;
            else return -1;
        }
        return 0;
    }

    private Post get(String slug) {
        return postRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "slug", slug));
    }

    private int totalVote(String slug) {
        final Post post = get(slug);

        int up = votePostRepo.countAllByPostAndUp(post, true);
        int down = votePostRepo.countAllByPostAndUp(post, false);
        return up - down;
    }

    private TPost toDTO(Post post, UserPrincipal currentUser) {
        TPost tp = mapper.map(post, TPost.class);

        System.out.println(tp);

        tp.setBody(new String(post.getBody()));
        tp.setCreatedBy(userService.getOne(post.getCreatedBy()).getUsername());

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        tp.setCreatedAt(formatter.format(Date.from(post.getCreatedAt())));
        tp.setUpdatedAt(formatter.format(Date.from(post.getUpdatedAt())));

        tp.setComments(commentService.getTComments(post.getSlug(), currentUser));
        tp.setTags(post.getTags());
        tp.setTopic(topicService.toDTO(post.getTopic(), currentUser));

        tp.setVotes(totalVote(post.getSlug()));
        tp.setVoted(setVoted(post, currentUser));

        return tp;
    }
}
