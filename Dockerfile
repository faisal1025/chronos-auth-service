#---------------BUILD-----------------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /AuthService
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle
# 3️⃣ Give permission
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon
COPY src ./src
RUN ./gradlew build --no-daemon
#-------------RUN---------------------
FROM eclipse-temurin:17-jdk
WORKDIR /AuthService
COPY --from=build /AuthService/build/libs/*.jar authservice.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "authservice.jar"]
CMD ["--spring.profiles.active=prod"]
