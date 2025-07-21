package com.raxrot.back.controllers;

import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.models.AppRole;
import com.raxrot.back.models.Role;
import com.raxrot.back.models.User;
import com.raxrot.back.repoitories.RoleRepository;
import com.raxrot.back.repoitories.UserRepository;
import com.raxrot.back.security.auth.LoginRequest;
import com.raxrot.back.security.auth.SignUpRequest;
import com.raxrot.back.security.auth.UserInfoResponse;
import com.raxrot.back.security.jwt.JwtUtils;
import com.raxrot.back.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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

    @PostMapping("/signup")
    public ResponseEntity<?>registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
        if (userRepository.existsByUsername(signUpRequest.getUsername())){
            return new ResponseEntity<>("Username is already in use",HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())){
            return new ResponseEntity<>("Email is already in use",HttpStatus.BAD_REQUEST);
        }

        User user=new User(
                signUpRequest.getUsername(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getEmail()
        );
        Set<String>strRoles=signUpRequest.getRoles();
        Set<Role>roles=new HashSet<>();
        if(strRoles==null){
            Role userRole=roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new ApiException("Role not found"));
            roles.add(userRole);
        }else{
            strRoles.forEach(role->{
                switch (role.toLowerCase()){
                    case "admin":
                        Role adminRole=roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(()->new ApiException("Role not found"));
                        roles.add(adminRole);
                        break;
                        case "seller":
                            Role sellerRole=roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(()->new ApiException("Role not found"));
                            roles.add(sellerRole);
                            break;
                            default:
                                Role userRole=roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new ApiException("Role not found"));
                                roles.add(userRole);
                }
            });
        }
        System.out.println("Длина email: " + signUpRequest.getEmail().length());
        System.out.println("Email в HEX: " +
                HexFormat.of().formatHex(signUpRequest.getEmail().getBytes(StandardCharsets.UTF_8)));
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

}
