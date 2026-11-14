package com.artemis.boot.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "artemis.jwt")
public class JwtProperties {
    /**
     * 签名密钥, 必须至少256bit(HS256要求), 生产环境使用环境变量或配置中心加密存储, 禁止明文硬编码
     * openssl rand -base64 32
     */
    private String secret;

    /**
     * token 有效期, 单位秒
     */
    private Long expire = 3600L;

    /**
     * 请求头名称
     */
    private String header;

    /**
     * token 前缀
     */
    private String prefix = "Bearer ";
}
