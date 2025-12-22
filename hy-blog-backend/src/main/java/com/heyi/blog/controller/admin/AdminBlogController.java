package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heyi.blog.entity.Blog;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.entity.dto.BlogDTO;
import com.heyi.blog.entity.query.BlogQuery;
import com.heyi.blog.service.BlogService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/blog")
public class AdminBlogController {

    @Autowired
    private BlogService blogService;

    /**
     * 列表查询
     */
    @PostMapping("/list") // 使用POST方便传JSON查询对象
    public R list(@RequestBody BlogQuery query) {
        IPage<Blog> page = blogService.pageAdminBlogs(query);
        return R.success().data("page", page);
    }

    /**
     * 新增博客
     */
    @PostMapping("/save")
    @BlogLog("发布文章")
    public R save(@RequestBody BlogDTO blogDTO) {
        return blogService.saveBlog(blogDTO, blogDTO.getTagIds());
    }

    /**
     * 修改博客
     */
    @PutMapping("/update")
    @BlogLog("更新文章")
    public R update(@RequestBody BlogDTO blogDTO) {
        return blogService.updateBlog(blogDTO, blogDTO.getTagIds());
    }

    /**
     * 获取详情 (用于回显)
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable Long id) {
        return blogService.getBlogDetail(id);
    }

    /**
     * 删除博客
     */
    @DeleteMapping("/{id}")
    @BlogLog("删除文章")
    public R delete(@PathVariable Long id) {
        return blogService.deleteBlog(id);
    }
    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    @BlogLog("批量删除文章")
    public R deleteBatch(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.error("请选择要删除的文章");
        }
        return blogService.deleteBatch(ids);
    }

}