# _context.md — inception
> Gerado em: 22/07/2026 | Fontes: Sessão Brainstorm (22/07/2026) | Palavras: ~230
> Atualizar quando: Novo PRD gerado, decisão arquitetural relevante, mudança de stack.

## O que esta pasta contém
Visão do produto, requisitos funcionais e não-funcionais, arquitetura de alto nível e especificações do sistema `api-ponto` — uma PoC Rails 8 que substitui o Módulo Presença da Intranet TJPI.

## Pontos-chave para agentes

### Visão Geral
- API REST Ruby on Rails 8 (modo API) que substitui servlets Java/Tomcat do Módulo Presença.
- Consumida pela **Estação Ponto de Presença** (cliente JavaFX Desktop) via HTTP GET/POST.
- Deve manter compatibilidade com criptografia DES/CBC/PKCS5Padding legada para autenticação.

### Stack
- **Backend:** Rails 8 API, PostgreSQL, bcrypt (senhas), DES/CBC/PKCS5Padding (compatibilidade).
- **Frontend (planejado):** Bootstrap 5.3 + AdminLTE 4, jQuery, HTML views embutidas no Rails.
- **Leitor biométrico:** Nitgen SDK (NBioBSP) — lado Estação, não no servidor.

### Repositório
- Código do Módulo Presença (Rails 8): `https://github.com/wilkersilva101/Frequencia`
- Código da Estação JavaFX: repositório local em `Estacao/`

## Estado atual
- 11 controllers implementados, 2 models (User, TimeRecord), 2 services (CryptoDes, FrequentadoresSerializer).
- 1 view HTML: `ponto_de_presenca/index.html.erb` (jQuery stub).
- Integração JavaFX via WebView: API validada via curl.

## Referências para aprofundamento
- Para documentação completa da Estação JavaFX → `docs/documentacao-estacao-ponto.md`
- Para relatório de integração Estação ↔ Presença → `docs/relatorio-interacao-presenca-estacao.md`
