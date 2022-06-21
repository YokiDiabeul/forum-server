package com.yoki.forum.dto.Response;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ApiResponse {

    private Boolean success;
    private String message;
}
