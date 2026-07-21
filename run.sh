#!/bin/bash
# run.sh — Compila e roda o estacaoPonto no Docker com X11 forwarding
set -e

cd "$(dirname "$0")"

xhost +local:docker 2>/dev/null || true

docker compose -f docker/docker-compose.yml run --rm \
  -e DISPLAY="$DISPLAY" \
  -e JAVA_TOOL_OPTIONS="-Dprism.order=sw -Djavafx.animation.fullspeed=true" \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  estacaoponto bash -c "
    mvn -q compile &&
    mvn -q exec:java -Dexec.mainClass=core.EstacaoPonto
"
