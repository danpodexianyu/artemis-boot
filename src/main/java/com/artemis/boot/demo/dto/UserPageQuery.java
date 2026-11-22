package com.artemis.boot.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPageQuery {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String username;
}
