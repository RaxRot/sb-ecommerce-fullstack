package com.raxrot.back.util;

import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.models.User;
import com.raxrot.back.repoitories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    @Autowired
    private UserRepository userRepository;

    public String loggedInEmail(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUsername(auth.getName()).orElseThrow(()->new ApiException("User not found"));
        return user.getEmail();
    }

    public User loggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUsername(auth.getName()).orElseThrow(()->new ApiException("User not found"));
        return user;
    }
}
