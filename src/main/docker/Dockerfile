FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=target/dependency
ARG MODE=staging
ARG ACTIVEPROFILE=-Dspring.profiles.active=${MODE}
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 5000
ENTRYPOINT ["java",${ACTIVEPROFILE},"-XX:+UnlockExperimentalVMOptions","-XX:+UseCGroupMemoryLimitForHeap","-cp","app:app/lib/*","com.github.mtesmct.rieau.api.Application"]