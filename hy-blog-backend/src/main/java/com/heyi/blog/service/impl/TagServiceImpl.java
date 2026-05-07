package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.BlogTag;
import com.heyi.blog.entity.Tag;
import com.heyi.blog.mapper.mybatis.BlogTagMapper;
import com.heyi.blog.mapper.mybatis.TagMapper;
import com.heyi.blog.service.TagService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 标签业务实现类
 *
 * 提供标签 CRUD、文章-标签关联查询及前台标签云统计。
 * 批量查询方法用于解决文章列表的 N+1 问题。
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Override
    public IPage<Tag> pageTags(Page<Tag> page) {
        return this.page(page, new LambdaQueryWrapper<Tag>().orderByDesc(Tag::getId));
    }

    @Override
    public List<Tag> listAll() {
        return this.list();
    }

    /**
     * 新增标签，校验ID和名称唯一性
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R saveTag(Tag tag) {
        if (tag.getId() != null && this.getById(tag.getId()) != null) {
            return R.error("该ID已存在，请重新输入");
        }
        Tag exist = this.getOne(new LambdaQueryWrapper<Tag>().eq(Tag::getName, tag.getName()));
        if (exist != null) {
            return R.error("该标签名称已存在");
        }
        return this.save(tag) ? R.success() : R.error("添加失败");
    }

    /**
     * 更新标签，名称唯一性校验时排除自身
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R updateTag(Tag tag) {
        Tag exist = this.getOne(new LambdaQueryWrapper<Tag>().eq(Tag::getName, tag.getName()));
        if (exist != null && !exist.getId().equals(tag.getId())) {
            return R.error("该标签名称已存在");
        }
        return this.updateById(tag) ? R.success() : R.error("更新失败");
    }

    /**
     * 删除标签（预留：可在此处增加"有文章引用则禁止删除"的判断）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R deleteTag(Long id) {
        return this.removeById(id) ? R.success() : R.error("删除失败");
    }

    /**
     * 查询指定文章的所有标签（通过中间表 t_blog_tag）
     */
    @Override
    public List<Tag> getTagsByBlogId(Long blogId) {
        // 1. 查中间表获取 tag_id 列表
        LambdaQueryWrapper<BlogTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogTag::getBlogId, blogId);
        List<BlogTag> blogTags = blogTagMapper.selectList(wrapper);

        if (blogTags.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 提取 tag_id，批量查询标签详情
        List<Long> tagIds = blogTags.stream()
                .map(BlogTag::getTagId)
                .collect(Collectors.toList());

        return baseMapper.selectBatchIds(tagIds);
    }

    /**
     * 批量获取多篇文章的标签映射，一次查询避免 N+1
     *
     * @param blogIds 文章ID列表
     * @return Map<blogId, 标签列表>
     */
    @Override
    public Map<Long, List<Tag>> getTagsByBlogIds(List<Long> blogIds) {
        if (blogIds == null || blogIds.isEmpty()) return new HashMap<>();

        // 1. 批量查询中间表关联关系
        List<BlogTag> allRelations = blogTagMapper.selectList(
                new LambdaQueryWrapper<BlogTag>().in(BlogTag::getBlogId, blogIds)
        );
        if (allRelations.isEmpty()) return new HashMap<>();

        // 2. 提取所有 tagId 并批量查询标签详情
        Set<Long> tagIds = allRelations.stream().map(BlogTag::getTagId).collect(Collectors.toSet());
        Map<Long, Tag> tagMap = this.selectBatchIds(new ArrayList<>(tagIds)).stream()
                .collect(Collectors.toMap(Tag::getId, tag -> tag));

        // 3. 按 blogId 分组，Collectors.mapping 直接映射为 Tag 对象
        return allRelations.stream().collect(Collectors.groupingBy(
                BlogTag::getBlogId,
                Collectors.mapping(rel -> tagMap.get(rel.getTagId()), Collectors.toList())
        ));
    }

    private List<Tag> selectBatchIds(List<Long> ids) {
        if (ids.isEmpty()) return new ArrayList<>();
        return baseMapper.selectBatchIds(ids);
    }

    /**
     * 前台标签云：统计每个标签下已发布文章的数量
     *
     * 使用 inSql 子查询过滤未发布文章，在内存中分组统计避免多次 DB 查询。
     */
    @Override
    public List<Map<String, Object>> listNameAndCount() {
        // 1. 获取所有标签
        List<Tag> tags = this.list();

        // 2. 只统计已发布文章的标签关联
        LambdaQueryWrapper<BlogTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.inSql(BlogTag::getBlogId, "select id from t_blog where published = 1");
        List<BlogTag> relationList = blogTagMapper.selectList(wrapper);

        // 3. 内存分组统计：TagId → 出现次数
        Map<Long, Long> countMap = relationList.stream()
                .collect(Collectors.groupingBy(BlogTag::getTagId, Collectors.counting()));

        // 4. 组装标签名称 + 文章数量，无文章的标签 count=0
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tag tag : tags) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", tag.getId());
            map.put("name", tag.getName());
            // getOrDefault 处理无关联文章的标签
            map.put("count", countMap.getOrDefault(tag.getId(), 0L));
            result.add(map);
        }

        return result;
    }
}