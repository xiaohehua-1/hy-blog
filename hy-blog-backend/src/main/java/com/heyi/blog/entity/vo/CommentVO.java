package com.heyi.blog.entity.vo;
import com.heyi.blog.entity.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentVO extends Comment {
    private String blogTitle; // 增加文章标题字段
}