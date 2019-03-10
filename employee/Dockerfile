FROM maven:3-jdk-8 AS builder
WORKDIR /proj
COPY pom.xml .
RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve
COPY . .
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml package

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=builder /proj/target/employee.jar /app
EXPOSE 8888
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,address=8888,suspend=n", "-jar", "employee.jar"]