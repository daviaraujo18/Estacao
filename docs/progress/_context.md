# _context.md — progress
> Gerado em: 24/07/2026 | Fontes: Sessão Brainstorm (22/07/2026), sessão de verificação (24/07/2026) | Palavras: ~280
> Atualizar quando: Sprint concluída, tarefa finalizada, mudança de milestone.

## O que esta pasta contém
Acompanhamento de sprints, status de iterações e planejamento incremental do projeto `api-ponto`. Contém `implementation_plan.md`, `iteration_A.md` e `iteration_R.md`.

## Pontos-chave para agentes

### Sprints 1-5 (Concluídas)
- Sprint 1: Setup Rails 8 API, PostgreSQL, modelo User com bcrypt + auto-username
- Sprint 2: Crypto DES (CryptoDes service), UrlBase64 customizada
- Sprint 3: Endpoints CarregaRelogioAtual, ValidarFrequentador, InicializarPonto
- Sprint 4: DynFrequentadoresEstacao (serialização), DynHashFrequentadoresEstacao (MD5)
- Sprint 5: SincronizarRegistrosPonto, model TimeRecord, registros offline

### Sprint 6 (Parcial — 6/10)
- Integração Estação JavaFX ↔ API Rails validada via curl e Docker
- Pendente (requer hardware): teste biométrico com leitor Nitgen, sincronização offline, batida manual real

### Sprint A (Concluída)
- Layout AdminLTE 4 + Bootstrap 5.3, views IniciarPonto, InicializarPonto, PontoDePresenca
- jQuery bridge, auto-alternação entry/exit (PunchTypeService), sidebar/navbar

### Sprint R (Concluída — ver iteration_R.md)
- **Merge reconciliado** (ADR-001): fork `wilkersilva101` + schema local unificados
- Controllers especializados: `Presenca::ApplicationController` (WebView) e `Admin::ApplicationController` (sessão)
- `punch_type` explícito no payload da Estação (fallback ADR-08 preservado)
- Matching biométrico: retorno nome/foto/horário com content negotiation
- 127 testes, 390 assertions, 0 failures — merge commitado em `feature/sprint-a`
- Branch `main` local sync com `daviaruijo18/main`, 12 commits à frente de `origin/wilkersilva101`

### Próximas Sprints
- **Sprint 7 (📋 Planejada)**: Login Manual na view PontoDePresenca — botão, formulário accessKey/plainPassword, alert('LOGINMANUAL'), changeMensagemStatus
- **Sprint B**: Views do CRUD Frequentador (controller existe sem views)
- **Sprint C**: ProblemaRegistro, job processamento, upload logs

## Estado atual
- 127 testes passando (models, controllers, services, integration) — 0 failures, 0 errors
- PRD 100% implementado (41/41 requisitos)
- 13 controllers presenca, 5 controllers admin, 2 models, 3 services
- Views: AdminLTE 4 (WebView) + admin layout separado (login/admin)
- Merge pausado resolvido — main contém Sprints 1-5 + A + R

## Referências para aprofundamento
- Detalhes da Sprint R → `progress/iteration_R.md`
- Plano macro → `progress/implementation_plan.md`
- Especificação endpoints → `docs/relatorio-interacao-presenca-estacao.md`
- Documentação Estação JavaFX → `docs/documentacao-estacao-ponto.md`