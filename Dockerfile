
FROM maven:3.9.0-eclipse-temurin-17 AS build

WORKDIR /app


COPY pom.xml /app/
RUN mvn dependency:go-offline


COPY src /app/src/


RUN mvn clean package -DskipTests


FROM tomcat:10.1.13-jdk17-temurin

WORKDIR /usr/local/tomcat/webapps/


COPY --from=build /app/target/*.war ROOT.war

EXPOSE 8080


CMD ["catalina.sh", "run"]
