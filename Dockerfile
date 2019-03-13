FROM azul/zulu-openjdk:11
ENV CIMI_URL=http://cimi:8201/api
ENV ALGORITHM=WST
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
CMD exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar --cimi.url=${CIMI_URL} --algorithm=${ALGORITHM}