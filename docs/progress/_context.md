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

### Sprint R (Planejada — bloqueante)
- Sprint de Reconciliação (ADR-001): destrava merge pausado em `Frequencia/` (`feature/sprint-a`),
  concilia módulo administrativo do fork (users/sessions/dashboard) com schema local, especializa
  `ApplicationController`/layout por contexto (WebView vs. administrativo), torna `punch_type`
  campo explícito no contrato Estação↔Frequência, implementa vínculo de matching biométrico com
  retorno nome/foto/horário.
- **Tensão sinalizada:** ADR-08 (inferência automática de punch_type) conflita com ADR-001
  (campo explícito). Resolução proposta em `iteration_R.md` (explícito com fallback ADR-08) —
  aguarda confirmação do dev antes da execução das tarefas R.4/R.5.
- Arquivo: `progress/iteration_R.md`.

### Próximas Sprints (Planejado)
- Sprint A: Layout AdminLTE + Bootstrap 5.3, views IniciarPonto e InicializarPonto. **Concluída no repo, mas seu merge está pausado — depende de Sprint R para ser commitada.**
- Sprint B: CRUD Frequentador (list, new, edit, show/explore, inativar) — UC01.
- Sprint C: ProblemaRegistro, job processamento (UC12), upload logs (UC08).

## Estado atual
- 25+ testes passando (models, controllers, services) — suíte local, ainda não integrada ao módulo administrativo do fork.
- CRUD Frequentador: apenas esqueleto (controller sem views). A ser implementado na Sprint B.
- Job de processamento e upload de logs: não iniciados.
- **Merge pausado em `Frequencia/` (branch `feature/sprint-a`)**: conflitos em `application_controller.rb`, `db/schema.rb`, `application.html.erb` e controllers de presença. Resolução formalizada em ADR-001 e detalhada em Sprint R.

## Referências para aprofundamento
- Para especificação detalhada dos endpoints → `docs/relatorio-interacao-presenca-estacao.md`
- Para documentação completa da Estação JavaFX → `docs/documentacao-estacao-ponto.md`
- Para a decisão arquitetural sobre Estação/Frequência como componentes cooperantes → `docs/governance/adr/adr-001-estacao-frequencia-cooperantes.md`
