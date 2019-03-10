FROM maven:3-jdk-8 AS builder
WORKDIR /proj
COPY pom.xml .
RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve
COPY . .
ENTRYPOINT ["mvn", "-B", "-s", "/usr/share/maven/ref/settings-docker.xml", "test"]