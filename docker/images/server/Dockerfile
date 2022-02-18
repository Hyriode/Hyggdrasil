FROM itzg/minecraft-server:java8

# Override default envs
ENV EULA="TRUE" ONLINE_MODE="FALSE" ENABLE_RCON="FALSE" \
    TYPE="CUSTOM" CUSTOM_SERVER="https://hyriode.blob.core.windows.net/server/spigot.jar"

# Disable healthcheck
HEALTHCHECK NONE