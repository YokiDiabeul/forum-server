package com.yoki.forum.controller;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.yoki.forum.data.model.RoleName;
import com.yoki.forum.data.model.User;
import com.yoki.forum.dto.Request.LoginRequest;
import com.yoki.forum.dto.Request.SignUpRequest;
import com.yoki.forum.dto.Response.ApiResponse;
import com.yoki.forum.dto.Response.JwtAuthenticationResponse;
import com.yoki.forum.security.JwtTokenProvider;
import com.yoki.forum.security.UserPrincipal;
import com.yoki.forum.service.UserService;
import com.yoki.forum.util.LogUtils;
import com.yoki.forum.util.StringUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String AUTH_CONTROLLER = "AuthController";
    
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private static final String PREFIX = StringUtils.inBracket(AUTH_CONTROLLER);
    private StringBuilder sb = new StringBuilder(PREFIX);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LogUtils.start(sb, "authenticateUser",LOGGER);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        LOGGER.info(sb + " user log in : " + principal.getUsername());
        Set<RoleName> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(RoleName::valueOf)
                .collect(Collectors.toSet());

        sb = LogUtils.reset(PREFIX);
        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        LogUtils.start(sb, "registerUser", LOGGER);

        User result = userService.signup(signUpRequest);
        LOGGER.info(sb + " user saved " + result);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        sb = LogUtils.reset(PREFIX);
        return ResponseEntity.created(location).body(new ApiResponse(true, "Register successfully"));
    }
}