package com.heyi.blog.entity.vo;
import com.heyi.blog.entity.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论列表视图对象，继承 Comment 并追加文章标题用于后台展示
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentVO extends Comment {
    private String blogTitle; // 所属文章标题，方便后台审核时定位上下文
}