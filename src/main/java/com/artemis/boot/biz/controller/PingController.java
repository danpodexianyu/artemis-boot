package com.artemis.boot.biz.controller;

import com.artemis.boot.common.exception.BusinessException;
import com.artemis.boot.common.result.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PingController {

    @GetMapping("/ping")
    public CommonResult<Map<String, Object>> ping() {
        return CommonResult.ok(Map.of("service", "up", "timestamp", System.currentTimeMillis()));
    }

    @GetMapping("/test/biz-error")
    public CommonResult<Void> triggerBizError() {
        throw new BusinessException(40001, "资源不存在");
    }

    @GetMapping("/test/sys-error")
    public CommonResult<Void> triggerSysError() {
        throw new NullPointerException("模拟系统异常");
    }

}
