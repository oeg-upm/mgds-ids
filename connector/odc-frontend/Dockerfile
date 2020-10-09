FROM nginx:alpine

EXPOSE 8080
ADD dist /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY runtimeconfig.sh /

RUN chmod +x /runtimeconfig.sh

CMD [ "/runtimeconfig.sh" ]