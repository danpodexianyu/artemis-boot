package com.artemis.boot.demo.service;

import com.artemis.boot.demo.entity.User;
import com.artemis.boot.demo.mapper.UserMapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
