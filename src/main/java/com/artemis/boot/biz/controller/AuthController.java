package com.artemis.boot.biz.controller;

import com.artemis.boot.common.util.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResp> login(@RequestBody LoginReq req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.username, req.password));
        UserDetails user = userDetailsService.loadUserByUsername(req.username);
        String token = jwtUtil.generateToken(user.getUsername(), Map.of());
        return ResponseEntity.ok(new LoginResp(token));
    }

    @Data
    public static class LoginReq {
        private String username;
        private String password;
    }

    @Data
    public static class LoginResp {
        private String token;
        public LoginResp(String token) {
            this.token = token;
        }
    }
}
