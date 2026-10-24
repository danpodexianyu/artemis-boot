package com.artemis.boot.common.result;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommonResultTest {

    @Test
    void testAllBuildMethod() {
        // 1. ok() 无数据成功
        CommonResult<Void> okEmpty = CommonResult.ok();
        assertThat(okEmpty.getCode()).isEqualTo(CommonResultCode.SUCCESS.getCode());
        assertThat(okEmpty.getMessage()).isEqualTo(CommonResultCode.SUCCESS.getMessage());
        assertThat(okEmpty.getData()).isNull();

        // 2. ok(T data) 带数据成功
        String testData = "hello-artemis";
        CommonResult<String> okWithData = CommonResult.ok(testData);
        assertThat(okWithData.getCode()).isEqualTo(CommonResultCode.SUCCESS.getCode());
        assertThat(okWithData.getData()).isEqualTo(testData);

        // 3. ok(message, data) 自定义成功提示
        String customMsg = "操作完成";
        CommonResult<Integer> okCustomMsg = CommonResult.ok(customMsg, 666);
        assertThat(okCustomMsg.getCode()).isEqualTo(CommonResultCode.SUCCESS.getCode());
        assertThat(okCustomMsg.getMessage()).isEqualTo(customMsg);
        assertThat(okCustomMsg.getData()).isEqualTo(666);

        // 4. error() 默认系统异常
        CommonResult<Void> errorDefault = CommonResult.error();
        assertThat(errorDefault.getCode()).isEqualTo(CommonResultCode.SERVER_ERROR.getCode());
        assertThat(errorDefault.getData()).isNull();

        // 5. error(code, message) 自定义码+信息
        int testCode = 40000;
        String testMsg = "参数非法";
        CommonResult<Void> errorWithCodeMsg = CommonResult.error(testCode, testMsg);
        assertThat(errorWithCodeMsg.getCode()).isEqualTo(testCode);
        assertThat(errorWithCodeMsg.getMessage()).isEqualTo(testMsg);
        assertThat(errorWithCodeMsg.getData()).isNull();

        // 6. error(enum) 枚举构造错误
        CommonResult<Void> errorByEnum = CommonResult.error(CommonResultCode.PARAM_VALIDATE_ERROR);
        assertThat(errorByEnum.getCode()).isEqualTo(CommonResultCode.PARAM_VALIDATE_ERROR.getCode());
        assertThat(errorByEnum.getMessage()).isEqualTo(CommonResultCode.PARAM_VALIDATE_ERROR.getMessage());
        assertThat(errorByEnum.getData()).isNull();

        // 7. build(enum, data) 枚举 + 数据
        CommonResult<Boolean> buildEnumWithData = CommonResult.build(CommonResultCode.SUCCESS, true);
        assertThat(buildEnumWithData.getCode()).isEqualTo(CommonResultCode.SUCCESS.getCode());
        assertThat(buildEnumWithData.getData()).isTrue();

        // 8. build(enum) 枚举、无data
        CommonResult<Void> buildEnumEmpty = CommonResult.build(CommonResultCode.PARAM_VALIDATE_ERROR);
        assertThat(buildEnumEmpty.getCode()).isEqualTo(CommonResultCode.PARAM_VALIDATE_ERROR.getCode());
        assertThat(buildEnumEmpty.getData()).isNull();

        // 9. build(code,msg,data) 原始全参静态构建
        CommonResult<String> rawBuild = CommonResult.build(20000, "自定义成功", "raw-data");
        assertThat(rawBuild.getCode()).isEqualTo(20000);
        assertThat(rawBuild.getMessage()).isEqualTo("自定义成功");
        assertThat(rawBuild.getData()).isEqualTo("raw-data");
    }

}