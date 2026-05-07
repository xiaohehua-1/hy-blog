package com.heyi.blog.entity.query;

import lombok.Data;

/**
 * 用户查询条件，支持按用户名和昵称搜索
 */
@Data
public class UserQuery {
    private Integer pageNum = 1;  // 当前页码
    private Integer pageSize = 10; // 每页条数
    private String username;       // 按用户名搜索
    private String nickname;       // 按昵称搜索
}