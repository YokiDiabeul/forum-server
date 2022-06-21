package com.yoki.forum.data.repo;

import com.yoki.forum.data.model.Role;
import com.yoki.forum.data.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName roleName);

}
