package com.yoki.forum.service;

import com.yoki.forum.data.model.Role;
import com.yoki.forum.data.model.RoleName;
import com.yoki.forum.data.model.Trophy;
import com.yoki.forum.data.model.User;
import com.yoki.forum.data.repo.RoleRepo;
import com.yoki.forum.data.repo.UserRepo;
import com.yoki.forum.dto.Request.SignUpRequest;
import com.yoki.forum.exception.AppException;
import com.yoki.forum.exception.ConflictRequestException;
import com.yoki.forum.exception.ResourceNotFoundException;
import com.yoki.forum.security.UserPrincipal;
import com.yoki.forum.util.LogUtils;
import com.yoki.forum.util.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceI implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceI.class);
    private static final String PREFIX = StringUtils.inBracket("UserService");
    private StringBuilder sb = new StringBuilder(PREFIX);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<User> getAllUsers() {
        LogUtils.info("getAllUsers", LOGGER);
        return userRepo.findAll();
    }

    @Override
    public User getUser(String usernameOrEmail) {
        return userRepo.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", usernameOrEmail));
    }

    @Override
    public User signup(SignUpRequest signUpRequest) {

        User user = testUserExist(modelMapper.map(signUpRequest, User.class));

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepo.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));
        user.setKarma(Long.valueOf(0));

        user = userRepo.save(user);

        sb = LogUtils.reset(PREFIX);
        return user;
    }

    @Override
    public User updateUser(UserPrincipal currentUser, User request) {

        User user = userRepo.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getId()));

        Optional.ofNullable(user.getId().equals(currentUser.getId()))
                .orElseThrow(() -> new AppException("You don't have the right to change this user"));

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());

        return userRepo.save(user);
    }

    @Override
    public User getOne(Long id) {
        return userRepo.getOne(id);
    }

    @Override
    public List<Trophy> getUserTrophy(String username, UserPrincipal currentUser) {

        User user = getUser(username);

        Optional.ofNullable(user.getId().equals(currentUser.getId()))
                .orElseThrow(() -> new AppException("You don't have the right to change this user"));

        return user.getTrophies();
    }

    @Override
    public void addTrophy(String username, Trophy trophy) {
        getUser(username).getTrophies().add(trophy);
    }

    private User testUserExist(User user) {

        if(userRepo.existsByUsername(user.getUsername())){
            throw new ConflictRequestException("Username already in use!");
        }

        if(userRepo.existsByEmail(user.getEmail())) {
            throw new ConflictRequestException("Email Address already in use!");
        }

        return user;
    }
}
