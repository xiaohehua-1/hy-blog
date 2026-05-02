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
 * 后台博客管理核心 Controller
 * 作用：整个后台最关键的模块！专门处理管理员写博客、改博客、删博客以及多条件查询博客的请求。
 */
@RestController
@RequestMapping("/admin/blog")
public class AdminBlogController {

    @Autowired
    private BlogService blogService;

    /**
     * 多条件分页查询博客列表
     * 【重点思考】：按理说查询应该用 @GetMapping，但我这里特意改成了 @PostMapping。
     * 因为博客的搜索条件太复杂了（比如按标题搜、按分类搜、按推荐状态搜），如果用 GET 把参数全拼在 URL 后面，
     * 不仅容易超长报错，前端拼接起来也很痛苦。所以干脆封装成 BlogQuery 对象，用 POST 发送 JSON 数据，稳妥又清晰。
     */
    @PostMapping("/list")
    public R list(@RequestBody BlogQuery query) {
        IPage<Blog> page = blogService.pageAdminBlogs(query);
        return R.success().data("page", page);
    }

    /**
     * 新增（发布）博客
     * 【避坑点】：这里的参数用的是 BlogDTO，而不是数据库实体类 Blog。
     * 为什么？因为发布博客时，前端传过来的不只有文章的标题和正文，还有一个数组格式的“标签ID列表(tagIds)”。
     * 单个 Blog 实体类接不住这堆复杂数据，所以专门建了个 DTO (Data Transfer Object) 把它们打包接进来。
     */
    @PostMapping("/save")
    @BlogLog("发布文章")
    public R save(@RequestBody BlogDTO blogDTO) {
        // 调用 Service 层去处理复杂的保存逻辑（既要存文章，还要存文章和标签的关联关系）
        return blogService.saveBlog(blogDTO, blogDTO.getTagIds());
    }

    /**
     * 修改博客
     * 逻辑和保存类似，前端把改好的数据连同重新选择的标签一起通过 DTO 传过来。
     */
    @PutMapping("/update")
    @BlogLog("更新文章")
    public R update(@RequestBody BlogDTO blogDTO) {
        return blogService.updateBlog(blogDTO, blogDTO.getTagIds());
    }

    /**
     * 根据 ID 获取博客详情
     * 作用：主要是给前端的“编辑文章”页面用的。管理员点“编辑”按钮时，
     * 前端要先调用这个接口，把原来的老数据（包括标题、正文、原来选了啥标签）查出来填到表单里，也就是“数据回显”。
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable Long id) {
        return blogService.getBlogDetail(id);
    }

    /**
     * 删除单篇博客
     * 这里的 @PathVariable 是配合 URL 里的 {id} 使用的，直接从路径里抠出要删的博客 ID。
     */
    @DeleteMapping("/{id}")
    @BlogLog("删除文章")
    public R delete(@PathVariable Long id) {
        return blogService.deleteBlog(id);
    }

    /**
     * 批量删除博客
     * 作用：前端勾选多篇文章后，把它们的 ID 组成一个数组传过来（List<Long>）。
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