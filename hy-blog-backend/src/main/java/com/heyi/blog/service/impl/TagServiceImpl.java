package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Tag;
import com.heyi.blog.mapper.TagMapper;
import com.heyi.blog.service.TagService;
import com.heyi.blog.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签业务实现类 (纯净后台版)
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public IPage<Tag> pageTags(Page<Tag> page) {
        return this.page(page, new LambdaQueryWrapper<Tag>().orderByDesc(Tag::getId));
    }

    @Override
    public List<Tag> listAll() {
        return this.list();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R saveTag(Tag tag) {
        // 1. 检查名称是否重复
        if (tag.getId() != null && this.getById(tag.getId()) != null) {
            return R.error("该ID已存在，请重新输入");
        }
        Tag exist = this.getOne(new LambdaQueryWrapper<Tag>().eq(Tag::getName, tag.getName()));
        if (exist != null) {
            return R.error("该标签名称已存在");
        }
        return this.save(tag) ? R.success() : R.error("添加失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R updateTag(Tag tag) {
        // 1. 检查名称是否重复 (排除自己)
        Tag exist = this.getOne(new LambdaQueryWrapper<Tag>().eq(Tag::getName, tag.getName()));
        if (exist != null && !exist.getId().equals(tag.getId())) {
            return R.error("该标签名称已存在");
        }
        return this.updateById(tag) ? R.success() : R.error("更新失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R deleteTag(Long id) {
        // 后续如果需要“有文章引用则禁止删除”的逻辑，可以在这里加
        return this.removeById(id) ? R.success() : R.error("删除失败");
    }
}