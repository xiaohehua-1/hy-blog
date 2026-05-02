package com.heyi.blog.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.Blog;
import com.heyi.blog.entity.SysConfig;
import com.heyi.blog.entity.Tag;
import com.heyi.blog.entity.vo.FrontBlogVO;
import com.heyi.blog.service.BlogService;
import com.heyi.blog.service.SysConfigService;
import com.heyi.blog.service.TagService;
import com.heyi.blog.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/front/blog")
public class FrontBlogController {

    @Autowired
    private BlogService blogService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private TagService tagService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    // Redis Key 常量
    private static final String KEY_RECOMMEND_LIST = "blog:front:recommend";
    private static final String KEY_TAGS_MAP_PREFIX = "blog:tags:map:";

    /**
     * 获取首页博客列表 (支持分页、分类筛选、多标签筛选、关键词搜索)
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer current,
                  @RequestParam(defaultValue = "10") Integer size,
                  @RequestParam(required = false) Integer typeId,
                  @RequestParam(required = false) String tagIds,
                  @RequestParam(required = false) String keyword) {

        // 1. 分页构造
        Page<Blog> page = new Page<>(current, size);

        // 2. 查询条件
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getPublished, true); // 只查已发布的

        if (typeId != null) {
            wrapper.eq(Blog::getTypeId, typeId); // 根据分类筛选
        }

        // === 多标签筛选逻辑 ===
        if (tagIds != null && !tagIds.trim().isEmpty()) {
            // 使用正则剔除非法字符，防止 SQL 注入
            String safeTagIds = tagIds.replaceAll("[^0-9,]", "");
            if (!safeTagIds.isEmpty()) {
                wrapper.inSql(Blog::getId, "select blog_id from t_blog_tag where tag_id in (" + safeTagIds + ")");
            }
        }
        wrapper.orderByDesc(Blog::getCreateTime);

        // === 关键词搜索 ===
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Blog::getTitle, keyword.trim())
                    .or()
                    .like(Blog::getDescription, keyword.trim()));
        }

        // 3. 执行查询
        Page<Blog> blogPage = blogService.page(page, wrapper);

        // 4. 转换为 VO (优化：批量获取标签，解决 N+1 问题)
        List<Blog> blogs = blogPage.getRecords();
        if (blogs.isEmpty()) {
            return R.ok().data("records", Collections.emptyList()).data("total", 0);
        }

        // 批量获取这些文章的所有标签
        List<Long> blogIds = blogs.stream().map(Blog::getId).collect(Collectors.toList());
        Map<Long, List<Tag>> blogTagsMap = tagService.getTagsByBlogIds(blogIds);

        List<FrontBlogVO> voList = blogs.stream().map(blog -> {
            FrontBlogVO vo = new FrontBlogVO();
            BeanUtils.copyProperties(blog, vo);
            vo.setTagList(blogTagsMap.getOrDefault(blog.getId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());

        // 5. 返回结果
        return R.ok()
                .data("records", voList)
                .data("total", blogPage.getTotal())
                .data("current", blogPage.getCurrent())
                .data("size", blogPage.getSize());
    }

    /**
     * 获取首页推荐文章 (最多7篇，带 Redis 缓存)
     */
    @GetMapping("/recommend")
    public R getRecommendList() {
        // 1. 尝试从缓存获取
        String cacheJson = redisTemplate.opsForValue().get(KEY_RECOMMEND_LIST);
        if (StringUtils.hasText(cacheJson)) {
            try {
                List<FrontBlogVO> cachedList = objectMapper.readValue(cacheJson, new TypeReference<List<FrontBlogVO>>() {});
                return R.ok().data("list", cachedList);
            } catch (JsonProcessingException e) {
                redisTemplate.delete(KEY_RECOMMEND_LIST); // 缓存解析失败，清理掉
            }
        }

        // 2. 缓存不存在，查询数据库
        // 2.1 获取站长名称
        SysConfig config = sysConfigService.getById(1);
        String authorName = (config != null && config.getAuthor() != null) ? config.getAuthor() : "HeYi";

        // 2.2 查询推荐文章
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getPublished, true)
                .eq(Blog::getRecommend, true)
                .orderByDesc(Blog::getCreateTime)
                .last("limit 7");

        List<Blog> list = blogService.list(wrapper);

        // 3. 转换为 VO
        List<FrontBlogVO> voList = list.stream().map(blog -> {
            FrontBlogVO vo = new FrontBlogVO();
            BeanUtils.copyProperties(blog, vo);
            vo.setAuthor(authorName);
            return vo;
        }).collect(Collectors.toList());

        // 4. 写入缓存 (有效期 1 小时)
        try {
            redisTemplate.opsForValue().set(KEY_RECOMMEND_LIST, objectMapper.writeValueAsString(voList), 1, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {}

        return R.ok().data("list", voList);
    }

    @GetMapping("/random")
    // 【删除】去掉这行 @Operation 注解，因为它会导致报错
    // @Operation(summary = "随机获取一篇文章ID")
    public R getRandomBlog() { // 【注意】这里返回值改为 R，不要写 R<Long>
        Long blogId = blogService.getRandomBlogId();
        if (blogId == null) {
            return R.error("暂无文章");
        }
        // 使用 .put 放入数据（假设你的 R 继承自 HashMap）
        return R.ok().data("id", blogId);
    }

    /**
     * 获取博文详情 (Redis 浏览量统计 +1)
     */
    @GetMapping("/detail/{id}")
    public R getDetail(@PathVariable Long id) {
        // 1. 查询数据库
        Blog blog = blogService.getById(id);

        // 2. 校验文章是否存在或已发布
        if (blog == null || !blog.getPublished()) {
            return R.error("文章不存在或未发布");
        }

        // 3. Redis 浏览量处理
        String viewKey = "blog:view:article:" + id;

        // 3.1 如果 Redis 里没有这个 key，先把数据库的浏览量 set 进去
        if (Boolean.FALSE.equals(redisTemplate.hasKey(viewKey))) {
            redisTemplate.opsForValue().set(viewKey, String.valueOf(blog.getViews()));
        }

        // 3.2 Redis 原子 +1
        Long newViews = redisTemplate.opsForValue().increment(viewKey);

        // === 修正点3：解决 Long 无法转 Integer 的报错 ===
        if (newViews != null) {
            blog.setViews(newViews.intValue());
        }

        // 3.4 同步更新回数据库
        blogService.updateById(blog);

        // 4. 获取文章标签 (同样使用 getTagsByBlogId)
        List<Tag> tags = tagService.getTagsByBlogId(id);

        // 5. 返回完整数据
        return R.success()
                .data("data", blog) // 返回详情
                .data("tags", tags); // 顺便把标签也返回去
    }
}