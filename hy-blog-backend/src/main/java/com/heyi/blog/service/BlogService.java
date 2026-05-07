/**
 * 博客文章服务接口
 *
 * 提供文章的后台 CRUD、分页查询及随机推荐功能。
 * 增/改操作同时维护文章-标签关联关系。
 */
package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Blog;
import com.heyi.blog.entity.dto.BlogDTO;
import com.heyi.blog.entity.query.BlogQuery;
import com.heyi.blog.utils.R;

import java.util.List;

public interface BlogService extends IService<Blog> {

    /** 后台多条件分页查询（标题/分类/标签/推荐/发布状态） */
    IPage<Blog> pageAdminBlogs(BlogQuery query);

    /** 新增文章并绑定标签 */
    R saveBlog(BlogDTO blogDTO, List<Long> tagIds);

    /** 更新文章并重置标签关联（先删后加） */
    R updateBlog(BlogDTO blogDTO, List<Long> tagIds);

    /** 获取文章详情及关联标签ID列表，用于后台编辑回显 */
    R getBlogDetail(Long id);

    /** 删除文章并清理标签关联 */
    R deleteBlog(Long id);

    /** 批量删除文章并清理标签关联 */
    R deleteBatch(List<Long> ids);

    /** 随机获取一篇已发布文章的ID，用于前台推荐 */
    Long getRandomBlogId();
}