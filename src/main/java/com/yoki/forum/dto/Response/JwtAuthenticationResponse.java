package com.yoki.forum.dto.Response;

import com.yoki.forum.data.model.RoleName;
import lombok.Data;

import java.util.Set;

@Data
public class JwtAuthenticationResponse {

    public static final String BEARER = "Bearer";

    private String accessToken;
    private String tokenType = BEARER;
    private Set<RoleName> roles;

    public JwtAuthenticationResponse(String accessToken, Set<RoleName> roles) {
        this.accessToken = accessToken;
        this.roles = roles;
    }
}
