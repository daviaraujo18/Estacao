#!/bin/bash
#
# Entrypoint do container de desenvolvimento EstacaoPonto.
#
# 1. Garante que os stubs dos JARs privados estejam no ~/.m2 (o volume
#    maven-repo sombreia o ~/.m2 da imagem na primeira execucao).
# 2. Se houver JARs reais em /app/lib/, instala-os no ~/.m2 (sobrescrevendo
#    os stubs). Assim o desenvolvedor que tiver os JARs originais da TJPI
#    basta coloca-los em lib/ que o build usara os reais em vez dos stubs.
# 3. Executa o comando passado (padrao: bash interativo).
#
set -e

APP_DIR="/app"
LIB_DIR="$APP_DIR/lib"
STUBS_DIR="/opt/stubs"
M2_REPO="${M2_REPO:-$HOME/.m2/repository}"

# -----------------------------------------------------------------------
# Instala um JAR no repositorio Maven local (~/.m2).
# Usa mvn install:install-file.
# -----------------------------------------------------------------------
install_file() {
    local file="$1"
    local groupId="$2"
    local artifactId="$3"
    local version="$4"

    mvn install:install-file \
        -DgroupId="$groupId" \
        -DartifactId="$artifactId" \
        -Dversion="$version" \
        -Dpackaging=jar \
        -Dfile="$file" \
        -DgeneratePom=true \
        -q
}

# -----------------------------------------------------------------------
# 1. Reinstala os stubs a partir de /opt/stubs se o volume .m2 estiver vazio
#    ou se a versao dos stubs mudou (STUBS_VERSION).
# -----------------------------------------------------------------------
STUBS_VERSION_FILE="$HOME/.m2/STUBS_VERSION"
NEED_INSTALL=0
if [ ! -f "$M2_REPO/br/jus/tjpi/jna/0.1-nitgen/jna-0.1-nitgen.jar" ]; then
    NEED_INSTALL=1
elif [ ! -f "$STUBS_VERSION_FILE" ] || [ "$(cat "$STUBS_VERSION_FILE" 2>/dev/null)" != "$(cat "$STUBS_DIR/STUBS_VERSION" 2>/dev/null)" ]; then
    NEED_INSTALL=1
fi

if [ "$NEED_INSTALL" = "1" ]; then
    echo ">> Instalando stubs dos JARs privados no repositorio Maven local..."
    install_file "$STUBS_DIR/jna-0.1-nitgen.jar"          "br.jus.tjpi" "jna"          "0.1-nitgen"
    install_file "$STUBS_DIR/jna-platform-0.1-nitgen.jar" "br.jus.tjpi" "jna-platform" "0.1-nitgen"
    install_file "$STUBS_DIR/NBioBSPJNI-0.1-nitgen.jar"   "br.jus.tjpi" "NBioBSPJNI"   "0.1-nitgen"
    install_file "$STUBS_DIR/registry-0.1-nitgen.jar"     "br.jus.tjpi" "registry"     "0.1-nitgen"
    cp "$STUBS_DIR/STUBS_VERSION" "$STUBS_VERSION_FILE"
    echo ">> Stubs instalados."
fi

# -----------------------------------------------------------------------
# 2. Se houver JARs reais em /app/lib/, sobrescreve os stubs.
# -----------------------------------------------------------------------
if [ -d "$LIB_DIR" ]; then
    install_real_jar() {
        local file="$1"
        local groupId="$2"
        local artifactId="$3"
        local version="$4"
        if [ -f "$LIB_DIR/$file" ]; then
            echo ">> Instalando JAR real: $file (groupId=$groupId artifactId=$artifactId)"
            install_file "$LIB_DIR/$file" "$groupId" "$artifactId" "$version"
        fi
    }
    install_real_jar "jna.jar"          "br.jus.tjpi" "jna"          "0.1-nitgen"
    install_real_jar "jna-platform.jar" "br.jus.tjpi" "jna-platform" "0.1-nitgen"
    install_real_jar "NBioBSPJNI.jar"   "br.jus.tjpi" "NBioBSPJNI"   "0.1-nitgen"
    install_real_jar "registry.jar"     "br.jus.tjpi" "registry"     "0.1-nitgen"
fi

# -----------------------------------------------------------------------
# 3. Executa o comando informado (padrao: bash interativo)
# -----------------------------------------------------------------------
if [ $# -gt 0 ]; then
    exec "$@"
else
    exec bash
fi
