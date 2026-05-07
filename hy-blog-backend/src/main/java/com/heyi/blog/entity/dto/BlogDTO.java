package com.heyi.blog.entity.dto;

import com.heyi.blog.entity.Blog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * 博客表单接收对象，继承 Blog 基础字段并扩展标签 ID 数组
 * 前端编辑/新建文章时，标签以 Long[] 形式提交
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BlogDTO extends Blog {
    private List<Long> tagIds;                  // 前端多传一个标签ID数组
}