package com.yoki.forum.dto.model;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TTopic {

    private Long id;
    @NotNull
    private String name;
    private boolean sub;
    private int nbSubs;
}