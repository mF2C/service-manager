FROM azul/zulu-openjdk:11.0.2
ENV CIMI_URL=http://cimi:8201/api
ENV LM_URL=http://lm-um:46000
ENV ALGORITHM=drl
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
CMD exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar --cimi.url=${CIMI_URL} --lm.url=${LM_URL} --algorithm=${ALGORITHM}