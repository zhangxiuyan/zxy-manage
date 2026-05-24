# 使用 OpenJDK 8 Alpine 镜像（最小化运行时）
FROM openjdk:8-jre-alpine

MAINTAINER zhangxiuyan

# 设置时区
ENV TZ=Asia/Shanghai
RUN apk --no-cache add tzdata && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 应用端口
EXPOSE 10010

# 工作目录
WORKDIR /opt/software

# 复制 JAR 文件
COPY target/zxy-manage.jar app.jar

# JVM 参数优化
ENV JAVA_OPTS="-Xmx512m -Xms512m -Djava.security.egd=file:/dev/./urandom"

# 启动命令（保持容器运行）
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
