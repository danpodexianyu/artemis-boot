package com.artemis.boot.demo.entity;

import com.artemis.boot.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("t_demo_user")
public class User extends BaseEntity {
    private Long id;
    private String username;
    private String nickname;
    private String email;
}
