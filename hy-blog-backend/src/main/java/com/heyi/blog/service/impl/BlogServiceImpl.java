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
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired private BlogTagMapper blogTagMapper;
    @Autowired private TagMapper tagMapper;
    @Autowired private TypeMapper typeMapper;
    @Autowired private BlogMapper blogMapper;
    @Autowired private BlogConvertMapper blogConvertMapper;

    @Override
    public IPage<Blog> pageAdminBlogs(BlogQuery query) {
        Page<Blog> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();

        // 1. 标题模糊查询
        wrapper.like(StringUtils.hasText(query.getTitle()), Blog::getTitle, query.getTitle());
        // 2. 分类精准查询
        wrapper.eq(query.getTypeId() != null, Blog::getTypeId, query.getTypeId());
        // 3. 推荐精准查询 (补回)
        wrapper.eq(query.getRecommend() != null, Blog::getRecommend, query.getRecommend());
        // 4. 发布状态查询
        wrapper.eq(query.getPublished() != null, Blog::getPublished, query.getPublished());
        // 5. 版权类型查询 (补回)
        wrapper.eq(query.getCopyright() != null, Blog::getCopyright, query.getCopyright());

        // 6. 标签关联查询 (子查询)
        if (query.getTagId() != null) {
            wrapper.apply("exists (select 1 from t_blog_tag bt where bt.blog_id = t_blog.id and bt.tag_id = {0})", query.getTagId());
        }

        // 7. 排序：原项目是按 updateTime 倒序
        wrapper.orderByDesc(Blog::getUpdateTime);

        Page<Blog> blogPage = this.page(page, wrapper);

        // 填充分类和标签名称 (优化：批量填充)
        if (!blogPage.getRecords().isEmpty()) {
            batchFillBlogDetails(blogPage.getRecords());
        }
        return blogPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R saveBlog(BlogDTO blogDTO, List<Long> tagIds) {
        Blog blog = blogConvertMapper.toBlog(blogDTO);
        // 初始化统计数据
        if(blog.getViews() == null) blog.setViews(0);
        if(blog.getCommentCount() == null) blog.setCommentCount(0);

        this.save(blog);
        saveBlogTags(blog.getId(), tagIds);
        return R.success().message("发布成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R updateBlog(BlogDTO blogDTO, List<Long> tagIds) {
        Blog blog = blogConvertMapper.toBlog(blogDTO);

        this.updateById(blog);

        // 重置标签：先删后加
        blogTagMapper.delete(new LambdaQueryWrapper<BlogTag>().eq(BlogTag::getBlogId, blog.getId()));
        saveBlogTags(blog.getId(), tagIds);
        return R.success().message("更新成功");
    }

    @Override
    public R getBlogDetail(Long id) {
        Blog blog = this.getById(id);
        if(blog == null) return R.error("文章不存在");

        List<BlogTag> blogTags = blogTagMapper.selectList(new LambdaQueryWrapper<BlogTag>().eq(BlogTag::getBlogId, id));
        List<Long> tagIds = blogTags.stream().map(BlogTag::getTagId).collect(Collectors.toList());

        return R.success().data("blog", blog).data("tagIds", tagIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R deleteBlog(Long id) {
        blogTagMapper.delete(new LambdaQueryWrapper<BlogTag>().eq(BlogTag::getBlogId, id));
        this.removeById(id);
        return R.success().message("删除成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R deleteBatch(List<Long> ids) {
        blogTagMapper.delete(new LambdaQueryWrapper<BlogTag>().in(BlogTag::getBlogId, ids));
        this.removeBatchByIds(ids);
        return R.success().message("批量删除成功");
    }

    @Override
    public Long getRandomBlogId() {
        // 【修改点】：这里不能用 BlogMapper(大写)，要用 this.blogMapper(小写，即注入的那个变量)
        // 你的 ServiceImpl 头部应该已经 @Autowired private BlogMapper blogMapper; 了
        return blogMapper.getRandomBlogId();
    }

    // --- 私有辅助方法 ---

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
     * 批量填充博客的分类和标签，解决 N+1 问题
     */
    private void batchFillBlogDetails(List<Blog> blogs) {
        if (blogs == null || blogs.isEmpty()) return;

        // 1. 批量填充分类名称
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

        // 2. 批量填充标签列表
        List<Long> blogIds = blogs.stream().map(Blog::getId).collect(Collectors.toList());
        List<BlogTag> allBlogTags = blogTagMapper.selectList(
                new LambdaQueryWrapper<BlogTag>().in(BlogTag::getBlogId, blogIds)
        );
        if (!allBlogTags.isEmpty()) {
            // 获取所有涉及到的标签ID
            Set<Long> tagIds = allBlogTags.stream().map(BlogTag::getTagId).collect(Collectors.toSet());
            Map<Long, Tag> tagMap = tagMapper.selectBatchIds(tagIds).stream()
                    .collect(Collectors.toMap(Tag::getId, tag -> tag));

            // 按 blogId 分组标签
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