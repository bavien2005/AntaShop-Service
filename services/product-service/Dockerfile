FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

EXPOSE 8086

CMD ["java", "-jar", "target/*SNAPSHOT.jar"]
