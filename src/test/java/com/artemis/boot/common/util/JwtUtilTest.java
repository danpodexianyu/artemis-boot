package com.artemis.boot.common.util;

import com.artemis.boot.common.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void testGenerateParseValidate() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 10001L);
        claims.put("username", "admin");
        String subject = "10001";

        String token = jwtUtil.generateToken(subject, claims);
        assertNotNull(token);

        // 剥离前缀测试
        String bearerHeader = jwtProperties.getPrefix() + token;
        String pureToken = jwtUtil.extractToken(bearerHeader);
        assertEquals(token, pureToken);

        assertTrue(jwtUtil.validateToken(pureToken));

        var payload = jwtUtil.parseToken(pureToken);
        assertEquals(subject, payload.getSubject());
        assertEquals(10001L, payload.get("userId", Long.class));
    }

    @Test
    void testBadToken() {
        assertFalse(jwtUtil.validateToken("fake-token"));
        assertNull(jwtUtil.extractToken("Basic xxx"));
    }
}