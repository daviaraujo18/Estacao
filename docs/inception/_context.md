# _context.md — inception
> Gerado em: 24/07/2026 | Fontes: Sessão Brainstorm (22/07/2026), sessão de verificação (24/07/2026) | Palavras: ~220
> Atualizar quando: Novo PRD gerado, decisão arquitetural relevante, mudança de stack.

## O que esta pasta contém
Visão do produto, requisitos funcionais e não-funcionais, arquitetura de alto nível e especificações do sistema `api-ponto` — uma PoC Rails 8 que substitui o Módulo Presença da Intranet TJPI.

## Pontos-chave para agentes

### Visão Geral
- API REST Ruby on Rails 8 (modo API) consumida pela Estação Ponto (JavaFX) via HTTP
- Compatibilidade com criptografia DES/CBC/PKCS5Padding legada
- PRD 100% implementado — `Frequencia/PRD-POC-API-PONTO.md`

### Stack
- **Backend:** Rails 8 API, PostgreSQL, bcrypt (senhas), DES/CBC/PKCS5Padding, AdminLTE 4 + Bootstrap 5.3
- **Leitor biométrico:** Nitgen SDK (NBioBSP) — lado Estação, não no servidor

### Repositórios
- Rails API (`api-ponto`): `https://github.com/daviaraujo18/Frequencia` (branch `main`)
- Fork original: `https://github.com/wilkersilva101/Frequencia`
- Estação JavaFX: repositório local em `Estacao/`

## Estado atual
- 18 controllers, 2 models, 3 services, 127 testes passando
- Sprints 1-5, A, R concluídas; Sprint 6 parcial (requer hardware)
- Última sprint não iniciada: Sprint 7 — Login Manual na view PontoDePresenca

## Referências para aprofundamento
- Documentação Estação JavaFX → `docs/documentacao-estacao-ponto.md`
- Relatório de integração → `docs/relatorio-interacao-presenca-estacao.md`
- ADR-001 (cooperação Estação/Frequencia) → `docs/governance/adr/adr-001-estacao-frequencia-cooperantes.md`