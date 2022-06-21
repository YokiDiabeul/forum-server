package com.yoki.forum.data.repo;

import com.yoki.forum.data.model.Trophy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrophyRepo extends JpaRepository<Trophy, Long> {

    Optional<Trophy> findByName(String name);

}
