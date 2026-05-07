/**
 * 文章标签服务接口
 *
 * 提供标签 CRUD、文章-标签关联查询及前台标签云展示功能。
 * 批量查询方法用于解决 N+1 问题。
 */
package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Tag;
import com.heyi.blog.utils.R;

import java.util.List;
import java.util.Map;

public interface TagService extends IService<Tag> {

    /** 后台分页查询标签 */
    IPage<Tag> pageTags(Page<Tag> page);

    /** 查询指定文章的所有标签 */
    List<Tag> getTagsByBlogId(Long blogId);

    /** 批量查询多篇文章的标签，解决 N+1 问题 */
    Map<Long, List<Tag>> getTagsByBlogIds(List<Long> blogIds);

    /** 获取前台标签列表（含每标签下已发布文章数量） */
    List<Map<String, Object>> listNameAndCount();

    /** 获取所有标签，用于写文章时下拉选择 */
    List<Tag> listAll();

    // ---- 增删改 ----
    R saveTag(Tag tag);
    R updateTag(Tag tag);
    R deleteTag(Long id);
}