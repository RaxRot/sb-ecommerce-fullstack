package com.raxrot.back.controllers;

import com.raxrot.back.security.jwt.JwtUtils;
import com.raxrot.back.security.jwt.LoginRequest;
import com.raxrot.back.security.jwt.UserInfoResponse;
import com.raxrot.back.security.services.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;//spring security core
        try {
            authentication=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword())
            );
        }catch (AuthenticationException e){
            Map<String,Object> map=new HashMap<>();
            map.put("message","Bad credentials");
            map.put("status",false);
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails=(UserDetailsImpl)authentication.getPrincipal();
        String jwtToken=jwtUtils.generateJwtFromUsername(userDetails);
        List<String> roles=userDetails.getAuthorities().stream()
                .map(auth->auth.getAuthority()).collect(Collectors.toList());
        UserInfoResponse response=new UserInfoResponse(userDetails.getId(),jwtToken,userDetails.getUsername(),roles);
        return ResponseEntity.ok(response);
    }
}
