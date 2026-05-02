package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 留言板实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_message")
public class Message extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;                            // 主键ID
    private String nickname;                    // 昵称
    private String email;                       // 邮箱
    private String content;                     // 留言内容
    private String avatar;                      // 头像
    private String address;                     // 留言者博客地址
    private Long rootMessageId;                 // 根留言ID
    private Long parentMessageId;               // 父留言ID
    private Boolean adminMessage;               // 是否为管理员留言

    public String getAvatar() {
        if (Boolean.TRUE.equals(adminMessage)) {
            return "/src/assets/images/me.jpg";
        }
        return avatar;
    }

    // === 新增辅助字段 (不存数据库) ===
    @TableField(exist = false)
    private List<Message> children; // 子留言列表

    @TableField(exist = false)
    private String replyNickname;   // 被回复人的昵称
    /**
     * 逻辑删除字段
     */
    @TableLogic
    private Integer deleted;
}