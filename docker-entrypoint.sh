#!/usr/bin/env bash

: "${MIN_MEMORY:=256M}}"
: "${MAX_MEMORY:=4G}}"

echo "[init] Copying Hyggdrasil jar"
cp /usr/app/Hyggdrasil.jar /hyggdrasil

echo "[init] Starting process..."
exec java -Xms${MIN_MEMORY} -Xmx${MAX_MEMORY} -jar Hyggdrasil.jar