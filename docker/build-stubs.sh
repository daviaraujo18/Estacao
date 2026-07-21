#!/bin/bash
#
# Compila as classes-stub dos JARs privados da TJPI e as instala no repositorio
# Maven local (~/.m2), permitindo que o projeto EstacaoPonto compile em ambientes
# onde os JARs reais (jna, jna-platform, NBioBSPJNI, registry) nao estao
# disponiveis.
#
# Os stubs sao implementacoes VAZIAS (no-op). Eles apenas expõem a mesma API
# publica usada pelo projeto para que o javac nao falhe. Em producao (Windows),
# os JARs reais devem substituir os stubs (veja lib/README.md).
#
# Uso: bash docker/build-stubs.sh
#
set -euo pipefail

STUBS_DIR="$(cd "$(dirname "$0")" && pwd)/stubs"
SRC_DIR="$STUBS_DIR/src"
OUT_DIR="$STUBS_DIR/out"
REGISTRY_DIR="$STUBS_DIR/registry"

MVN="${MVN:-mvn}"

echo "==> Limpando saida anterior..."
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

echo "==> Compilando stubs..."
javac -d "$OUT_DIR" \
    "$SRC_DIR"/com/sun/jna/Pointer.java \
    "$SRC_DIR"/com/sun/jna/platform/win32/WinDef.java \
    "$SRC_DIR"/com/sun/jna/platform/win32/WinUser.java \
    "$SRC_DIR"/com/sun/jna/platform/win32/Kernel32.java \
    "$SRC_DIR"/com/sun/jna/platform/win32/User32.java \
    "$SRC_DIR"/com/nitgen/SDK/BSP/NBioBSPJNI.java

echo "==> Empacotando JARs..."

# jna-0.1-nitgen.jar  ->  com/sun/jna/* (apenas Pointer, sem inner classes)
( cd "$OUT_DIR" && jar cf "$STUBS_DIR/jna-0.1-nitgen.jar" com/sun/jna/*.class )

# jna-platform-0.1-nitgen.jar  ->  com/sun/jna/platform/win32/* (inclui inner classes)
( cd "$OUT_DIR" && jar cf "$STUBS_DIR/jna-platform-0.1-nitgen.jar" com/sun/jna/platform/win32/*.class )

# NBioBSPJNI-0.1-nitgen.jar  ->  com/nitgen/SDK/BSP/* (inclui todas as inner classes)
( cd "$OUT_DIR" && jar cf "$STUBS_DIR/NBioBSPJNI-0.1-nitgen.jar" com/nitgen/SDK/BSP/*.class )

# registry-0.1-nitgen.jar  ->  JAR vazio (apenas MANIFEST.MF)
# O JAR registry (com.ice.registry / ICE_JNIRegistry) nao e referenciado por
# nenhuma classe Java do projeto; e carregado apenas via System.loadLibrary.
# Um JAR vazio satisfaz a dependencia declarada no pom.xml.
mkdir -p "$REGISTRY_DIR/META-INF"
echo "Manifest-Version: 1.0" > "$REGISTRY_DIR/META-INF/MANIFEST.MF"
echo "Created-By: docker/build-stubs.sh" >> "$REGISTRY_DIR/META-INF/MANIFEST.MF"
( cd "$REGISTRY_DIR" && jar cfm "$STUBS_DIR/registry-0.1-nitgen.jar" META-INF/MANIFEST.MF )

echo "==> Instalando stubs no repositorio Maven local..."

install_file() {
    $MVN install:install-file \
        -DgroupId="$1" \
        -DartifactId="$2" \
        -Dversion="$3" \
        -Dpackaging=jar \
        -Dfile="$STUBS_DIR/$2-$3.jar" \
        -DgeneratePom=true \
        -q
}

install_file "br.jus.tjpi" "jna"           "0.1-nitgen"
install_file "br.jus.tjpi" "jna-platform"  "0.1-nitgen"
install_file "br.jus.tjpi" "NBioBSPJNI"    "0.1-nitgen"
install_file "br.jus.tjpi" "registry"      "0.1-nitgen"

echo "==> Stubs instalados com sucesso."
echo "    jna-0.1-nitgen.jar"
echo "    jna-platform-0.1-nitgen.jar"
echo "    NBioBSPJNI-0.1-nitgen.jar"
echo "    registry-0.1-nitgen.jar"
