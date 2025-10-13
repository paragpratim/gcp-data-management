FROM eclipse-temurin:17-jre AS run
WORKDIR /app
COPY services/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
