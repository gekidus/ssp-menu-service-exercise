FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

RUN useradd --system --create-home --shell /usr/sbin/nologin appuser

COPY --from=build /app/target/menu-service.jar /app/menu-service.jar

RUN chown appuser:appuser /app/menu-service.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/menu-service.jar"]