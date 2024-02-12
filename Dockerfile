FROM openjdk:11
EXPOSE 8080
ADD target/sid-cloud-sdk-0.0.1-SNAPSHOT.jar sid-cloud-sdk-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/sid-cloud-sdk-0.0.1-SNAPSHOT.jar"]
