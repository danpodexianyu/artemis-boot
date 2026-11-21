package com.artemis.boot.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 元数据自动填充处理器
 * 自动填充 createTime / updateTime / createBy / updateBy
 * <p>
 * 说明：当前脚手架阶段暂未接入登录上下文，createBy、updateBy 先填充默认值；
 * 后续JWT鉴权完善后，再从Security上下文获取当前登录用户名替换
 *
 * @author ryan fawcett
 * @since 1.2.1
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final String DEFAULT_OPERATOR = "SYSTEM";

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "createBy", () -> DEFAULT_OPERATOR, String.class);

        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "updateBy", () -> DEFAULT_OPERATOR, String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        this.strictUpdateFill(metaObject, "updateBy", () -> DEFAULT_OPERATOR, String.class);
    }
}
