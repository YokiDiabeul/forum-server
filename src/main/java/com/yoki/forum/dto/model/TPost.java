package com.yoki.forum.dto.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import com.yoki.forum.data.model.Tag;

import lombok.Data;

@Data
public class TPost {

    private Long id;
    @NotBlank
    private String title;
    private String slug;
    @NotBlank
    private String body;
    private String summary;
    private TTopic topic;
    private Set<Tag> tags = new HashSet<>();
    private List<TComment> comments = new ArrayList<>();

    private int votes;
    private int voted;
    private String createdBy;
    private String createdAt;
    private String updatedAt;
}
