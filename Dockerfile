# Build Application Jar
FROM gradle:7.6.0 AS build

WORKDIR /usr/app/

# Copy Hyggdrasil project files
COPY . .

# Get username and token used in build.gradle
ARG GITHUB_USERNAME
ARG GITHUB_TOKEN
ENV GITHUB_USERNAME=$GITHUB_USERNAME GITHUB_TOKEN=$GITHUB_TOKEN

RUN gradle shadowJar

# Run Application
FROM openjdk:18.0.1.1-jdk

VOLUME ["/hyggdrasil"]
WORKDIR /hyggdrasil

# Copy previous builded Jar
COPY --from=build /usr/app/build/libs/Hyggdrasil-all.jar /usr/app/Hyggdrasil.jar
# Copy entrypoint script
COPY --from=build /usr/app/docker-entrypoint.sh /usr/app/docker-entrypoint.sh

# Add permission to file
RUN chmod +x /usr/app/docker-entrypoint.sh

STOPSIGNAL SIGTERM

# Start application
ENTRYPOINT ["sh", "/usr/app/docker-entrypoint.sh"]