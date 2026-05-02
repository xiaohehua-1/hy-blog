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
    // 在 TagServiceImpl 中注入 BlogTagMapper
    @Autowired
    private BlogTagMapper blogTagMapper;

    @Override
    public List<Tag> getTagsByBlogId(Long blogId) {
        // 1. 先查中间表，找出关联的 tag_id
        LambdaQueryWrapper<BlogTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogTag::getBlogId, blogId);
        List<BlogTag> blogTags = blogTagMapper.selectList(wrapper);

        if (blogTags.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 提取 tag_id 列表
        List<Long> tagIds = blogTags.stream()
                .map(BlogTag::getTagId)
                .collect(Collectors.toList());

        // 3. 查 tag 表
        return baseMapper.selectBatchIds(tagIds);
    }

    @Override
    public Map<Long, List<Tag>> getTagsByBlogIds(List<Long> blogIds) {
        if (blogIds == null || blogIds.isEmpty()) return new HashMap<>();

        // 1. 批量查询关联关系
        List<BlogTag> allRelations = blogTagMapper.selectList(
                new LambdaQueryWrapper<BlogTag>().in(BlogTag::getBlogId, blogIds)
        );
        if (allRelations.isEmpty()) return new HashMap<>();

        // 2. 提取所有涉及到的 TagId
        Set<Long> tagIds = allRelations.stream().map(BlogTag::getTagId).collect(Collectors.toSet());
        
        // 3. 批量查询 Tag 详情并建立 ID 映射
        Map<Long, Tag> tagMap = this.selectBatchIds(new ArrayList<>(tagIds)).stream()
                .collect(Collectors.toMap(Tag::getId, tag -> tag));

        // 4. 按 BlogId 分组
        return allRelations.stream().collect(Collectors.groupingBy(
                BlogTag::getBlogId,
                Collectors.mapping(rel -> tagMap.get(rel.getTagId()), Collectors.toList())
        ));
    }

    private List<Tag> selectBatchIds(List<Long> ids) {
        if (ids.isEmpty()) return new ArrayList<>();
        return baseMapper.selectBatchIds(ids);
    }

    @Override
    public List<Map<String, Object>> listNameAndCount() {
        // 1. 获取所有标签
        List<Tag> tags = this.list();

        // 2. 获取所有“已发布文章”的标签关联关系
        // 等价 SQL: SELECT * FROM t_blog_tag WHERE blog_id IN (SELECT id FROM t_blog WHERE published = 1)
        LambdaQueryWrapper<BlogTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.inSql(BlogTag::getBlogId, "select id from t_blog where published = 1");
        List<BlogTag> relationList = blogTagMapper.selectList(wrapper);

        // 3. 在内存中分组统计 (TagId -> Count)
        Map<Long, Long> countMap = relationList.stream()
                .collect(Collectors.groupingBy(BlogTag::getTagId, Collectors.counting()));

        // 4. 组装结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tag tag : tags) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", tag.getId());
            map.put("name", tag.getName());
            // 获取数量，如果为 null 则为 0
            map.put("count", countMap.getOrDefault(tag.getId(), 0L));
            result.add(map);
        }

        return result;
    }
}