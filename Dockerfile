FROM openjdk:8-jdk-alpine

ENV CIMI_API_KEY=""
ENV CIMI_API_SECRET=""
ENV JAVA_OPTS=""
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar --cimi.api.key=${CIMI_API_KEY} --cimi.api.secret=${CIMI_API_SECRET}