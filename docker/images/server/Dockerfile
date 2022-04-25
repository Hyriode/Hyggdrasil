FROM itzg/minecraft-server:java8

# Override default envs
ENV EULA="TRUE" ONLINE_MODE="FALSE" ENABLE_RCON="FALSE" \
    TYPE="CUSTOM" CUSTOM_SERVER="https://hyriode.fr/spigot.jar" \
    MAX_PLAYERS="1000" MEMORY="6G"

# Disable healthcheck
HEALTHCHECK NONE