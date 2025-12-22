package com.heyi.blog.entity.vo;

import com.heyi.blog.entity.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TagVO extends Tag {
    /**
     * 该标签下的文章数量
     */
    private Integer blogCount;
}