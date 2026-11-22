package com.artemis.boot.demo.controller;

import com.artemis.boot.common.result.CommonResult;
import com.artemis.boot.demo.entity.User;
import com.artemis.boot.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo/user")
@RequiredArgsConstructor
public class DemoUserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public CommonResult<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return CommonResult.ok(user);
    }
}
