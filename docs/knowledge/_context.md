# _context.md — knowledge
> Gerado em: 24/07/2026 | Fontes: `docs/relatorio-interacao-presenca-estacao.md`, código-fonte, sessão de verificação | Palavras: ~200
> Atualizar quando: Nova biblioteca integrada, novo padrão técnico adotado, mudança de versão de dependência.

## O que esta pasta contém
Base técnica, padrões de código, referências de APIs externas e conhecimento específico de integração entre a API Rails e sistemas legados (Estação JavaFX, Intranet TJPI).

## Pontos-chave para agentes

### Criptografia Compartilhada
- **Algoritmo:** DES/CBC/PKCS5Padding, chave fixa `"cryp:gpf"`, IV = chave
- **Serviço Rails:** `CryptoDes` — encrypt/decrypt compatível com `CryptoUtils.java`
- **Formato wire:** UrlBase64 (substitui `+` → `-`, `/` → `_`, sem padding `=`)

### Serialização Legada
- `DynFrequentadoresEstacao`: campos separados por `;`, registros por `'`
- Formato: `<id>;<username>;<nome>;<hashFIR>;;false;N;0'<id>;...`
- Hash MD5 hex maiúsculo para controle de versão

### Endpoints
- GET (maioria): query params diretos na URL
- POST: `SincronizarRegistrosPonto` — aceita DES ou plain text (fallback)
- Respostas: plain text (legado) ou JSON (content negotiation via `Accept` ou `confirmacaoVisual=1`)

## Estado atual
- Rails 8 API mode + AdminLTE 4 + Bootstrap 5.3 + jQuery
- PostgreSQL, bcrypt + DES/CBC/PKCS5Padding
- 127 testes, 18 controllers, 2 models, 3 services

## Referências para aprofundamento
- Criptografia → `docs/relatorio-interacao-presenca-estacao.md` (seção 12)
- Documentação Estação JavaFX → `docs/documentacao-estacao-ponto.md`
- Lições sobre API mode + HTML → `docs/governance/lessons.md`