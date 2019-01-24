FROM azul/zulu-openjdk:8
ENV CIMI_URL=http://cimi:8201/api
VOLUME /tmp
#COPY target/classes/use-cases.json /tmp/
#ENV LOCAL_SERVICES="/tmp/use-cases.json"
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
#CMD exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar --cimi.url=${CIMI_URL} --local.services=${LOCAL_SERVICES}
CMD exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar --cimi.url=${CIMI_URL}