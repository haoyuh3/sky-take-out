 # 多阶段构建：第一阶段 - 构建应用
FROM maven:3.8.1-openjdk-8 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 文件，利用 Docker 层缓存
COPY pom.xml .
COPY sky-common/pom.xml sky-common/
COPY sky-pojo/pom.xml sky-pojo/
COPY sky-server/pom.xml sky-server/

# 下载依赖（这一层会被缓存，除非 pom.xml 改变）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY sky-common/ sky-common/
COPY sky-pojo/ sky-pojo/
COPY sky-server/ sky-server/

# 构建应用
RUN mvn clean package -DskipTests -B

# 第二阶段 - 运行时镜像
FROM openjdk:8-jre-slim

# 创建非root用户以提高安全性
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 设置工作目录
WORKDIR /app

# 从构建阶段复制jar包
COPY --from=builder /app/sky-server/target/sky-server-*.jar app.jar

# 修改文件所有者
RUN chown -R appuser:appuser /app

# 切换到非root用户
USER appuser

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom"

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]