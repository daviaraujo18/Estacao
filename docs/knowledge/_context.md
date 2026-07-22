# _context.md — knowledge
> Gerado em: 22/07/2026 | Fontes: `docs/relatorio-interacao-presenca-estacao.md`, Sessão Brainstorm (22/07/2026) | Palavras: ~200
> Atualizar quando: Nova biblioteca integrada, novo padrão técnico adotado, mudança de versão de dependência.

## O que esta pasta contém
Base técnica, padrões de código, referências de APIs externas e conhecimento específico de integração entre a API Rails e sistemas legados (Estação JavaFX, Intranet TJPI).

## Pontos-chave para agentes

### Criptografia Compartilhada
- **Algoritmo:** DES/CBC/PKCS5Padding, chave fixa `"cryp:gpf"`.
- **Serviço Rails:** `CryptoDes` (compatível com `CryptoUtils.java` da Estação).
- **Formato wire:** UrlBase64 (não Base64 padrão — usar `Base64.encodeBase64URLSafeString`).

### Serialização Legada
- `DynFrequentadoresEstacao` retorna string com campos `;` e registros `'`.
- Formato: `<id>;<matricula>;<nome>;<hashFIR>;<fotoURL>;false;<sexo>;<predioId>'<id>;...`
- Hash MD5 para controle de versão (endpoint separado).

### Endpoints
- GET based (maioria): query params diretos na URL.
- POST: apenas `SincronizarRegistrosPonto` (registros criptografados) e `UploadFile`.
- Respostas: strings simples (plain text), sem JSON — compatibilidade legado.

## Estado atual
- Rails 8 API, PostgreSQL, bcrypt + DES/CBC/PKCS5Padding.
- 25 testes passando. Stacks futuras: Bootstrap 5.3, AdminLTE 4, jQuery.

## Referências para aprofundamento
- Para detalhes de criptografia → `docs/relatorio-interacao-presenca-estacao.md` (seção 12)
- Para documentação completa da Estação JavaFX → `docs/documentacao-estacao-ponto.md`
