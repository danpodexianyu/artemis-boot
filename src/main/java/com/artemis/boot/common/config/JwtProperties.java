package com.artemis.boot.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性类
 * 读取配置前缀 artemis.jwt 下的相关配置
 *
 * @author ryan fawcett
 * @since 1.2.1
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "artemis.jwt")
public class JwtProperties {
    /**
     * 签名密钥, 必须至少256bit(HS256要求), 生产环境使用环境变量或配置中心加密存储, 禁止明文硬编码
     * 生成命令: openssl rand -base64 32
     */
    private String secret;

    /**
     * token 有效期, 单位: 秒
     * 默认值: 3600s(1h)
     */
    private Long expire = 3600L;

    /**
     * 请求头名称
     */
    private String header = "Authorization";

    /**
     * token 前缀
     */
    private String prefix = "Bearer ";
}
