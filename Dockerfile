FROM azul/zulu-openjdk:11.0.2
RUN  apt-get update \
  && apt-get install -y wget
ENV CIMI_URL=http://cimi:8201/api
ENV LM_URL=http://lm-um:46000/api/v2/lm
ENV EM_URL=http://event-manager:8000
ENV ALGORITHM=drl
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
CMD exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar \
    --cimi.url=${CIMI_URL} \
    --lm.url=${LM_URL} \
    --em.url=${EM_URL} \
    --algorithm=${ALGORITHM}