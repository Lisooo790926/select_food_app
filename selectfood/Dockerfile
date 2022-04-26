FROM maven:3.8.5-openjdk-11-slim as maven_build

VOLUME ~/.m2 /root/.m2
WORKDIR /app

COPY pom.xml .
RUN mvn verify --fail-never

ENV google.api.key=$google.api.key
ENV h2.password=$h2.password
ENV jwtsecrt.password=$jwtsecrt.password
ENV allowed.origin=$allowed.origin

COPY src ./src
RUN mvn package  -Dmaven.test.skip

FROM openjdk:11.0.15-jdk-slim-buster
COPY --from=maven_build /app/target/*.jar selectfood.jar

ENTRYPOINT ["java","-jar","selectfood.jar"]