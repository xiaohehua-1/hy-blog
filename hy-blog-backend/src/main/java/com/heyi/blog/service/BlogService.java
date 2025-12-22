package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Blog;
import com.heyi.blog.entity.dto.BlogDTO;
import com.heyi.blog.entity.query.BlogQuery;
import com.heyi.blog.utils.R;

import java.util.List;

public interface BlogService extends IService<Blog> {

    // 后台分页查询
    IPage<Blog> pageAdminBlogs(BlogQuery query);

    // 新增文章
    R saveBlog(BlogDTO blogDTO, List<Long> tagIds);

    // 更新文章
    R updateBlog(BlogDTO blogDTO, List<Long> tagIds);

    // 获取文章详情（后台回显用）
    R getBlogDetail(Long id);

    // 删除文章
    R deleteBlog(Long id);

    // 批量删除
    R deleteBatch(List<Long> ids);
}