FROM eclipse-temurin:17-jre-alpine

#创建目录
RUN mkdir /opt/app
# redis地址
ENV REDIS_HOST 127.0.0.1
# redis端口
ENV REDIS_PORT 6379
# redis密码
ENV REDIS_PASSWORD redis
# redis 库
ENV REDIS_DB 0
# mysql地址
ENV MYSQL_DB_HOST 127.0.0.1
# mysql端口
ENV MYSQL_DB_PORT 3306
# mysql数据库名称
ENV MYSQL_DB_NAME demo
# mysql用户名
ENV MYSQL_USER_NAME root
# mysql密码
ENV MYSQL_PASSWORD test123

# 应用端口
EXPOSE 8090
# 健康检查
EXPOSE 6080

# 执行jar
COPY backend/target/dataease-cloud-management.jar /opt/app/dataease-cloud-management.jar

# 健康检查
HEALTHCHECK --interval=15s --timeout=5s --retries=20 --start-period=30s CMD curl -s 127.0.0.1:8090

# 执行
CMD ["java", "-jar", "/opt/app/dataease-cloud-management.jar"]
