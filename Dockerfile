# 使用 GraalVM 17 作为基础镜像
FROM ghcr.io/graalvm/graalvm-ce:latest

# 设置容器内的工作目录
WORKDIR /app

# 将可执行的jar文件复制到容器内
COPY target/cool-admin-8.0.0.jar /app/cool-admin-8.0.0.jar

# 暴露Spring Boot应用程序运行的端口
EXPOSE 8001

# 运行Spring Boot应用程序的命令
ENTRYPOINT ["java", "-jar", "/app/cool-admin-8.0.0.jar", "--spring.profiles.active=prod"]
