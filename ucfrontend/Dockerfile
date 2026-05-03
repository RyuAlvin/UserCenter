FROM nginx:alpine

# 删除默认配置
RUN rm -rf /usr/share/nginx/dist/*

# 拷贝前端文件
COPY dist/ /usr/share/nginx/dist/

# 可选：自定义 nginx.conf
COPY /etc/nginx/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
