package com.artemis.boot.common.util;

import com.artemis.boot.common.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        String secret = jwtProperties.getSecret();
        if (!StringUtils.hasText(secret)) {
            throw new IllegalArgumentException("secret is empty");
        }

        byte[] bytes = null;
        try {
            bytes = Base64.getDecoder().decode(secret);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid secret");
        }
        secretKey = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 根据用户信息生成token
     *
     * @param subject 主题, 一般放{@code userId}或者{@code username}
     * @param claims  自定义载荷
     * @return jwt token, 原始token, 不带Bearer前缀
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        long currentTimeMillis = System.currentTimeMillis();

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(currentTimeMillis))
                .expiration(new Date(currentTimeMillis + jwtProperties.getExpire() * 1000))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 解析 Token, 获取自定义载荷Claims
     *
     * @param token jwt token
     * @return Claims
     */
    public Claims parseToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new MalformedJwtException("token is empty");
        }

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 校验 Token 是否有效：签名合法 + 未过期
     *
     * @param token jwt token
     * @return true合法, false 非法/过期/格式错误
     */
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("jwt token has expired");
            return false;
        } catch (SignatureException e) {
            log.warn("invalid jwt signature");
            return false;
        } catch (MalformedJwtException e) {
            log.warn("invalid jwt format");
            return false;
        } catch (IncorrectClaimException e) {
            log.warn("invalid jwt claims");
            return false;
        } catch (JwtException e) {
            log.warn("jwt validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 剥离Bearer前缀
     *
     * @param authHeader 请求头
     * @return jwt token, 不符合前缀规则返回null
     */
    public String extractToken(String authHeader) {
        String prefix = jwtProperties.getPrefix();
        if (StringUtils.hasText(prefix) && authHeader.startsWith(prefix)) {
            return authHeader.substring(prefix.length());
        }
        return null;
    }
}
