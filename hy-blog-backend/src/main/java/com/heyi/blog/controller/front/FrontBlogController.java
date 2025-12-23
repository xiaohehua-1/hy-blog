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

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 获取首页博客列表 (支持分页、分类筛选)
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer current,
                  @RequestParam(defaultValue = "10") Integer size,
                  @RequestParam(required = false) Integer typeId) {

        // 1. 分页构造
        Page<Blog> page = new Page<>(current, size);

        // 2. 查询条件
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getPublished, true); // 只查已发布的

        if (typeId != null) {
            wrapper.eq(Blog::getTypeId, typeId); // 根据分类筛选
        }

        // === 修正点1：移除了 getTop() 排序，只保留时间倒序 ===
        wrapper.orderByDesc(Blog::getCreateTime);

        // 3. 执行查询
        Page<Blog> blogPage = blogService.page(page, wrapper);

        // 4. 转换为 VO (携带标签信息)
        List<FrontBlogVO> voList = blogPage.getRecords().stream().map(blog -> {
            FrontBlogVO vo = new FrontBlogVO();
            BeanUtils.copyProperties(blog, vo);

            // === 修正点2：使用你已有的方法 getTagsByBlogId ===
            List<Tag> tags = tagService.getTagsByBlogId(blog.getId());
            vo.setTagList(tags);

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
     * 获取首页推荐文章 (最多7篇)
     */
    @GetMapping("/recommend")
    public R getRecommendList() {
        // 1. 获取站长名称
        SysConfig config = sysConfigService.getById(1);
        String authorName = (config != null && config.getAuthor() != null) ? config.getAuthor() : "HeYi";

        // 2. 查询推荐文章
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getPublished, true)  // 已发布
                .eq(Blog::getRecommend, true)  // 是推荐
                .orderByDesc(Blog::getCreateTime) // 按时间倒序
                .last("limit 7"); // 只取7篇

        List<Blog> list = blogService.list(wrapper);

        // 3. 转换为 VO
        List<FrontBlogVO> voList = list.stream().map(blog -> {
            FrontBlogVO vo = new FrontBlogVO();
            BeanUtils.copyProperties(blog, vo);
            vo.setAuthor(authorName); // 设置统一作者名
            return vo;
        }).collect(Collectors.toList());

        return R.ok().data("list", voList);
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