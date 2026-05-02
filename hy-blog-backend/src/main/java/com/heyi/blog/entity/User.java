package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;                            // 主键ID
    private String username;                    // 用户名/账号
    private String password;                    // 密码
    private String nickname;                    // 昵称
    private String email;                       // 邮件
    private String avatar;                      // 头像

    public String getAvatar() {
        return "/src/assets/images/me.jpg";
    }
    /**
     * 逻辑删除
     * @TableLogic:
     * 查询时自动带上 WHERE deleted = 0
     * 删除时自动变成 UPDATE SET deleted = 1
     */
    @TableLogic
    private Integer deleted;
}