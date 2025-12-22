package com.heyi.blog.entity.query;

import lombok.Data;

/**
 * 用户查询对象
 */
@Data
public class UserQuery {
    private Integer pageNum = 1;  // 当前页
    private Integer pageSize = 10; // 每页条数
    private String username;       // 搜索用户名
    private String nickname;       // 搜索昵称
}