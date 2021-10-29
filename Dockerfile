FROM openjdk:8-jdk-alpine
ADD  build/libs/authorizerapp-0.0.1-SNAPSHOT.jar /app/lib/authorizerapp.jar
ADD  batchfiles/operations.txt /app/lib/operations.txt
WORKDIR /app/lib
ENTRYPOINT ["java","-jar","authorizerapp.jar","/app/lib/operations.txt"]
