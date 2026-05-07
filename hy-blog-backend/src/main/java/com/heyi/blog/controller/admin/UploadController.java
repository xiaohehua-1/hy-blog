package com.heyi.blog.controller.admin;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 文件上传控制器
 * 处理后台各类文件上传，通过白名单限制可上传的文件类型，防止恶意文件上传
 * 文件按日期分目录存储（yyyy/MM/dd），文件名使用 UUID 避免冲突
 */
@RestController
@RequestMapping("/admin/upload")
public class UploadController {

    @Value("${file.upload-path}")
    private String uploadPath;

    // 白名单：仅允许常见图片、文档、压缩包、音视频格式
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md",
            "zip", "rar", "7z",
            "mp4", "avi", "mp3", "wav", "flac"
    );

    /**
     * 上传文件，进行空值校验、后缀白名单过滤后存储到日期目录
     */
    @PostMapping("/file")
    public R upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("上传文件不能为空");
        }

        // 1. 获取后缀名 (转小写)
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.extName(originalFilename).toLowerCase();

        // 2. 安全校验：白名单检查
        if (!ALLOWED_EXTENSIONS.contains(suffix)) {
            return R.error("不支持的文件格式: " + suffix);
        }

        // 3. 生成日期目录 (yyyy/MM/dd)
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 4. 生成新文件名 (UUID)
        String newFileName = IdUtil.simpleUUID() + "." + suffix;

        // 5. 组合最终物理路径
        // 注意：uploadPath 已经在 yml 里配置为 ${user.dir}/file/upload/
        File destFile = new File(uploadPath + File.separator + datePath, newFileName);

        // 6. 自动创建父目录 (Hutool 工具)
        FileUtil.touch(destFile);

        try {
            // 7. 保存文件
            file.transferTo(destFile);

            // 8. 返回 URL (对应 WebMvcConfig 的映射)
            // 比如返回： /upload/2026/01/21/xxx.jpg
            String fileUrl = "/upload/" + datePath + "/" + newFileName;
            return R.success().data("url", fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return R.error("文件上传失败: " + e.getMessage());
        }
    }
}