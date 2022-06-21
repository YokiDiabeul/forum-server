package com.yoki.forum.data.repo;


import com.yoki.forum.data.model.Comment;
import com.yoki.forum.data.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseRepo extends JpaRepository<Response, Long> {

    List<Response> findAllByComment(Comment c);

}
