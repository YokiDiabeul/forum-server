
package com.yoki.forum.dto.model;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class TComment {

    private Long id;
    @NotBlank
    private String text;
    private int level;
    private int votes;
    private int voted;
    private String createdBy;
    private String createdAt;
}
