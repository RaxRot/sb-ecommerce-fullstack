package com.raxrot.back.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {
    private String jwt;
    private String username;
    private List<String> roles;
}
