# _context.md — governance
> Gerado em: 24/07/2026 | Fontes: `AGENTS.md`, `adr/adr-001-estacao-frequencia-cooperantes.md`, `lessons.md` | Palavras: ~300
> Atualizar quando: Mudança em protocolos, ADRs, regras de entrega ou configuração de pipeline.

## O que esta pasta contém
Diretrizes de desenvolvimento, protocolos de entrega, configurações de pipeline AI Workflow e registros de decisões arquiteturais do projeto.

## Pontos-chave para agentes

### AGENTS.md (raiz)
- `MEMORY_MODE=classic`, `TASK_MODE=STANDARD`, `COMMIT_MODE=manual`
- `SUGGESTION_LEVEL=1`, `BUG_LEVEL=1`
- Estratégia de documentação: `Atualizar`

### ADR-001 — Estação e Frequencia são Componentes Cooperantes
- Schema local (`feature/sprint-a`) é o canônico; fork é conciliado, nunca sobrescrito
- Dois módulos no mesmo app: (a) views WebView para Estação, (b) área administrativa (users/sessions/dashboard)
- `punch_type` é campo explícito no contrato de integração (com fallback ADR-08)
- Matching biométrico: Estação envia `user_id`, Frequencia retorna nome/foto/horário

### lessons.md — Lições operacionais
- `ActionController::API` NÃO inclui `ActionView::Layouts`, `Flash`, `MimeResponds`, `ImplicitRender`, `RequestForgeryProtection`. Necessário incluir manualmente para renderizar HTML.
- `protect_from_forgery` e `allow_forgery_protection` devem ser configurados explicitamente (API mode não herda defaults de `Base`)
- `config.api_only = true` exclui rotas `:new`/`:edit` de `resources` — declarar explicitamente

## Estado atual
- Projeto PoC: API Rails 8 substituindo Módulo Presença (Java/Tomcat) da Intranet TJPI
- 18 controllers (13 presenca, 5 admin), 2 models, 3 services, 127 testes passando
- Sprints 1-5, A, R concluídas; Sprint 6 parcial; Sprint 7 planejada
- Stack: Rails 8 API, PostgreSQL, bcrypt + DES/CBC/PKCS5Padding, AdminLTE 4 + Bootstrap 5.3

## Referências para aprofundamento
- ADR-001 → `docs/governance/adr/adr-001-estacao-frequencia-cooperantes.md`
- Lições sobre API mode + HTML → `docs/governance/lessons.md`
- Arquitetura da Estação JavaFX → `docs/documentacao-estacao-ponto.md`