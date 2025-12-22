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

    // 读取配置文件中的路径
    @Value("${file.upload-path}")
    private String uploadPath;

    @PostMapping("/file")
    public R upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("上传文件不能为空");
        }

        try {
            // 1. 准备文件夹 (按日期分类，避免一个文件夹下文件太多)
            String dateDir = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            String realPath = uploadPath + dateDir;

            File dir = new File(realPath);
            if (!dir.exists()) {
                dir.mkdirs(); // 自动创建 D:/blog-data/upload/2023/12/09
            }

            // 2. 生成新文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 3. 保存文件到磁盘
            file.transferTo(new File(dir, newFileName));

            // 4. 返回访问 URL
            // 格式：/upload/2023/12/09/xxx.mp3
            String url = "/upload/" + dateDir + "/" + newFileName;

            return R.success().data("url", url).data("fileName", originalFilename);

        } catch (IOException e) {
            e.printStackTrace();
            return R.error("上传失败: " + e.getMessage());
        }
    }
}