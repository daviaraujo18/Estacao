#!/bin/bash
# run.sh — Compila e roda o estacaoPonto no Docker com X11 forwarding
set -e

cd "$(dirname "$0")"

xhost +local:docker 2>/dev/null || true

# Garante que a imagem existe/esta atualizada (docker compose run nao
# suporta --network por flag, entao usamos "docker run" direto abaixo).
docker compose -f docker/docker-compose.yml build

# --network host: sem isso, "localhost" dentro do container aponta pro
# proprio container, nao pro host onde a Frequencia (Rails) roda em dev.
docker run --rm \
  --network host \
  -e DISPLAY="$DISPLAY" \
  -e JAVA_TOOL_OPTIONS="-Dprism.order=sw -Djavafx.animation.fullspeed=true" \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  -v "$(pwd)":/app \
  -v docker_maven-repo:/root/.m2 \
  -w /app \
  estacaoponto:dev bash -c "
    mkdir -p /root/.local/share/TJPI/EstacaoPonto &&
    if [ ! -f /root/.local/share/TJPI/EstacaoPonto/config.properties ]; then
      printf 'app_name=ESTACAOPONTO\nbase_intranet_url=http://localhost:3000\ntela_cheia=false\nbloqueio_tela=false\nnivel_seguranca_leitor=8\nbaixa_foto=false' > /root/.local/share/TJPI/EstacaoPonto/config.properties
    fi &&
    mvn -q compile &&
    mvn -q exec:java -Dexec.mainClass=core.EstacaoPonto
"
