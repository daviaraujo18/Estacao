# lib/ — JARs reais da TJPI (opcional)

Esta pasta e **opcional**. Por padrao, o Docker compila o projeto usando
**stubs** (implementacoes vazias) dos 4 JARs privados da TJPI, o que permite
desenvolver em Linux sem os JARs originais.

Se voce tiver os JARs reais (por exemplo, do projeto antigo do NetBeans ou do
SVN `pontodigital`), coloque-os aqui com **exatamente** estes nomes:

```
lib/jna.jar
lib/jna-platform.jar
lib/NBioBSPJNI.jar
lib/registry.jar
```

Ao iniciar o container, o `docker/entrypoint.sh` detecta esses arquivos e os
instala no repositorio Maven local (`~/.m2`), **sobrescrevendo os stubs**. Assim
o build passa a usar os JARs reais.

> Os JARs reais compilam em Linux, mas as DLLs nativas associadas
> (`NBioBSP.dll`, `NBioBSPCOM.dll`, `NBioBSPJNI.dll`, `ICE_JNIRegistry.dll`)
> so funcionam em Windows. Em Linux, as funcionalidades biométricas e de
> registro continuam indisponiveis mesmo com os JARs reais.

Os nomes dos arquivos devem ser exatamente os listados acima (minusculos,
com `.jar`). O `entrypoint.sh` mapeia cada arquivo para as coordenadas Maven
`br.jus.tjpi:<artifactId>:0.1-nitgen` conforme o `pom.xml`.
