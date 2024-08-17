FROM openjdk:22-ea-oraclelinux8
LABEL authors="daniilmozzhukhin"
COPY target/user-service-0.0.1-SNAPSHOT.jar user-service.jar

ENTRYPOINT ["java", "-jar", "user-service.jar"]