# ---------- Build stage ----------
FROM azul/zulu-openjdk:21 AS build

WORKDIR /app

ENV JAVA_HOME=/usr/lib/jvm/zulu21
ENV PATH="$JAVA_HOME/bin:$PATH"

RUN java -version

# Copy Gradle wrapper and config first (better caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy source
COPY src src

# Build the Spring Boot jar
RUN ./gradlew bootJar --no-daemon


# ---------- Runtime stage ----------
FROM azul/zulu-openjdk:21

WORKDIR /app

ENV JAVA_HOME=/usr/lib/jvm/zulu21
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
