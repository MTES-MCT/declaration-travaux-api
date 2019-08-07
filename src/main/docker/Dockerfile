FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENV SERVER_PORT=5000
EXPOSE ${SERVER_PORT}
COPY ./src/main/docker/entrypoint.sh /usr/local/bin/start
RUN chmod +x /usr/local/bin/start
RUN apk update && apk upgrade && apk add --no-cache bash
ENTRYPOINT [ "/bin/bash", "-c", "/usr/local/bin/start" ]