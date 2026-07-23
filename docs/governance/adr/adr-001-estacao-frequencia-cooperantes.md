# ADR-001 — Estação e Frequencia são Componentes Cooperantes de um Único Sistema

> **Status:** Aceita
> **Data:** 23/07/2026
> **Decisor:** Davi Araújo
> **Contexto de origem:** Merge pausado em `feature/sprint-a` (repo `Frequencia/`), conflitos em `application_controller.rb`, `db/schema.rb`, `application.html.erb` e controllers de presença, após pull do commit `9209906` ("Front Edn", fork `wilkersilva101`).

## Contexto

Durante a resolução de um `git merge origin/main`, o merge foi pausado porque as duas branches pareciam representar UIs concorrentes de aplicações distintas. O dev corrigiu essa interpretação: as duas branches evoluem **partes cooperantes de um único sistema** (Estação JavaFX + backend Rails "Frequencia"/api-ponto), não aplicações rivais. Um Orchestrator determinou que essa decisão deveria ser formalizada em ADR antes de qualquer alteração de código, elicitando 5 perguntas.

## Decisão

### 1. Delimitação das duas aplicações e compartilhamento de banco
- **Estação** (`estacaoPonto/`, JavaFX Desktop): recebe, via WebView, as telas implementadas localmente na branch `feature/sprint-a` (layout AdminLTE do dev — IniciarPonto, InicializarPonto, PontoDePresenca).
- **Frequencia** (repo Rails, api-ponto): contém DOIS módulos dentro do mesmo app/repo:
  - (a) o módulo que serve as views consumidas pela Estação via WebView;
  - (b) um módulo administrativo separado, trazido pelo commit `9209906` do fork, com `sessions_controller`, `users_controller`, `dashboard_controller` (login, users, dashboard, time_records) — área administrativa que **não interage diretamente com a Estação**.
- Ambos os módulos **compartilham o mesmo banco de dados** (não há dois bancos).

### 2. Banco de dados canônico
O `db/schema.rb` **local (HEAD/feature/sprint-a)** é a referência canônica. O schema trazido pelo merge do fork deve ser **conciliado/adaptado** ao schema local — nunca o inverso.

### 3. Contrato de integração usuário↔ponto (fluxo formal)
1. Estação (JavaFX) captura uma marcação de ponto via biometria do funcionário.
2. Estação envia a marcação para a aplicação Rails (Frequencia/api-ponto).
3. A aplicação Rails realiza matching biométrico contra os cadastros de usuário/frequentador já existentes no banco.
4. A aplicação Rails vincula o ponto registrado ao usuário/funcionário correspondente.
5. A Estação exibe/imprime, como confirmação visual: **nome**, **foto** e **horário** do ponto registrado.

Este fluxo é o contrato de integração formal entre Estação e Frequencia — qualquer alteração de endpoint ou payload relacionado a ele deve preservar essas 5 etapas.

**Entrada e saída:** o registro de ponto, em **ambas** as aplicações (Estação e Frequencia), deve especificar explicitamente se a marcação corresponde a uma **entrada** ou **saída** (tipo do evento). Isso deve estar presente tanto no payload enviado pela Estação quanto no armazenamento/vinculação feito pela Frequencia — não é um dado inferido, é um campo explícito do contrato.

### 4. `application_controller.rb` / `application.html.erb`
Não são decididos tecnicamente por este ADR. A necessidade (ou não) de separação em dois arquivos/contextos distintos (área WebView-facing vs área administrativa) é responsabilidade do Sprint Planner/Code Specialist, tomando este ADR como insumo de arquitetura.

### 5. Estratégia de destravamento do merge
Fora de escopo deste ADR — decisão técnica do Code Specialist, a ser tomada com base nesta decisão (schema local como canônico, dois módulos cooperantes no mesmo app).

## Consequências
- Nenhum código deve ser tratado como "descartável" por pertencer a uma branch "concorrente" — ambas contribuem ao mesmo sistema.
- Migrations/conciliação de schema devem partir do schema local, absorvendo apenas o que for necessário do fork (tabelas de users/sessions/dashboard) sem sobrescrever a modelagem local de presença/frequentadores.
- O módulo administrativo (users/sessions/dashboard) pode evoluir com rotas/controllers próprios, desde que não quebre o contrato de integração da Seção 3.

## Referências
- `Estacao/docs/documentacao-estacao-ponto.md`
- `Estacao/docs/relatorio-interacao-presenca-estacao.md`
- `Frequencia/PRD-POC-API-PONTO.md`
