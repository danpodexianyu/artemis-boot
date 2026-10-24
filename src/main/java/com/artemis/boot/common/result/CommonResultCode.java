package com.artemis.boot.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接口响应业务状态码枚举
 *
 * @author ryan fawcett
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum CommonResultCode {
    SUCCESS(20000, "操作成功"),
    SERVER_ERROR(50000, "系统异常, 请稍后重试"),
    PARAM_VALIDATE_ERROR(40000, "参数校验失败");

    private final Integer code;
    private final String message;
}
