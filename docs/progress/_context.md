# _context.md — progress
> Gerado em: 22/07/2026 | Fontes: Sessão Brainstorm (22/07/2026) | Palavras: ~220
> Atualizar quando: Sprint concluída, tarefa finalizada, mudança de milestone.

## O que esta pasta contém
Acompanhamento de sprints, status de iterações e planejamento incremental do projeto `api-ponto`. Contém `implementation_plan.md` e `iteration_N.md`.

## Pontos-chave para agentes

### Sprints 1-5 (Concluídas)
- Sprint 1: Setup Rails 8 API, PostgreSQL, modelo User com bcrypt.
- Sprint 2: Crypto DES (CryptoDes service), endpoint ValidarFrequentador.
- Sprint 3: Endpoints de relógio (CarregaRelogioAtual), heartbeat (AdicioneEstacao), hash MD5 (DynHashFrequentadoresEstacao).
- Sprint 4: Endpoint biométrico (DynFrequentadoresEstacao) e serialização.
- Sprint 5: Endpoint SincronizarRegistrosPonto, model TimeRecord, registros offline.

### Sprint 6 (Em andamento)
- Integração Estação JavaFX ↔ API Rails. Endpoints validados via curl.
- Pendente: validação completa do fluxo biometria → sincronização → confirmação.

### Próximas Sprints (Planejado)
- Sprint A: Layout AdminLTE + Bootstrap 5.3, views IniciarPonto e InicializarPonto.
- Sprint B: CRUD Frequentador (list, new, edit, show/explore, inativar) — UC01.
- Sprint C: ProblemaRegistro, job processamento (UC12), upload logs (UC08).

## Estado atual
- 25 testes passando (models, controllers, services).
- CRUD Frequentador: apenas esqueleto (controller sem views). A ser implementado na Sprint B.
- Job de processamento e upload de logs: não iniciados.

## Referências para aprofundamento
- Para especificação detalhada dos endpoints → `docs/relatorio-interacao-presenca-estacao.md`
- Para documentação completa da Estação JavaFX → `docs/documentacao-estacao-ponto.md`
