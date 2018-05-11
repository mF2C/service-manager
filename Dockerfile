FROM openjdk:8-jdk-alpine
ENV CIMI_URL=http://cimi:8201/api
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
CMD exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar --cimi.url=${CIMI_URL}