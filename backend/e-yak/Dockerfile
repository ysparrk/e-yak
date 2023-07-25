FROM openjdk:17-jdk
LABEL maintainer="email"
ARG JAR_FILE=build/libs/core-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} docker-springboot.jar
EXPOSE 8081
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/docker-springboot.jar"]