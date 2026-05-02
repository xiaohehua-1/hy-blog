package com.heyi.blog.entity.dto;

import com.heyi.blog.entity.Blog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * 接收前端博客表单参数：扩展了标签ID数组
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BlogDTO extends Blog {
    private List<Long> tagIds;                  // 前端多传一个标签ID数组
}