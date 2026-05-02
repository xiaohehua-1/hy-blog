package com.heyi.blog.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充配置类
 * 作用：帮我们在向数据库插入或更新数据时，自动补全时间和日期，省得每次在 Service 里手动 set。
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入数据时的自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 新增数据时，自动把 createTime 和 updateTime 填为当前系统时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新数据时的自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 修改数据时，只更新 updateTime 字段为当前时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}