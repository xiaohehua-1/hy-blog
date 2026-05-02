package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_moment_comment")
public class MomentComment implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long momentId;        // 动态ID
    private Long rootCommentId;   // 根评论
    private Long parentCommentId; // 父评论

    private String nickname;
    private String email;
    private String content;
    private String avatar;
    private String address;       // 网址

    public String getAvatar() {
        if (Boolean.TRUE.equals(adminComment)) {
            return "/src/assets/images/me.jpg";
        }
        return avatar;
    }

    private String ip;            // IP
    private String location;      // 归属地

    private Boolean adminComment; // 是否管理员

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableLogic
    private Boolean deleted;
}