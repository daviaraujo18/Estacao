# EstacaoPonto — Ambiente de Desenvolvimento Docker

Este diretorio contem tudo que outro desenvolvedor precisa para compilar e
trabalhar no projeto **EstacaoPonto** em qualquer maquina com Docker, **sem
precisar instalar JDK, Maven, OpenJFX ou os JARs privados da TJPI**.

## Sumario

- [Inicio rapido](#inicio-rapido)
- [O que o container fornece](#o-que-o-container-fornece)
- [Comandos uteis](#comandos-uteis)
- [Os JARs privados da TJPI (stubs)](#os-jars-privados-da-tjpi-stubs)
- [Usando os JARs reais (opcional)](#usando-os-jars-reais-opcional)
- [O que funciona e o que nao funciona em Linux](#o-que-funciona-e-o-que-nao-funciona-em-linux)
- [Estrutura dos arquivos](#estrutura-dos-arquivos)
- [Resolucao de problemas](#resolucao-de-problemas)

---

## Inicio rapido

```bash
# 1. Na raiz do projeto (onde esta o pom.xml):
cd estacaoPonto

# 2. Construir a imagem (so na primeira vez ou quando o Dockerfile mudar):
docker compose -f docker/docker-compose.yml build

# 3. Abrir um shell interativo dentro do container:
docker compose -f docker/docker-compose.yml run --rm estacaoponto

# 4. Dentro do container, compilar o projeto:
mvn compile
```

> Dica: para encurtar os comandos, crie um alias:
> `alias ep='docker compose -f docker/docker-compose.yml run --rm estacaoponto'`
> e use `ep bash`, `ep mvn compile`, etc.

## O que o container fornece

| Componente  | Versao | Observacao                                       |
|-------------|--------|--------------------------------------------------|
| OpenJDK     | 8      | `openjdk-8-jdk` (Debian 9 Stretch)              |
| OpenJFX     | 8      | `openjfx` (jfxrt.jar em jre/lib/ext — auto classpath) |
| Maven       | 3.6.3  | instalado manualmente (apt traz versao antiga)  |
| Xvfb        | -      | display virtual para rodar JavaFX headless       |
| Stubs TJPI  | -      | jna, jna-platform, NBioBSPJNI, registry (no-op)  |

> **Por que Debian 9 (Stretch)?** E a ultima distribuicao cujo pacote
> `openjfx` e versao 8 (class version 52.0), compativel com `openjdk-8-jdk`.
> Em distribuicoes mais novas o `openjfx` e versao 11 (class version 54.0),
> que nao compila com JDK 8.

O diretorio do projeto e montado em `/app` dentro do container, entao qualquer
edicao feita no host aparece imediatamente no container (e vice-versa). O cache
do Maven (`~/.m2`) e persistido em um volume nomeado (`maven-repo`) para que as
dependencias nao sejam baixadas a cada execucao.

## Comandos uteis

Todos os comandos below sao executados **dentro do container** (apos `ep bash`)
ou passados diretamente:

```bash
# Compilar (sem empacotar)
mvn compile

# Empacotar o JAR (gera target/app/EstacaoPonto.jar + dependencias)
mvn package

# Limpar e empacotar
mvn clean package

# Rodar a aplicacao (UI JavaFX — requer X11 forwarding ou Xvfb)
xvfb-run -a mvn exec:java -Dexec.mainClass=core.EstacaoPonto

# Rodar os testes
mvn test
```

Para rodar a UI JavaFX com X11 forwarding (ver a janela no host Linux):
```bash
xhost +local:docker   # no host, uma vez
docker compose -f docker/docker-compose.yml run --rm \
    -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix estacaoponto \
    xvfb-run -a mvn exec:java -Dexec.mainClass=core.EstacaoPonto
```

## Os JARs privados da TJPI (stubs)

O projeto depende de 4 JARs privados da TJPI que **nao estao em nenhum
repositorio Maven publico**:

| GroupId        | ArtifactId    | Versao      | Uso real                            |
|----------------|---------------|-------------|-------------------------------------|
| br.jus.tjpi    | jna           | 0.1-nitgen  | Hooks de teclado Windows (KeyHook)  |
| br.jus.tjpi    | jna-platform  | 0.1-nitgen  | API Win32 (User32, Kernel32)        |
| br.jus.tjpi    | NBioBSPJNI    | 0.1-nitgen  | SDK do leitor biometrico Nitgen     |
| br.jus.tjpi    | registry      | 0.1-nitgen  | Registro do Windows (ICE_JNIRegistry)|

Para que o projeto **compile** sem esses JARs, o Docker build cria
**implementacoes-stub (vazias / no-op)** e as instala no `~/.m2` do container.
As stubs expõem a mesma API publica usada pelo codigo, mas todos os metodos
retornam valores padrao (false, 0, null, no-op). Isso permite:

- Compilar o projeto integralmente
- Iniciar a aplicacao (a UI JavaFX abre)
- Testar a logica nao-biometrica (conexao com intranet, navegacao, etc.)

As funcionalidades Windows-only (biometria, registro, hooks de teclado) ficam
**silenciosamente desativadas** em Linux — veja a secao abaixo.

Os stubs ficam em `docker/stubs/src/` e sao compilados por
`docker/build-stubs.sh` durante o `docker build`.

## Usando os JARs reais (opcional)

Se voce tiver os JARs originais da TJPI (por exemplo, do projeto antigo do
NetBeans), coloque-os na pasta `lib/` na raiz do projeto com estes nomes:

```
lib/jna.jar
lib/jna-platform.jar
lib/NBioBSPJNI.jar
lib/registry.jar
```

Ao iniciar o container, o `entrypoint.sh` detecta esses arquivos e os instala
no `~/.m2`, **sobrescrevendo os stubs**. Assim o build passa a usar os JARs
reais. Em Linux os JARs reais compilam normalmente, mas as DLLs nativas
associadas (NBioBSP.dll, etc.) nao funcionarao — apenas em Windows.

## O que funciona e o que nao funciona em Linux

### Funciona em Linux (com os stubs)
- Compilacao completa do projeto (`mvn compile` / `mvn package`)
- Inicializacao da aplicacao (UI JavaFX, com X11/Xvfb)
- Navegacao na intranet (WebView) e logica de conexao HTTP
- Persistencia de configuracao e dados em `~/.local/share/TJPI/EstacaoPonto/`
- Geracao de codigo unico da maquina (baseada em arquivo, nao em WMI)
- Logica de batida manual e cache de frequentadores

### NAO funciona em Linux (Windows-only)
- **Leitor biometrico Nitgen** — requer DLLs Windows (`NBioBSP.dll`,
  `NBioBSPCOM.dll`, `NBioBSPJNI.dll`). As chamadas sao no-op com os stubs.
- **Registro do Windows** — `RegistroWindows` / `WinRegistry` usam reflexao
  sobre metodos privados do JDK que so existem em Windows. Em Linux,
  `getInstallDir()` retorna vazio e `getCodigoAtivacaoRegistro()` retorna
  `"SistemaOperacionalNaoSuportado"` (ja havia guarda `OSVerifier.isWindows()`).
- **Hooks de teclado Windows** — `KeyHook` bloqueia teclas via JNA/User32.
  Ja possui guarda `isWindows()`: em Linux e no-op.
- **WMI (jWMI)** — usa `cmd.exe` + `cscript.exe`. Nao e chamado em runtime
  (o codigo ativo usa UUID em arquivo; o metodo WMI esta comentado).
- **Restart via .bat / cmd.exe** — `ScriptsBat` agora tem guarda `isWindows()`.
- **Geracao de instalador nativo** — `javapackager -native` (perfil `release`
  do pom). O perfil `dev` (ativo por padrao) pula esse passo.

### Adaptacoes feitas no codigo para Linux
- `core/EstacaoPonto.java`: `System.loadLibrary` das DLLs so roda em Windows;
  em outros SOs registra aviso e continua (antes fazia `System.exit(0)`).
- `core/LocalPaths.java`: caminhos conscientes do SO
  (`~/.local/share/TJPI/EstacaoPonto` em Linux, `AppData/Local/...` em Windows);
  `moverDiretorioAntigo()` so roda em Windows.
- `core/RegistroWindows.java`: `getInstallDir()` retorna vazio em nao-Windows.
- `utils/ScriptsBat.java`: criacao de `.bat` e `cmd.exe` so em Windows.
- `pom.xml`: perfil `dev` (ativo por padrao) pula os executions do
  `exec-maven-plugin` que dependem de `javapackager`/`pscp`.

## Estrutura dos arquivos

```
estacaoPonto/
├── docker/
│   ├── Dockerfile              # Imagem: Ubuntu 18.04 + JDK8 + OpenJFX8 + Maven
│   ├── docker-compose.yml      # Servico de dev com volume do codigo e do .m2
│   ├── entrypoint.sh           # Instala JARs reais de lib/ (se houver) e abre bash
│   ├── build-stubs.sh          # Compila stubs e instala no ~/.m2
│   ├── README.md               # Este arquivo
│   └── stubs/
│       └── src/
│           ├── com/sun/jna/Pointer.java
│           ├── com/sun/jna/platform/win32/{WinDef,WinUser,Kernel32,User32}.java
│           └── com/nitgen/SDK/BSP/NBioBSPJNI.java
├── lib/                        # (opcional) coloque aqui os JARs reais da TJPI
│   └── README.md
├── pom.xml                     # Adicionado perfil 'dev' (ativa por padrao)
└── src/main/java/...           # Codigo (com adaptacoes para Linux)
```

## Resolucao de problemas

### `mvn compile` falha com "package com.sun.jna.platform.win32 does not exist"
Os stubs nao foram instalados. Rode dentro do container:
```bash
bash /tmp/build-stubs.sh
```
Ou reconstrua a imagem: `docker compose -f docker/docker-compose.yml build`.

### Erro de `javafx.*` nao encontrado
O container usa `openjfx` 8 (Debian Stretch), que instala `jfxrt.jar` em
`$JAVA_HOME/jre/lib/ext/`, deixando `javafx.*` automaticamente no classpath.
Se estiver rodando Maven fora do container, instale o OpenJFX 8 ou use o
container. Dentro do container `echo $JAVA_HOME` deve apontar para
`/usr/lib/jvm/java-8-openjdk-amd64` e `jfxrt.jar` deve existir em
`$JAVA_HOME/jre/lib/ext/jfxrt.jar`.

### `Cannot find package` ou erro de dependencia
Rode `mvn dependency:tree` dentro do container para conferir se os 4 stubs
estao resolvidos. Se faltar algum, reconstrua a imagem.

### A UI nao abre (HeadlessException)
JavaFX precisa de display. Dentro do container use `xvfb-run`:
```bash
xvfb-run -a mvn exec:java -Dexec.mainClass=core.EstacaoPonto
```
Ou encaminhe o X11 do host (ver secao "Comandos uteis").

### Quero gerar o instalador nativo (Windows)
O perfil `dev` (ativo por padrao) pula o `javapackager`. Para ativar os
executions de empacotamento, use o perfil `release` **em uma maquina Windows**
com `javapackager` e `pscp` disponiveis:
```
mvn package -P !dev,release
```

### Reconstruir a imagem apos mudar stubs ou Dockerfile
```bash
docker compose -f docker/docker-compose.yml build
```
