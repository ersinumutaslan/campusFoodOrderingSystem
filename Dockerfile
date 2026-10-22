# Builds a Docker image of the app in two stages.

# Stage 1 ("kitchen"): compile the code into a runnable .jar with Maven + JDK 25
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
# Download the libraries first; Docker reuses this step until pom.xml changes, so rebuilds are faster
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
# Tests already run in CI (GitHub Actions), so skip them here
RUN mvn -B -q package -DskipTests

# Stage 2 ("lunch box"): only Java (JRE) + the .jar, no Maven and no source code
FROM eclipse-temurin:25-jre
WORKDIR /app
# Run as a normal user, not as root, for safety
RUN groupadd --system app && useradd --system --gid app app
COPY --from=build /app/target/campus-food-ordering-*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
