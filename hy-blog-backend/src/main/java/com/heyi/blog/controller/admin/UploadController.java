package com.heyi.blog.controller.admin;

import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/admin/upload")
public class UploadController {

    // 读取 yml 中的配置路径
    @Value("${file.upload-path}")
    private String uploadPath;

    @PostMapping("/file")
    public R upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("上传文件不能为空");
        }

        try {
            // === 调试日志开始 ===
            System.out.println("====== 开始处理文件上传 ======");
            System.out.println("1. 读取到的配置路径 (uploadPath): " + uploadPath);

            // 1. 生成日期目录 (例如 2025/12/23)
            String dateDir = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            System.out.println("2. 生成的日期子目录: " + dateDir);

            // 2. 智能拼接真实存储路径 (自动处理斜杠问题)
            String finalPath;
            if (uploadPath == null) {
                // 防御性编程：如果没读到配置，默认存到项目根目录下的 temp 文件夹
                finalPath = System.getProperty("user.dir") + File.separator + "temp" + File.separator + dateDir;
                System.err.println("!!! 警告：uploadPath 为 null，使用临时路径: " + finalPath);
            } else {
                // 去除末尾可能重复的斜杠，统一拼接
                String cleanPath = uploadPath.endsWith("/") || uploadPath.endsWith("\\")
                        ? uploadPath.substring(0, uploadPath.length() - 1)
                        : uploadPath;
                finalPath = cleanPath + File.separator + dateDir;
            }
            System.out.println("3. 最终计算的物理存储路径: " + finalPath);

            // 3. 创建目录
            File dir = new File(finalPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                System.out.println("4. 文件夹不存在，尝试创建... 结果: " + (created ? "成功" : "失败"));
            } else {
                System.out.println("4. 文件夹已存在");
            }

            // 4. 生成新文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 5. 保存文件
            File dest = new File(dir, newFileName);
            file.transferTo(dest);
            System.out.println("5. 文件保存成功! 绝对路径: " + dest.getAbsolutePath());
            System.out.println("====== 文件上传结束 ======");

            // 6. 返回前端访问 URL
            // 格式: /upload/2025/12/23/uuid.jpg
            // 这个前缀 /upload/ 对应前端 vite.config.js 的代理配置
            String url = "/upload/" + dateDir + "/" + newFileName;

            return R.success()
                    .data("url", url)
                    .data("fileName", originalFilename);

        } catch (IOException e) {
            e.printStackTrace();
            return R.error("上传失败: " + e.getMessage());
        }
    }
}