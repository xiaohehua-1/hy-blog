package com.heyi.blog.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充处理器
 * 配合实体类 @TableField(fill = FieldFill.INSERT/UPDATE) 注解，自动补全 createTime 和 updateTime
 * 避免在每个 Service 方法中手动设置时间
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充 createTime 和 updateTime 为当前时间
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // strictInsertFill：仅在字段值为 null 时才填充，不覆盖已有值
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新时自动填充 updateTime 为当前时间
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // strictUpdateFill：仅在字段值为 null 时才填充
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}