#!/bin/bash

: "${MIN_MEMORY:=256M}}"
: "${MAX_MEMORY:=4G}}"

echo "[init] Copying HyriSpigot jar"
cp /usr/app/Hyggdrasil.jar /hyggdrasil

echo "[init] Starting process..."
java ${JVM_XX_OPTS} -Xms${MIN_MEMORY} -Xmx${MAX_MEMORY} -jar Hyggdrasil