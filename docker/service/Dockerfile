# 使用官方Nginx Alpine镜像作为基础
FROM nginx:alpine

# 定义版本和时区参数
ARG VERSION
ARG TZ=Asia/Shanghai

USER root

# 使用环境变量来提高可维护性
ENV TZ ${TZ}


# 更新Alpine包列表和安装所需的软件包
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && \
    apk update && \
    apk add --no-cache tzdata openjdk8 && \
    # 设置时区
    ln -sf /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo ${TZ} > /etc/timezone && \
    # 删除原 Nginx 配置以避免冲突
    rm -rf /etc/nginx/conf.d/* && \
    # 清理缓存以减小镜像大小
    apk del tzdata && rm /var/cache/apk/*

# 注意：避免在镜像中使用特定版本的软件包 tar 文件，这会降低镜像的通用性和可维护性。
# 假设这个包是必需的，确保从一个安全、可靠的来源获取。
ADD devops-service-${VERSION}-launch-bin.tar.gz /opt/
# 设置工作目录
WORKDIR /opt/devops/

# 复制Nginx配置文件
RUN cp nginx.conf /etc/nginx/conf.d

# 验证是否存在entrypoint.sh并设置其为可执行，以避免潜在问题
RUN touch /entrypoint.sh && chmod +x /entrypoint.sh

# 使用root用户启动entrypoint.sh
ENTRYPOINT ["sh","entrypoint.sh"]
