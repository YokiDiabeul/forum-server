package com.yoki.forum.data.repo;

import com.yoki.forum.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);

    void deleteByUsername(String username);

    Boolean existsByUsername(String unsername);
    Boolean existsByEmail(String email);
    Boolean existsByEmailOrUsername(String email, String username);
}
