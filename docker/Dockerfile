# Dockerfile do ambiente de desenvolvimento do EstacaoPonto
#
# Imagem base: Debian 9 (Stretch) — escolhida porque o pacote openjfx do
# Stretch e versao 8 (class version 52.0), compativel com openjdk-8-jdk.
# O openjfx-8 instala jfxrt.jar no diretorio ext do JDK 8, deixando javafx.*
# automaticamente no classpath (como no Oracle JDK 8).
#
# O que esta imagem fornece:
#   - JDK 8 (OpenJDK 8) com JavaFX 8 (OpenJFX 8 / jfxrt.jar)
#   - Maven 3.6.3
#   - Xvfb (para rodar JavaFX headless, se necessario)
#   - Stubs dos JARs privados da TJPI ja compilados em /opt/stubs
#
# O que NAO funciona neste ambiente Linux:
#   - Leitor biometrico Nitgen (DLLs Windows-only)
#   - Registro do Windows / WMI
#   - Hooks de teclado Windows (JNA)
#   - Geracao de instalador nativo (javapackager)
# Esses recursos sao Windows-only e ficam como no-op em Linux (veja
# docker/README.md).
#
FROM debian:stretch

ENV DEBIAN_FRONTEND=noninteractive
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV MAVEN_HOME=/opt/maven
ENV PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH
ENV DISPLAY=:0

# Debian 9 (Stretch) e EOL — repos em archive.debian.org. Desativa a checagem
# de validade do Release (o arquivo assinado pode estar expirado).
RUN echo "deb http://archive.debian.org/debian stretch main" > /etc/apt/sources.list \
    && echo "deb http://archive.debian.org/debian-security stretch/updates main" >> /etc/apt/sources.list \
    && echo 'Acquire::Check-Valid-Until "false";' > /etc/apt/apt.conf.d/99no-check-valid-until

# JDK 8 + OpenJFX 8 + Xvfb (JavaFX headless) + dependencias de build.
# O openjfx do Stretch (v8) instala jfxrt.jar em jre/lib/ext do JDK 8,
# deixando javafx.* automaticamente no classpath.
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        openjdk-8-jdk \
        openjfx \
        xvfb \
        xauth \
        ca-certificates \
        wget \
        bash \
    && rm -rf /var/lib/apt/lists/*

# Maven 3.6.3 (binario oficial — versao do apt do Stretch e antiga demais)
RUN wget -q https://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz -O /tmp/maven.tar.gz \
    && tar -xzf /tmp/maven.tar.gz -C /opt \
    && ln -s /opt/apache-maven-3.6.3 /opt/maven \
    && rm /tmp/maven.tar.gz

WORKDIR /app

# Compila os stubs dos JARs privados. Os JARs resultantes ficam em /opt/stubs
# para que o entrypoint os reinstale no volume ~/.m2 (o volume sombreia o ~/.m2
# da imagem na primeira execucao).
COPY docker/stubs /build/stubs
COPY docker/build-stubs.sh /build/build-stubs.sh
RUN chmod +x /build/build-stubs.sh && bash /build/build-stubs.sh \
    && mkdir -p /opt/stubs && cp /build/stubs/*.jar /opt/stubs/ \
    && rm -rf /build
# Marcador de versao dos stubs. O entrypoint compara com ~/.m2/STUBS_VERSION
# para saber se deve reinstalar (bump deste valor quando os stubs mudarem).
RUN echo "stubs-v1" > /opt/stubs/STUBS_VERSION

# Entrypoint instala os stubs no ~/.m2 (se necessario), instala JARs reais de
# lib/ (se existirem) e abre o shell.
COPY docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
