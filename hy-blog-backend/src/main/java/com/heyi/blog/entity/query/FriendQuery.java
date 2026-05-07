package com.heyi.blog.entity.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 友链查询条件，支持按审核状态筛选
 */
@Data
public class FriendQuery implements Serializable {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    // 审核状态筛选：null-全部，0-待审核，1-已通过，-1-未通过
    private Integer status;
}