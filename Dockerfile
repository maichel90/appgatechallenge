FROM openjdk:15-jdk
COPY /target/appgate-challenge-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java","-jar","appgate-challenge-0.0.1-SNAPSHOT.jar"]
