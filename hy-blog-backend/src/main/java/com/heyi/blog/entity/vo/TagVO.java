package com.heyi.blog.entity.vo;

import com.heyi.blog.entity.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签视图对象，继承 Tag 并追加文章计数用于标签页/标签云展示
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TagVO extends Tag {
    /** 该标签下的文章数量 */
    private Integer blogCount;
}