package com.artemis.boot.common.exception;

import com.artemis.boot.common.result.CommonResult;
import com.artemis.boot.common.result.CommonResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 使用 {@link RestControllerAdvice} 注解开启全局异常拦截能力,
 * 将所有Controller层抛出的异常统一捕获并封装为标准化的 {@link CommonResult} 结构返回,
 * 确保无论正常请求还是异常请求, 前端接收的返回格式始终一致
 *
 * @author ryan fawcett
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获自定义业务异常
     * <p>
     * 提取异常中的状态码和提示信息, 直接封装为统一返回结构.
     * 适用于业务逻辑中主动抛出的可预期异常场景.
     *
     * @param e 业务异常实例
     * @return 封装了异常状态码和提示信息的统一返回结果
     */
    @ExceptionHandler(BusinessException.class)
    public CommonResult<Void> handleBusinessException(BusinessException e) {
        log.warn("[BusinessException] code={}, message={}", e.getCode(), e.getMessage());
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 捕获请求体参数校验异常 {@link MethodArgumentNotValidException}
     * <p>
     * 当Controller方法使用 {@code @RequestBody @Valid} 接收参数时,
     * 如果 DTO 中的字段不满足校验注解规则, Spring MVC 将会抛出这个异常
     *
     * @param e 请求体参数异常实例
     * @return 封装了参数校验失败的统一返回结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMsg = fieldErrors.stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining(";"));
        log.warn("[MethodArgumentNotValidException] invalid request body: {}", errorMsg);
        return CommonResult.error(CommonResultCode.PARAM_VALIDATE_ERROR);
    }

    /**
     * 捕获单参数校验异常 {@link ConstraintViolationException} (Jakarta Validation 原生异常)
     * <p>
     * 当Controller类或方法上添加了 {@code @Validated}, 并在方法参数中直接使用 {@code @RequestParam}
     * 或 {@code @PathVariable} 配合 Jakarta Validation 约束注解进行单参数校验时, 若校验失败, 则会抛出此异常.
     *
     * @param e 单参数约束违反异常实例
     * @return 封装了参数校验失败的统一返回结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public CommonResult<Void> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        String errorMsg = constraintViolations.stream()
                .map(violation -> {
                    String propertyPath = violation.getPropertyPath().toString();
                    String fieldName = propertyPath.contains(".") ? propertyPath.substring(propertyPath.lastIndexOf(".") + 1) : propertyPath;
                    return fieldName + ": " + violation.getMessage();
                })
                .collect(Collectors.joining(";"));
        log.warn("[ConstraintViolationException] invalid request params: {}", errorMsg);
        return CommonResult.error(CommonResultCode.PARAM_VALIDATE_ERROR);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public CommonResult<Void> handleMissingPathVariableException(MissingPathVariableException e) {
        log.warn("[MissingPathVariableException] missing required path param: {}", e.getVariableName());
        return CommonResult.error(CommonResultCode.PARAM_VALIDATE_ERROR);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CommonResult<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("[MissingServletRequestParameterException] required request param missing: {}", e.getParameterName());
        return CommonResult.error(CommonResultCode.PARAM_VALIDATE_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResult<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("[HttpMessageNotReadableException] request body unreadable: {}", e.getMessage());
        return CommonResult.error(CommonResultCode.PARAM_VALIDATE_ERROR);
    }

    /**
     * 捕获通用异常 {@link Exception}
     * <p>
     * 作为全局兜底异常处理逻辑, 处理所有未被上述精准拦截器捕获的未知异常.
     * 返回系统默认错误提示 {@link CommonResultCode#SYSTEM_ERROR}, 避免将底层错误堆栈信息直接暴露给前端,
     * 同时便于日志记录和问题排查
     *
     * @param e 通用异常实例
     * @return 封装了系统默认错误提示的统一返回结果
     */
    @ExceptionHandler(Exception.class)
    public CommonResult<Void> handleException(Exception e) {
        log.error("[SystemException] internal server error: {}", e.getMessage(), e);
        return CommonResult.error(CommonResultCode.SYSTEM_ERROR);
    }
}

