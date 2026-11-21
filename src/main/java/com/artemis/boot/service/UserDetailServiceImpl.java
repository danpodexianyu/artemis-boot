package com.artemis.boot.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        // Mock
        if ("admin".equals(username)) {
            return User.withUsername(username)
                    .password("$2a$10$akCfL4TkEH6gZAU//yCmaeFIzWKxCXl5sWfW1/9J31Buk0FkBP/mG") // 123456
                    .authorities(Collections.emptyList())
                    .build();
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
