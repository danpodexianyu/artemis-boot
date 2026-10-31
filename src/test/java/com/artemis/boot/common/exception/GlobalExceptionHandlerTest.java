package com.artemis.boot.common.exception;

import com.artemis.boot.common.result.CommonResult;
import com.artemis.boot.common.result.CommonResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link GlobalExceptionHandler} 全局异常处理器单元测试
 * <p>
 * 覆盖: 业务异常, 系统异常兜底, 参数校验边界场景
 *
 * @author ryan fawcett
 * @since 1.0.0
 */
@SuppressWarnings("ConstantConditions")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Nested
    class BusinessExceptionTest {
        @Test
        void shouldReturnCodeAndMessageFromBusinessException() {
            BusinessException ex = new BusinessException(10002, "资源不存在");

            CommonResult<Void> result = handler.handleBusinessException(ex);
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(10002);
            assertThat(result.getMessage()).isEqualTo("资源不存在");
            assertThat(result.getData()).isNull();
        }

        @Test
        void shouldHandleNullMessageGracefully() {
            BusinessException ex = new BusinessException(10002, null);

            CommonResult<Void> result = handler.handleBusinessException(ex);
            assertThat(result.getCode()).isEqualTo(10002);
            assertThat(result.getMessage()).isNull();
        }
    }

    @Nested
    class SystemExceptionTest {
        @Test
        void shouldReturnSystemErrorWithoutLeakingDetails() {
            RuntimeException ex = new RuntimeException("db password=123456 at com.artemis.boot.SecretService");

            CommonResult<Void> result = handler.handleException(ex);
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(CommonResultCode.SYSTEM_ERROR.getCode());
            assertThat(result.getMessage()).isEqualTo(CommonResultCode.SYSTEM_ERROR.getMessage());
            assertThat(result.getData()).isNull();
            assertThat(result.getMessage()).doesNotContain("password").doesNotContain("at com.artemis").doesNotContain("RuntimeException");
        }

        @Test
        void shouldReturnSystemErrorWithNullMessage() {
            RuntimeException ex = new RuntimeException();

            CommonResult<Void> result = handler.handleException(ex);
            assertThat(result.getCode()).isEqualTo(CommonResultCode.SYSTEM_ERROR.getCode());
            assertThat(result.getMessage()).isEqualTo(CommonResultCode.SYSTEM_ERROR.getMessage());
        }
    }

    @Nested
    class MethodArgumentNotValidTest {

        @Test
        void shouldReturnParamValidateErrorWhenFieldInvalid() {
            FieldError fieldError = new FieldError("userDTO", "username", "不能为空");
            BindingResult bindingResult = mock(BindingResult.class);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
            CommonResult<Void> result = handler.handleMethodArgumentNotValidException(ex);

            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(CommonResultCode.PARAM_VALIDATE_ERROR.getCode());
            assertThat(result.getMessage()).isEqualTo(CommonResultCode.PARAM_VALIDATE_ERROR.getMessage());
        }

        @Test
        void shouldHandleMultipleFieldErrors() {
            BindingResult bindingResult = mock(BindingResult.class);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(
                    new FieldError("userDTO", "username", "不能为空"),
                    new FieldError("userDTO", "age", "必须大于 0")
            ));

            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
            CommonResult<Void> result = handler.handleMethodArgumentNotValidException(ex);
            assertThat(result.getCode()).isEqualTo(CommonResultCode.PARAM_VALIDATE_ERROR.getCode());
        }
    }

    @Nested
    class ConstraintViolationTest {
        @Test
        void shouldReturnParamValidateErrorWhenConstraintViolated() {
            ConstraintViolation<?> violation = mock(ConstraintViolation.class);
            Path path = mock(Path.class);
            when(path.toString()).thenReturn("method.page");
            when(violation.getPropertyPath()).thenReturn(path);
            when(violation.getMessage()).thenReturn("必须大于 0");

            ConstraintViolationException ex = new ConstraintViolationException("invalid", Set.of(violation));

            CommonResult<Void> result = handler.handleConstraintViolationException(ex);

            assertThat(result.getCode()).isEqualTo(CommonResultCode.PARAM_VALIDATE_ERROR.getCode());
        }

        @Test
        void shouldHandleEmptyConstraintViolations() {
            ConstraintViolationException ex = new ConstraintViolationException(Set.of());

            CommonResult<Void> result = handler.handleConstraintViolationException(ex);

            assertThat(result.getCode()).isEqualTo(CommonResultCode.PARAM_VALIDATE_ERROR.getCode());
        }
    }

    @Nested
    class MissingOrUnreadableTest {
        @Test
        void shouldHandleMissingPathVariable() {
            MissingPathVariableException ex = new MissingPathVariableException("id", mock(org.springframework.core.MethodParameter.class));

            CommonResult<Void> result = handler.handleMissingPathVariableException(ex);

            assertThat(result.getCode()).isEqualTo(CommonResultCode.PARAM_VALIDATE_ERROR.getCode());
        }

        @Test
        void shouldHandleMissingServletRequestParameter() {
            MissingServletRequestParameterException ex = new MissingServletRequestParameterException("page", "Integer");

            CommonResult<Void> result = handler.handleMissingServletRequestParameterException(ex);

            assertThat(result.getCode()).isEqualTo(CommonResultCode.PARAM_VALIDATE_ERROR.getCode());
        }

        @Test
        void shouldHandleHttpMessageNotReadable() {
            HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error", null);

            CommonResult<Void> result = handler.handleHttpMessageNotReadableException(ex);

            assertThat(result.getCode()).isEqualTo(CommonResultCode.PARAM_VALIDATE_ERROR.getCode());
        }
    }
}