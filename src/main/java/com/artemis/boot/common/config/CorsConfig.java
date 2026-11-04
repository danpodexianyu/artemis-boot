package com.artemis.boot.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 跨域资源共享 (CORS) 配置
 * <p>
 * 通过 {@code application.yml} 的 {@code artemis.cors.allowed-origins} 属性外部化配置允许的来源白名单,
 * 避免硬编码 {@code "*"} 配合 {@code allowCredentials(true)} 带来的安全隐患。
 * <p>
 * 默认仅放行本地开发地址, 生产环境需在配置文件中显式指定前端域名白名单。
 *
 * @author ryan fawcett
 * @since 1.2.1
 */
@Slf4j
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final List<String> DEFAULT_DEV_ORIGINS = List.of("http://localhost:3000", "http://127.0.0.1:3000");

    private static final String[] ALLOWED_METHODS = {"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"};

    private static final String[] ALLOWED_HEADERS = {"Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"};

    @Value("${artemis.cors.allowed-origins:}")
    private String allowedOriginsConfig;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = resolveOrigins();
        log.info("[CorsConfig] allowed origins: {}", Arrays.toString(origins));

        registry.addMapping("/api/**")
                .allowedOrigins(origins)
                .allowedMethods(ALLOWED_METHODS)
                .allowedHeaders(ALLOWED_HEADERS)
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 解析允许的来源列表
     * <p>
     * 优先读取配置文件, 若未配置则回退到默认开发环境白名单。
     * 配置格式: 逗号分隔的完整 origin, 如
     * {@code https://app.example.com,https://admin.example.com}
     *
     * @return 允许的来源数组
     */
    private String[] resolveOrigins() {
        if (allowedOriginsConfig != null && !allowedOriginsConfig.isBlank()) {
            return Arrays.stream(allowedOriginsConfig.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
        }
        return DEFAULT_DEV_ORIGINS.toArray(String[]::new);
    }
}
