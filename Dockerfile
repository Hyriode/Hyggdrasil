# Build Application Jar
FROM gradle:7.4.2-jdk18 AS build

WORKDIR /usr/app/

# Copy Hyggdrasil project files
COPY runtime .

# Get username and token used in build.gradle
ARG USERNAME
ARG TOKEN
ENV USERNAME=$USERNAME TOKEN=$TOKEN

RUN gradle shadowJar

# Run Application
FROM openjdk:18.0.1.1-jdk

WORKDIR /usr/app/

# Get all environments variables
ENV MEMORY="1G"

# Copy previous builded Jar
COPY --from=build /usr/app/build/libs/Hyggdrasil-all.jar /usr/app/Hyggdrasil.jar

ENTRYPOINT java -Xmx${MEMORY} -jar Hyggdrasil.jar
