package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.BlackList;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.BlackListService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台黑名单管理控制层 (Controller)
 * 作用：专门用来管理系统的小黑屋。如果博客上线后遇到有人一直恶意刷评论、刷访问量，
 * 就可以通过这个接口把他的 IP 加进黑名单，禁止他访问系统。
 */
@RestController
@RequestMapping("/admin/blacklist")
public class AdminBlackListController {

    @Autowired private BlackListService blackListService;

    /**
     * 获取黑名单列表（带分页）
     * 说明：这里用 defaultValue 给页码和每页条数塞了默认值，主要是为了防错，万一前端粗心漏传了参数，后端也不至于报空指针异常。
     * 查询时用 orderByDesc 按照创建时间倒序排了一下，这样最新被拉黑的 IP 就会显示在表格第一行，方便管理员查看。
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<BlackList> page = new Page<>(pageNum, pageSize);
        return R.success().data("page", blackListService.page(page, new LambdaQueryWrapper<BlackList>().orderByDesc(BlackList::getCreateTime)));
    }

    /**
     * 新增黑名单 (把捣乱的 IP 关起来)
     * 注意：头上这个 @BlogLog 注解，就是配合我们前面写的那个 LogAspect 切面用的。
     * 只要加了这行，管理员点击“添加”的时候，切面就会自动把“谁干的、干了啥”偷偷记录到系统操作日志表里，出了问题好扯皮。
     */
    @PostMapping("/save")
    @BlogLog("添加黑名单IP")
    public R save(@RequestBody BlackList blackList) {
        return blackListService.save(blackList) ? R.success().message("添加成功") : R.error("添加失败");
    }

    /**
     * 更新黑名单信息
     * 作用：有时候并不是想直接删掉黑名单记录，可能只是想给某个人临时解封一下（改个状态），或者改改拉黑的备注原因，就用这个接口。
     */
    @PutMapping("/update")
    @BlogLog("更新黑名单状态")
    public R update(@RequestBody BlackList blackList) {
        return blackListService.updateById(blackList) ? R.success().message("更新成功") : R.error("更新失败");
    }

    /**
     * 移除黑名单 (给 IP 解封)
     * 踩坑提醒：这里的 id 是直接拼在 URL 路径后面的（比如 /admin/blacklist/5 ），
     * 所以接收参数时一定要用 @PathVariable，千万不能习惯性地写成 @RequestBody，不然绝对接不到前端传来的 ID，还会报 400 错误。
     */
    @DeleteMapping("/{id}")
    @BlogLog("移除黑名单IP")
    public R delete(@PathVariable Long id) {
        return blackListService.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }
}