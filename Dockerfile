FROM openjdk:8-jdk-alpine
COPY ./target/auth-course-0.0.1-SNAPSHOT.jar auth-course-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "auth-course-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080