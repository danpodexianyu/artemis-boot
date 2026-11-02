package com.artemis.boot.common.exception;

import com.artemis.boot.common.result.CommonResultCode;
import lombok.Getter;

/**
 * 自定义业务异常类
 * <p>
 * 继承自 {@link RuntimeException}, 作为非检查型异常使用, 无需在方法签名上显示声明
 * 支持通过 {@link CommonResultCode} 枚举快速定义异常状态, 也支持传入自定义消息覆盖默认提示
 * 全局异常处理器 {@link GlobalExceptionHandler} 会专门捕获此异常并封装为统一返回体
 *
 * @author ryan fawcett
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    private final String message;

    public BusinessException(CommonResultCode commonResultCode) {
        super(commonResultCode.getMessage());
        this.code = commonResultCode.getCode();
        this.message = commonResultCode.getMessage();
    }

    public BusinessException(CommonResultCode commonResultCode, String message) {
        super(message);
        this.code = commonResultCode.getCode();
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
