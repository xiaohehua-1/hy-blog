package com.heyi.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.heyi.blog.mapper") // 扫描 Mapper 文件夹
public class HyBlogBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HyBlogBackendApplication.class, args);
    }

}