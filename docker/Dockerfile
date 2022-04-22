# Build Application Jar
FROM gradle:7.3.0-jdk8 AS build

WORKDIR /usr/app/

# Copy Hyggdrasil project files
COPY . .

# Get username and token used in build.gradle
ARG USERNAME
ARG TOKEN
ENV USERNAME=$USERNAME TOKEN=$TOKEN

RUN gradle shadowJar

# Run Application
FROM openjdk:16-slim

WORKDIR /usr/app/

# Get all environments variables
ENV MEMORY="1G" \
    STACK_NAME="" NETWORK_NAME="" \
    REDIS_HOST="127.0.0.1" REDIS_PORT=6379 REDIS_PASS="" \
    STARTING_PROXIES="1" MAX_PROXIES="10" PROXIES_STARTING_PORT="20001" \
    MINIMUM_LOBBIES="4"

# Copy previous builded Jar
COPY --from=build /usr/app/build/libs/Hyggdrasil-all.jar /usr/app/Hyggdrasil.jar

ENTRYPOINT java -Xmx${MEMORY} -jar Hyggdrasil.jar