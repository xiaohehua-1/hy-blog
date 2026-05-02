package com.heyi.blog.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 作用：用来配置 MyBatis-Plus 的增强功能。目前主要就是开启分页插件，不然博客列表、评论列表都没法正常分页展示。
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册分页拦截器
     * 说明：如果不加这个配置，MyBatis-Plus 的自带分页功能是不生效的（查出来还是全部数据）。
     * 加了之后，我们在 Service 层只要传个 Page 对象，它底层就会自动帮我们在 SQL 后面加上 MySQL 的 LIMIT 语句，省得自己手写分页了。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加 MySQL 的分页插件，告诉框架我们用的是 MySQL 数据库
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}