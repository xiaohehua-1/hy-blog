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

/**
 * 后台博客管理控制器
 * 负责文章的多条件分页查询、发布、编辑、删除及批量删除
 */
@RestController
@RequestMapping("/admin/blog")
public class AdminBlogController {

    @Autowired
    private BlogService blogService;

    /**
     * 多条件分页查询博客列表
     * 使用 @PostMapping 而非 @GetMapping：查询条件复杂（标题/分类/推荐/标签等），
     * GET 拼参数易超长，POST JSON 传参更可靠
     */
    @PostMapping("/list")
    public R list(@RequestBody BlogQuery query) {
        IPage<Blog> page = blogService.pageAdminBlogs(query);
        return R.success().data("page", page);
    }

    /**
     * 发布新文章
     * 参数使用 BlogDTO（继承 Blog 并扩展 tagIds 数组），一次接收文章正文和所选标签
     */
    @PostMapping("/save")
    @BlogLog("发布文章")
    public R save(@RequestBody BlogDTO blogDTO) {
        // 调用 Service 层去处理复杂的保存逻辑（既要存文章，还要存文章和标签的关联关系）
        return blogService.saveBlog(blogDTO, blogDTO.getTagIds());
    }

    /**
     * 更新文章（含标签关联），参数与新增一致
     */
    @PutMapping("/update")
    @BlogLog("更新文章")
    public R update(@RequestBody BlogDTO blogDTO) {
        return blogService.updateBlog(blogDTO, blogDTO.getTagIds());
    }

    /**
     * 获取文章详情（含标签列表），用于编辑页数据回显
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable Long id) {
        return blogService.getBlogDetail(id);
    }

    /**
     * 删除单篇文章
     */
    @DeleteMapping("/{id}")
    @BlogLog("删除文章")
    public R delete(@PathVariable Long id) {
        return blogService.deleteBlog(id);
    }

    /**
     * 批量删除文章，空列表保护防止执行无意义的 SQL
     */
    @DeleteMapping("/delete/batch")
    @BlogLog("批量删除文章")
    public R deleteBatch(@RequestBody List<Long> ids) {
        // 健壮性保护：防一手前端抽风，万一啥也没勾选就发了请求，直接拦截返回错误，防止到底层执行空的 SQL 报错
        if (ids == null || ids.isEmpty()) {
            return R.error("请选择要删除的文章");
        }
        return blogService.deleteBatch(ids);
    }

}