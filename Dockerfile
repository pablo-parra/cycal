FROM eclipse-temurin:17-jdk

RUN apt-get update && \
    apt-get clean

WORKDIR /usr/local/sbin/

COPY ./build/libs/cycal.jar cycal.jar

CMD java ${JAVA_OPTS} \
        -Djava.security.egd=file:/dev/./urandom \
        -Dspring.profiles.active=remote \
        -jar cycal.jar