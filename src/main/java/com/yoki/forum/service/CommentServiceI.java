package com.yoki.forum.service;

import com.yoki.forum.data.model.*;
import com.yoki.forum.data.repo.CommentRepo;
import com.yoki.forum.data.repo.ResponseRepo;
import com.yoki.forum.data.repo.VoteCommentRepo;
import com.yoki.forum.dto.model.TComment;
import com.yoki.forum.exception.ResourceNotFoundException;
import com.yoki.forum.security.UserPrincipal;
import com.yoki.forum.util.LogUtils;
import com.yoki.forum.util.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceI implements CommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceI.class);
    private static final String PREFIX = StringUtils.inBracket("CommentService");

    public static final String DATE_TIME_FORMAT = "MMMM dd, yyyy - HH:mm";
    public static final String TIME_ZONE = "Europe/Paris";

    private StringBuilder sb = new StringBuilder(PREFIX);

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private ResponseRepo responseRepo;

    @Autowired
    private VoteCommentRepo voteCommentRepo;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mapper;

    @Override
    public Comment addComment(TComment comment, String slug) {

        Comment c = mapper.map(comment, Comment.class);

        c.setPost(postService.getOPost(slug));

        return commentRepo.save(c);
    }

    @Override
    public List<TComment> getTComments(String slug, UserPrincipal currentUser) {

        return commentRepo.findAllByPost(postService.getOPost(slug)).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .flatMap(com -> toDTO(com, currentUser, 0).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<TComment> getCommentsCreatedBy(String username, UserPrincipal currentUser) {

        return commentRepo.findAllByCreatedBy(userService.getUser(username).getId()).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .flatMap(com -> toDTO(com, currentUser, 0).stream())
                .collect(Collectors.toList());
    }

    @Override
    public Response addReply(TComment comment, Long id) {

        Comment parent = commentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        Response res = mapper.map(comment, Response.class);
        res.setComment(parent);
        res = responseRepo.save(res);

        sb = LogUtils.reset(PREFIX);
        return res;
    }

    @Override
    public void vote(Long id, UserPrincipal currentUser, boolean up) {

        User user = userService.getUser(currentUser.getUsername());
        Comment comment = commentRepo.getOne(id);

        Optional<VoteComment> vco = voteCommentRepo.findByCommentAndUser(comment, user);
        if(vco.isPresent()) {
            VoteComment vc = vco.get();
            if(vc.isUp() != up) { // 1&0 0&1
                voteCommentRepo.delete(vc);
            } // else 0&0 1&1 so do nothing
            return;
        }

        VoteComment vc = new VoteComment();
        vc.setComment(comment);
        vc.setUser(user);
        vc.setUp(up);

        voteCommentRepo.save(vc);
    }

    private int setVoted(Comment comment, UserPrincipal currentUser) {

        if (currentUser == null) {
            return 0;
        }

        User user = userService.getUser(currentUser.getUsername());

        Optional<VoteComment> vco = voteCommentRepo.findByCommentAndUser(comment, user);
        if(vco.isPresent()) {
            if(vco.get().isUp()) return 1;
            else return -1;
        }
        return 0;
    }

    private int totalVote(Long id) {
        final Comment comment = commentRepo.getOne(id);

        int up = voteCommentRepo.countAllByCommentAndUp(comment, true);
        int down = voteCommentRepo.countAllByCommentAndUp(comment, false);
        return up - down;
    }

    private List<TComment> toDTO(Comment comment, UserPrincipal currentUser, int level) {

        List<TComment> comments = new ArrayList<>();

        TComment tc = toDTO(comment, currentUser);
        tc.setLevel(level);

        comments.add(tc);

        List<Response> res = responseRepo.findAllByComment(comment).stream()
                .sorted(Comparator.comparing(Response::getCreatedAt))
                .collect(Collectors.toList());

        if (!res.isEmpty()) {
            comments.addAll(res.stream()
                    .flatMap(r -> toDTO(r, currentUser, level + 1).stream())
                    .collect(Collectors.toList()));
        }

        return comments;
    }

    private TComment toDTO(Comment comment, UserPrincipal currentUser) {

        TComment tc = mapper.map(comment, TComment.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime myDateCr = LocalDateTime.ofInstant(comment.getCreatedAt(), ZoneId.of(TIME_ZONE));

        tc.setCreatedAt(myDateCr.format(formatter).replace("-", "at"));
        tc.setCreatedBy(userService.getOne(comment.getCreatedBy()).getUsername());

        tc.setVotes(totalVote(comment.getId()));
        tc.setVoted(setVoted(comment, currentUser));

        return tc;
    }

}
