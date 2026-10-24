package com.artemis.boot.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 后端接口统一返回实体
 * <p>
 * 全局API响应包装对象, 所有控制器接口统一以此对象作为返回值
 *
 * @param <T> 业务数据的泛型类型
 * @author ryan fawcett
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public class CommonResult<T> {

    /**
     * 状态码, 参考 {@link CommonResultCode}
     */
    private Integer code;

    /**
     * 提示信息, 用于前端展示或日志查看
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 私有构造器, 禁止外部直接通过 new 关键字创建实例, 统一使用静态方法构建
     */
    private CommonResult() {
    }

    /**
     * 全参数构建返回对象
     *
     * @param code    业务状态码
     * @param message 提示消息
     * @param data    返回数据
     * @param <T>     数据泛型
     * @return CommonResult实例
     */
    public static <T> CommonResult<T> build(Integer code, String message, T data) {
        return new CommonResult<>(code, message, data);
    }

    /**
     * 根据状态码枚举 + 返回数据构建响应
     *
     * @param commonResultCode 业务状态枚举
     * @param data             返回数据
     * @param <T>              数据泛型
     * @return CommonResult实例
     */
    public static <T> CommonResult<T> build(CommonResultCode commonResultCode, T data) {
        return build(commonResultCode.getCode(), commonResultCode.getMessage(), data);
    }

    /**
     * 根据状态码枚举构建响应，不携带data
     *
     * @param commonResultCode 业务状态枚举
     * @param <T>              数据泛型
     * @return CommonResult实例
     */
    public static <T> CommonResult<T> build(CommonResultCode commonResultCode) {
        return build(commonResultCode, null);
    }

    /**
     * 请求成功，无返回数据，使用默认成功提示
     *
     * @param <T> 数据泛型
     * @return CommonResult实例
     */
    public static <T> CommonResult<T> ok() {
        return build(CommonResultCode.SUCCESS);
    }

    /**
     * 请求成功，自定义提示信息并携带返回数据
     *
     * @param message 自定义成功提示
     * @param data    返回数据
     * @param <T>     数据泛型
     * @return CommonResult实例
     */
    public static <T> CommonResult<T> ok(String message, T data) {
        return build(CommonResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 请求成功，携带返回数据，使用默认成功提示
     *
     * @param data 返回数据
     * @param <T>  数据泛型
     * @return CommonResult实例
     */
    public static <T> CommonResult<T> ok(T data) {
        return build(CommonResultCode.SUCCESS, data);
    }

    /**
     * 默认系统异常，使用50000状态码与默认提示，无data
     *
     * @param <T> 数据泛型
     * @return CommonResult实例
     */
    public static <T> CommonResult<T> error() {
        return build(CommonResultCode.SERVER_ERROR);
    }

    /**
     * 自定义业务错误码和错误信息，不携带data
     *
     * @param code    自定义业务状态码
     * @param message 错误提示信息
     * @param <T>     数据泛型
     * @return CommonResult实例
     */
    public static <T> CommonResult<T> error(Integer code, String message) {
        return build(code, message, null);
    }

    /**
     * 根据状态枚举返回错误信息，不携带data
     *
     * @param commonResultCode 业务状态枚举
     * @param <T>              数据泛型
     * @return CommonResult实例
     */
    public static <T> CommonResult<T> error(CommonResultCode commonResultCode) {
        return build(commonResultCode.getCode(), commonResultCode.getMessage(), null);
    }

}
