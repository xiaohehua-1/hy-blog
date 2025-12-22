package com.heyi.blog.entity.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class FriendQuery implements Serializable {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    // 状态筛选：0-待审核，1-已通过，-1-未通过
    private Integer status;
}