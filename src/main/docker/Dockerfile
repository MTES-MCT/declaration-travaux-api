FROM openjdk:8-jdk-alpine
RUN apk update && apk upgrade
VOLUME /tmp
ARG DEPENDENCY=target/dependency
ARG MODE=staging
ARG DB_URL=jdbc:postgresql://localhost:5432/rieau
ARG DB_USER=rieau
ARG DB_PASSWORD=rieau
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 5000
ENTRYPOINT ["java","-XX:+UnlockExperimentalVMOptions","-XX:+UseCGroupMemoryLimitForHeap","-Dspring.profiles.active=${MODE}","-Dspring.datasource.url=${DB_URL}","-Dspring.datasource.username=${DB_USER}","-Dspring.datasource.password=${DB_PASSWORD}","-cp","app:app/lib/*","com.github.mtesmct.rieau.api.Application"]