package com.raxrot.back.security.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UserInfoResponse {
    private Long id;
    private String jwt;
    private String username;
    private List<String> roles;
}
