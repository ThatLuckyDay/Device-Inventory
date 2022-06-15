FROM maven:3.8.5-eclipse-temurin-11-alpine
EXPOSE 8080
WORKDIR /device-inventory
CMD ["sh", "-c", "mvn package && java -jar target/device-inventory-0.0.1-SNAPSHOT.jar"]