package com.heyi.blog.entity.query;

import lombok.Data;
import java.io.Serializable;

/**
 * 博客列表查询条件：用于后台管理多条件筛选文章
 */
@Data
public class BlogQuery implements Serializable {

    private Integer pageNum = 1;                // 当前页码 (默认第1页)
    private Integer pageSize = 10;              // 每页条数 (默认10条)
    private String title;                       // 搜索标题
    private Long typeId;                        // 搜索分类ID
    private Boolean recommend;                  // 是否推荐
    private Boolean published;                  // 是否发布
    private Integer copyright;                  // 版权类型 (1-原创 2-转载 3-翻译)
    private Long tagId;                         // 按标签筛选
}