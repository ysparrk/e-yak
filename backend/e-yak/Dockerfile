FROM openjdk:17-jdk
LABEL maintainer="email"
ARG JAR_FILE=./backend/e-yak/build/libs/e-yak-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} docker-springboot.jar
EXPOSE 8081
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/docker-springboot.jar"]