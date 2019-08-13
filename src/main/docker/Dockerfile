FROM adoptopenjdk/openjdk8:slim
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENV SERVER_PORT=5000
EXPOSE ${SERVER_PORT}
ENV SERVER_CERT=/app/server.cer
COPY ./src/main/docker/entrypoint.sh /usr/local/bin/start
RUN chmod +x /usr/local/bin/start
ENTRYPOINT [ "/bin/bash", "-c", "/usr/local/bin/start" ]