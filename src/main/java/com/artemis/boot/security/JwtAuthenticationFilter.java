package com.artemis.boot.security;

import com.artemis.boot.common.config.JwtProperties;
import com.artemis.boot.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${artemis.jwt.disable:false}")
    private boolean disableJwt;

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (disableJwt) {
            // 开发调试
            UserDetails userDetails = User.withUsername("admin").password("mock").authorities("ROLE_ADMIN").build();
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(jwtProperties.getHeader());
        if (authHeader == null || !authHeader.startsWith(jwtProperties.getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.extractToken(authHeader);
        String username;
        try {
            Claims claims = jwtUtil.parseToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(userDetails);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
