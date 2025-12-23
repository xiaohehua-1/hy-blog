package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Tag;
import com.heyi.blog.utils.R;

import java.util.List;

public interface TagService extends IService<Tag> {

    // 分页查询标签
    IPage<Tag> pageTags(Page<Tag> page);

    List<Tag> getTagsByBlogId(Long blogId);

    // 获取所有标签（用于写文章时下拉选择）
    List<Tag> listAll();

    // 新增
    R saveTag(Tag tag);

    // 修改
    R updateTag(Tag tag);

    // 删除
    R deleteTag(Long id);
}