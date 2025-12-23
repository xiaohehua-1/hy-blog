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

    /**
     * 获取首页博客列表 (支持分页、分类筛选)
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer current,
                  @RequestParam(defaultValue = "10") Integer size,
                  @RequestParam(required = false) Long typeId) { // 新增 typeId 参数

        // 1. 获取站长名称
        SysConfig config = sysConfigService.getById(1);
        String authorName = (config != null && config.getAuthor() != null) ? config.getAuthor() : "HeYi";

        // 2. 构建查询条件
        Page<Blog> page = new Page<>(current, size);
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getPublished, true); // 只查已发布

        // 如果传了分类ID，就加筛选条件
        if (typeId != null) {
            wrapper.eq(Blog::getTypeId, typeId);
        }

        wrapper.orderByDesc(Blog::getRecommend) // 推荐的排前面
                .orderByDesc(Blog::getCreateTime); // 按时间倒序

        Page<Blog> blogPage = blogService.page(page, wrapper);
        List<Blog> records = blogPage.getRecords();

        // 3. 数据转换
        List<FrontBlogVO> voList = records.stream().map(blog -> {
            FrontBlogVO vo = new FrontBlogVO();
            BeanUtils.copyProperties(blog, vo);

            // 确保 createTime 被正确复制 (BeanUtils 会自动处理同名同类型字段)
            // 如果 vo.createTime 是 null，请检查数据库该字段是否有值

            vo.setAuthor(authorName);
            vo.setIsOriginal(blog.getCopyright() != null && blog.getCopyright() == 1);

            // 查询标签
            List<Tag> tags = tagService.getTagsByBlogId(blog.getId());
            vo.setTagList(tags);

            return vo;
        }).collect(Collectors.toList());

        // 4. 返回结果
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

        // 如果推荐的文章不足7篇，可以考虑补足最新的文章 (可选逻辑，这里先严格按推荐查)

        // 3. 转换为 VO
        List<FrontBlogVO> voList = list.stream().map(blog -> {
            FrontBlogVO vo = new FrontBlogVO();
            BeanUtils.copyProperties(blog, vo);
            vo.setAuthor(authorName);
            // 推荐区不需要查标签，为了性能可以跳过标签查询
            // vo.setTagList(...)
            return vo;
        }).collect(Collectors.toList());

        return R.ok().data("list", voList);
    }
}