# 1. 基础镜像：使用轻量级的 Java 17 环境
FROM openjdk:17-jdk-alpine

# 2. 作者信息
MAINTAINER heyi

# 3. 挂载目录 (临时文件存放点)
VOLUME /tmp

# 4. 暴露端口
EXPOSE 8080

# 5. 将打包好的 jar 包复制进容器，重命名为 app.jar
# 注意：你需要先执行 maven package 生成 jar 包
COPY target/*.jar app.jar

# 6. 启动命令
# 重点：这里我们使用 --file.upload-path 参数，强制覆盖 yml 里的 D:/... 路径
# 让容器内部使用 /app/uploads/ 作为上传目录
ENTRYPOINT ["java", "-jar", "/app.jar", "--file.upload-path=/app/uploads/"]