package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Blog;
import com.heyi.blog.entity.BlogTag;
import com.heyi.blog.entity.Tag;
import com.heyi.blog.entity.Type;
import com.heyi.blog.entity.dto.BlogDTO;
import com.heyi.blog.entity.query.BlogQuery;
import com.heyi.blog.mapper.mybatis.BlogMapper;
import com.heyi.blog.mapper.mybatis.BlogTagMapper;
import com.heyi.blog.mapper.mybatis.TagMapper;
import com.heyi.blog.mapper.mybatis.TypeMapper;
import com.heyi.blog.service.BlogService;
import com.heyi.blog.utils.R;
import com.heyi.blog.mapper.BlogConvertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 博客文章业务实现类
 *
 * 核心职责：文章增删改查 + 标签关联维护 + 分类/标签批量填充。
 * 所有写操作使用 @Transactional 保证一致性。
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired private BlogTagMapper blogTagMapper;
    @Autowired private TagMapper tagMapper;
    @Autowired private TypeMapper typeMapper;
    @Autowired private BlogMapper blogMapper;
    @Autowired private BlogConvertMapper blogConvertMapper;

    /**
     * 后台多条件分页查询，支持标题/分类/推荐/发布状态/版权/标签筛选
     */
    @Override
    public IPage<Blog> pageAdminBlogs(BlogQuery query) {
        Page<Blog> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();

        // 条件动态拼接：仅参数非空时才生效，避免无效 WHERE 子句
        wrapper.like(StringUtils.hasText(query.getTitle()), Blog::getTitle, query.getTitle());
        wrapper.eq(query.getTypeId() != null, Blog::getTypeId, query.getTypeId());
        wrapper.eq(query.getRecommend() != null, Blog::getRecommend, query.getRecommend());
        wrapper.eq(query.getPublished() != null, Blog::getPublished, query.getPublished());
        wrapper.eq(query.getCopyright() != null, Blog::getCopyright, query.getCopyright());

        // 标签关联子查询：exists 方式比多表 JOIN 更简洁，避免结果集膨胀
        if (query.getTagId() != null) {
            wrapper.apply("exists (select 1 from t_blog_tag bt where bt.blog_id = t_blog.id and bt.tag_id = {0})", query.getTagId());
        }

        // 按更新时间倒序，最新修改的排在前面
        wrapper.orderByDesc(Blog::getUpdateTime);

        Page<Blog> blogPage = this.page(page, wrapper);

        // 一次批量填充分类名和标签列表，避免每条记录单独查询（N+1 问题）
        if (!blogPage.getRecords().isEmpty()) {
            batchFillBlogDetails(blogPage.getRecords());
        }
        return blogPage;
    }

    /**
     * 新增文章并绑定标签，初始化统计字段为0
     */
    // 开启事务安全保护
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R saveBlog(BlogDTO blogDTO, List<Long> tagIds) {
        Blog blog = blogConvertMapper.toBlog(blogDTO);
        // 避免 null 值导致数据库异常或前端展示问题
        if(blog.getViews() == null) blog.setViews(0);
        if(blog.getCommentCount() == null) blog.setCommentCount(0);

        this.save(blog);
        // 先保存文章获得ID，再写入标签关联
        saveBlogTags(blog.getId(), tagIds);
        return R.success().message("发布成功");
    }

    /**
     * 更新文章并重置标签：先清空旧关联，再写入新标签列表
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R updateBlog(BlogDTO blogDTO, List<Long> tagIds) {
        Blog blog = blogConvertMapper.toBlog(blogDTO);

        this.updateById(blog);

        // 先删后加策略：比增量比对更简单可靠，适合标签数据量小的场景
        blogTagMapper.delete(new LambdaQueryWrapper<BlogTag>().eq(BlogTag::getBlogId, blog.getId()));
        saveBlogTags(blog.getId(), tagIds);
        return R.success().message("更新成功");
    }

    /**
     * 获取文章详情及关联标签ID，用于后台编辑回显
     */
    @Override
    public R getBlogDetail(Long id) {
        Blog blog = this.getById(id);
        if(blog == null) return R.error("文章不存在");

        // 查询关联的标签ID列表，回显到编辑页的标签选择器
        List<BlogTag> blogTags = blogTagMapper.selectList(new LambdaQueryWrapper<BlogTag>().eq(BlogTag::getBlogId, id));
        List<Long> tagIds = blogTags.stream().map(BlogTag::getTagId).collect(Collectors.toList());

        return R.success().data("blog", blog).data("tagIds", tagIds);
    }

    /**
     * 删除文章及关联的标签记录
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R deleteBlog(Long id) {
        // 先删关联，再删主记录，避免外键或孤儿数据
        blogTagMapper.delete(new LambdaQueryWrapper<BlogTag>().eq(BlogTag::getBlogId, id));
        this.removeById(id);
        return R.success().message("删除成功");
    }

    /**
     * 批量删除文章及关联的标签记录
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R deleteBatch(List<Long> ids) {
        blogTagMapper.delete(new LambdaQueryWrapper<BlogTag>().in(BlogTag::getBlogId, ids));
        this.removeBatchByIds(ids);
        return R.success().message("批量删除成功");
    }

    @Override
    public Long getRandomBlogId() {
        return blogMapper.getRandomBlogId();
    }

    /**
     * 逐条写入文章-标签关联（标签数少时直接循环插入，无需批量优化）
     */
    private void saveBlogTags(Long blogId, List<Long> tagIds) {
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                BlogTag blogTag = new BlogTag();
                blogTag.setBlogId(blogId);
                blogTag.setTagId(tagId);
                blogTagMapper.insert(blogTag);
            }
        }
    }

    /**
     * 批量填充文章的分类名称和标签列表，解决 N+1 查询问题
     *
     * 两步批量查询替代逐条查询：先查所有分类 → 再查所有标签关联 → 内存分组映射。
     */
    private void batchFillBlogDetails(List<Blog> blogs) {
        if (blogs == null || blogs.isEmpty()) return;

        // 1. 收集所有 typeId，一次批量查询分类名称
        Set<Long> typeIds = blogs.stream()
                .map(Blog::getTypeId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (!typeIds.isEmpty()) {
            Map<Long, String> typeMap = typeMapper.selectBatchIds(typeIds).stream()
                    .collect(Collectors.toMap(Type::getId, Type::getName));
            blogs.forEach(blog -> {
                if (blog.getTypeId() != null) {
                    blog.setTypeName(typeMap.get(blog.getTypeId()));
                }
            });
        }

        // 2. 收集所有 blogId，一次查询标签关联 + 标签详情
        List<Long> blogIds = blogs.stream().map(Blog::getId).collect(Collectors.toList());
        List<BlogTag> allBlogTags = blogTagMapper.selectList(
                new LambdaQueryWrapper<BlogTag>().in(BlogTag::getBlogId, blogIds)
        );
        if (!allBlogTags.isEmpty()) {
            // 批量查询标签详情并建立 ID→Tag 映射
            Set<Long> tagIds = allBlogTags.stream().map(BlogTag::getTagId).collect(Collectors.toSet());
            Map<Long, Tag> tagMap = tagMapper.selectBatchIds(tagIds).stream()
                    .collect(Collectors.toMap(Tag::getId, tag -> tag));

            // 按 blogId 分组标签列表，getOrDefault 处理无标签的文章
            Map<Long, List<Tag>> blogTagsMap = allBlogTags.stream()
                    .collect(Collectors.groupingBy(
                            BlogTag::getBlogId,
                            Collectors.mapping(bt -> tagMap.get(bt.getTagId()), Collectors.toList())
                    ));

            blogs.forEach(blog -> {
                blog.setTagList(blogTagsMap.getOrDefault(blog.getId(), Collections.emptyList()));
            });
        }
    }
}