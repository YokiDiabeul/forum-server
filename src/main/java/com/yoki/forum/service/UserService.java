package com.yoki.forum.service;

import com.yoki.forum.data.model.Trophy;
import com.yoki.forum.data.model.User;
import com.yoki.forum.dto.Request.SignUpRequest;
import com.yoki.forum.security.UserPrincipal;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User signup(SignUpRequest signUpRequest);

    User getUser(String username);

    User updateUser(UserPrincipal currentUser, User request);

    User getOne(Long id);

    List<Trophy> getUserTrophy(String username, UserPrincipal currentUser);

    void addTrophy(String username, Trophy trophy);
}
