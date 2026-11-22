package com.artemis.boot.common.config;

import com.artemis.boot.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security安全配置类
 * <p>
 * 配置认证链路, 授权规则, 会话策略, 密码编码器, JWT过滤器等
 * 无状态JWT鉴权模式, 不使用服务端Session
 *
 * @author ryan fawcett
 * @since 1.2.1
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    /**
     * JWT认证过滤器，用于解析、校验Token并填充Security上下文
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 安全过滤链核心配置
     * <p>
     * 1. 关闭CSRF防护（前后端分离无状态JWT场景）
     * 2. 设置会话策略为无状态，不创建、不使用HttpSession
     * 3. 授权规则：登录接口放行，其余接口需要认证
     * 4. 注册自定义认证Provider
     * 5. 将JWT过滤器置于用户名密码认证过滤器之前执行
     *
     * @param httpSecurity           SpringSecurity核心构建器
     * @param authenticationProvider 自定义认证提供者
     * @return SecurityFilterChain
     * @throws Exception 构建异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationProvider authenticationProvider) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(registry -> registry.requestMatchers("/api/auth/login").permitAll().anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    /**
     * Dao认证提供者
     * 负责基于UserDetailsService加载用户、执行密码比对
     *
     * @param userDetailsService 用户数据查询服务
     * @param passwordEncoder    密码加密器
     * @return AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * 认证管理器，用于登录接口执行账号密码认证
     *
     * @param config 认证配置
     * @return AuthenticationManager
     * @throws Exception 获取认证管理器异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCrypt密码编码器
     * 密码加密、校验统一使用BCrypt算法，不可逆
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
