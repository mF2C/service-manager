FROM openjdk:8-jdk-alpine
ENV CIMI_URL=cimi:8902
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar --cimi.url=${CIMI_URL}