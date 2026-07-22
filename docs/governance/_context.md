# _context.md — governance
> Gerado em: 22/07/2026 | Fontes: `AGENTS.md`, `docs/documentacao-estacao-ponto.md`, `docs/relatorio-interacao-presenca-estacao.md` | Palavras: ~280
> Atualizar quando: Mudança em protocolos, ADRs, regras de entrega ou configuração de pipeline.

## O que esta pasta contém
Diretrizes de desenvolvimento, protocolos de entrega, configurações de pipeline AI Workflow e registros de decisões arquiteturais do projeto. Rege como agentes leem, escrevem e entregam.

## Pontos-chave para agentes

### AGENTS.md (raiz)
- `MEMORY_MODE=classic` — usar `classic-memory-protocol` para carregar contexto; `_context.md` é a fonte primária.
- `TASK_MODE=STANDARD` — fluxo completo (Orchestrator → Brainstorm → Planner → Code → Review → CTO).
- `COMMIT_MODE=manual` — commits aguardam dev. `SUGGESTION_LEVEL=1`, `BUG_LEVEL=1`.
- Estratégia de documentação: `Atualizar` (se existir, atualizar; senão, criar).

### classic-memory-protocol (skill)
- Hierarquia de leitura obrigatória: `governance` → `progress` → `inception` → `analysis` → `knowledge`.
- `_context.md` máximo 800 palavras. Prioridade: decisões ativas > estado atual > histórico.

## Estado atual
- Projeto PoC: API Rails 8 substituindo Módulo Presença (Java/Tomcat) da Intranet TJPI.
- 11 controllers implementados, 2 models, 2 services, 25 testes passando.
- Sprints 1-5 concluídas (setup, crypto, endpoints); Sprint 6 parcial (validação integração JavaFX).
- Pendente: views HTML (AdminLTE), CRUD Frequentador, job sincronização, upload logs.
- Stack: Rails 8 API, PostgreSQL, bcrypt + DES/CBC/PKCS5Padding (compatibilidade Estação JavaFX).

## Referências para aprofundamento
- Para detalhes da arquitetura da Estação JavaFX → `docs/documentacao-estacao-ponto.md`
- Para mapeamento completo de integração (endpoints, criptografia, fluxos) → `docs/relatorio-interacao-presenca-estacao.md`
- Para configuração de pipeline e agentes → `AGENTS.md` (raiz do projeto)
