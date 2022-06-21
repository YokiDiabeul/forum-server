package com.yoki.forum.data.repo;

import com.yoki.forum.data.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepo extends JpaRepository<Tag, Long> {
    
}
