# Iteration R — Reconciliação Estação/Frequência (ADR-001)
> Status: 📋 Planejada | Período: 24/07/2026 – 31/07/2026 (1 semana, precede Sprint A em execução real) | Goal: Destravar o merge pausado em `Frequencia/` conciliando o módulo administrativo (fork) com o schema/controllers locais, especializar o contexto WebView vs. administrativo, e tornar `entry`/`exit` um campo explícito no contrato de integração Estação↔Frequência | RFs: RF-01, RF-02, RF-04 | ADRs: ADR-001, ADR-08 (revisão)

## Desenvolvedores

| Dev | Perfil | Foco |
|-----|--------|------|
| Dev 1 | Fullstack Rails 8 | Resolução de merge, schema, controllers, contrato de integração |

## ⚠️ Alerta de Tensão Arquitetural — ADR-08 vs. ADR-001

O `ADR-08` (implementation_plan.md) decidiu que `punch_type` é **inferido** pelo `PunchTypeService`
a partir do último registro do dia, porque "a Estação não envia tipo de batida". O `ADR-001`
(Seção 3) exige que a entrada/saída seja um **campo explícito** enviado pela Estação e armazenado
pela Frequência em ambos os lados do contrato — não apenas inferido.

Isso é uma **contradição direta**, não uma nuance. As tarefas R.4 e R.5 abaixo assumem a resolução
mais segura e reversível (compatibilidade retroativa): o payload da Estação passa a aceitar um
campo `punch_type` opcional; se presente, é usado como fonte de verdade; se ausente (Estação antiga
ainda não atualizada), cai no fallback do `PunchTypeService` (ADR-08 vira fallback, não mais a
regra primária). **Esta decisão de design precisa de confirmação explícita do dev antes da
execução de R.4/R.5** — está registrada como pressuposto, não como fato consumado.

## Backlog

### GIT — Resolução do Merge Pausado

#### Tarefa R.1 — Resolver conflitos do merge em `Frequencia/` usando schema local como canônico

- **User Story:** Como time de desenvolvimento, quero que o merge pausado em `feature/sprint-a` seja resolvido preservando o schema e os controllers de presença locais como base, para que o módulo administrativo do fork seja incorporado sem substituir a modelagem de Frequentador/TimeRecord já validada.
- **Rastreabilidade:** ADR-001 (Seção 2, 5)
- **Estimativa:** 5pt | Atribuição: Dev 1
- **Dependências:** Nenhuma (ADR-001 já aceito)
- **Critérios de aceite:**
  - [x] Conflito em `db/schema.rb` resolvido: tabelas locais (`time_records`, `users`) preservadas integralmente; tabela nova do fork (índice `status` em `users`, migration `20260723121710`) incorporada sem colidir com colunas locais (`punch_type`, etc.) — ver Linha do Tempo sobre ausência de `frequentadores`/`estacoes` neste schema
  - [x] Conflito em `application_controller.rb` resolvido de forma temporária (merge simples, sem decidir arquitetura final — isso é R.2)
  - [x] Conflito em `application.html.erb` resolvido de forma temporária (idem, decisão final em R.2)
  - [x] Conflitos em `inicializar_ponto_controller.rb`, `iniciar_ponto_controller.rb`, `ponto_de_presenca_controller.rb` resolvidos preservando o comportamento local (render via layout `application` — nada do fork sobrescreve essa lógica)
  - [x] Nenhum arquivo é resolvido com `git checkout --ours` ou `--theirs` sem revisão manual do conteúdo (ambos os lados podem ter contribuição válida, conforme ADR-001)
  - [x] `git status` não mostra mais "You have unmerged paths" (confirmado: "Todos os conflitos foram corrigidos mas você continua mesclando")
  - [x] Suíte de testes existente (`bin/rails test`) roda sem erros de carregamento — `bundle install` executado com aprovação do dev (removida duplicata de `gem "importmap-rails"` no `Gemfile`, introduzida pelo próprio merge); migration pendente (`20260723121710_add_index_status_to_users`) aplicada em `test`; `bin/rails test` executa as 102 tarefas sem erro de boot/schema. 15 erros remanescentes (`ArgumentError: callback :require_login has not been defined`) são esperados e ficam para R.2 — `require_login` não foi aplicado globalmente ao `ApplicationController` (decisão deliberada desta tarefa), então `SessionsController`/afins que referenciam esse callback falham até R.2 criar `Admin::ApplicationController`. 0 erros de carregamento/sintaxe/schema.
  - [x] Merge NÃO é commitado ainda nesta tarefa — commit fica condicionado à conclusão de R.2 e R.3 (evita commit de estado intermediário quebrado)
- **Status:** ✅ Concluído (critérios de aceite cumpridos; os 15 erros de teste remanescentes são de escopo de R.2, não de R.1)

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | R.1 | `db/schema.rb`: não há colisão real de nome de tabela — `time_records`/`users` são as mesmas tabelas em ambos os lados (o Git já mesclou os `create_table` sem marcador de conflito, preservando `punch_type` local e o índice `status` do fork simultaneamente). Não existem tabelas `frequentadores`/`estacoes` neste schema/repo — apenas `version:` divergia; usada a maior (`2026_07_23_121710`). | Resolvido, staged |
  | R.1 (validação) | `bundle install` autorizado pelo dev; Gemfile tinha `gem "importmap-rails"` duplicado (linha 22 e 40) introduzido pelo merge — removida a duplicata da linha 40. Migration `20260723121710` pendente aplicada em `RAILS_ENV=test`. `bin/rails test`: 102 runs, 275 assertions, 0 failures, 15 errors (todos `require_login` indefinido — escopo de R.2). | Suíte carrega e roda; falhas remanescentes documentadas como escopo de R.2 |
  | R.1 | `application_controller.rb`: mantida base local (`ActionController::API` + includes de layout); adicionados `current_user`/`logged_in?`/`require_login` do fork como métodos disponíveis, SEM `before_action` global (quebraria rotas WebView sem sessão). Decisão documentada como temporária — separação definitiva é da Tarefa R.2. | Resolvido, staged |
  | R.1 | `application.html.erb`: mantida integralmente a versão local (AdminLTE, em uso pelas rotas de presença). Conteúdo do fork descartado do arquivo, mas recuperável via `git show origin/main:api-ponto/app/views/layouts/application.html.erb` para uso em R.2. | Resolvido, staged |
  | R.1 | `inicializar_ponto_controller.rb`, `iniciar_ponto_controller.rb`, `ponto_de_presenca_controller.rb`: diff do fork NÃO era trivial — mudava a classe-base para `ApiController < ActionController::API` (sem `ActionView::Layouts`/`Flash`/helpers), o que quebraria `render ..., layout: "application"`. Mantido `ApplicationController` local integralmente. | Resolvido, staged |
  | R.1 | Conflitos adicionais fora da lista original mas necessários para zerar `git status`: `manifest.js`, `application.css`, `application.js`, `config/importmap.rb`, `config/application.rb`, `views/presenca/ponto_de_presenca/index.html.erb`. Todos resolvidos mantendo a versão local (AdminLTE/jQuery/PunchType), exceto `config/application.rb`, onde o `session_store` nomeado do fork foi incorporado ao middleware manual local (mantendo `config.api_only = true`) para viabilizar o login administrativo sem alterar o pipeline das rotas de presença. Conteúdo do fork descartado (`application.js`/`importmap.rb`) recuperável via `git show origin/main:api-ponto/<arquivo>` para uso em R.2. | Resolvido, staged |
  | R.1 | Itens "ambos adicionaram" já staged antes desta tarefa (`manifest.js`, `application.css`, `javascript/application.js`, logs, cache bootsnap) foram na verdade encontrados AINDA em conflito (marcadores presentes) — reavaliados e resolvidos manualmente nesta tarefa, não apenas confirmados. | Resolvido, staged |
  | R.1 | `bin/rails test`: bloqueado por `Bundler::GemNotFound` (`turbo-rails`, `stimulus-rails` ausentes do vendor/bundle local, sem rede para `bundle install`). Gemfile/Gemfile.lock já vieram mesclados sem conflito de uma etapa anterior a esta tarefa. Validação alternativa via `ruby -c` em todos os arquivos `.rb` relevantes, sem erro de sintaxe. | Bloqueado — requer rede para validação completa |

---

### ARQUITETURA — Separação de Contexto

#### Tarefa R.2 — Especializar `ApplicationController`/`application.html.erb` por contexto (WebView vs. Administrativo)

- **User Story:** Como desenvolvedor, quero que o controller e o layout base sejam especializados por contexto (WebView-facing da Estação vs. área administrativa), para que mudanças em um módulo não afetem o outro e cada um possa evoluir com seus próprios requisitos de UI/autenticação.
- **Rastreabilidade:** ADR-001 (Seção 4)
- **Estimativa:** 5pt | Atribuição: Dev 1
- **Dependências:** R.1 (conflitos resolvidos, ambos os conteúdos disponíveis no working tree)
- **Critérios de aceite:**
  - [x] `ApplicationController` (base) mantém apenas o que é comum a ambos os contextos (CSRF, CSP, helpers gerais)
  - [x] Novo `Presenca::ApplicationController` (ou equivalente, seguindo o namespace `presenca/` já existente) herda de `ApplicationController` e concentra o que é específico do fluxo WebView (sem autenticação de sessão administrativa, layout `application` da Sprint A)
  - [x] Controllers administrativos (`sessions_controller.rb`, `users_controller.rb`, `dashboard_controller.rb`, `time_records_controller.rb`) passam a herdar de um `Admin::ApplicationController` (ou equivalente) com autenticação de sessão própria
  - [x] `application.html.erb` (layout AdminLTE da Estação/Sprint A) permanece exclusivo do namespace `presenca/`
  - [x] Layout `login.html.erb` (trazido do fork) é preservado e associado ao contexto administrativo, sem ser referenciado pelas views de `presenca/`
  - [x] Nenhuma rota administrativa é acessível via WebView sem autenticação, e nenhuma rota de `presenca/` exige a autenticação de sessão administrativa
  - [x] Teste de sistema/controller: acessar rota administrativa sem login redireciona para `sessions/new` (`test_deve_redirecionar_para_login_se_nao_autenticado`, `dashboard_controller_test.rb`); acessar rota `presenca/` funciona sem sessão administrativa (suíte de `presenca/*` já cobre isso e continua verde)
- **Status:** ✅ Concluído

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | R.2 | `ApplicationController` (base): removidos `current_user`/`logged_in?`/`require_login` (movidos para `Admin::ApplicationController`). Mantido apenas CSRF/CSP/helpers/layout comuns. | Resolvido |
  | R.2 | Criado `app/controllers/presenca/application_controller.rb` (`Presenca::ApplicationController < ApplicationController`), sem autenticação. Os 3 controllers de presença que herdavam de `ApplicationController` (`InicializarPontoController`, `IniciarPontoController`, `PontoDePresencaController`) passaram a resolver `Presenca::ApplicationController` automaticamente por lexical scoping do Ruby (estão dentro de `module Presenca`) — nenhuma edição de código foi necessária neles. Os demais controllers de `presenca/` (`ValidarFrequentador`, `DynFrequentadoresEstacao` etc.) continuam herdando de `ApiController`, fora do escopo desta tarefa. | Resolvido, sem alteração de arquivo |
  | R.2 | Criado `app/controllers/admin/application_controller.rb` (`Admin::ApplicationController < ApplicationController`) com `current_user`/`logged_in?`/`require_login` movidos da base, `before_action :require_login` e `layout "admin"`. | Resolvido |
  | R.2 | Movidos `sessions_controller.rb`, `users_controller.rb`, `dashboard_controller.rb`, `time_records_controller.rb` para `app/controllers/admin/`, classes `Admin::SessionsController < Admin::ApplicationController` (idem para os demais). `skip_before_action :require_login, only: [:new, :create]` preservado em `Admin::SessionsController` (tela de login continua acessível sem sessão). | Resolvido |
  | R.2 | `config/routes.rb`: rotas administrativas envolvidas em `scope module: "admin"` (não `namespace :admin`) para preservar os path helpers já usados pelos testes/views (`login_path`, `dashboard_path`, `users_path`, `time_records_path` etc.) sem prefixo `/admin` na URL — decisão registrada aqui conforme critério 5 da tarefa. Adicionadas rotas explícitas `new_user`/`edit_user` (ver achado abaixo). | Resolvido |
  | R.2 | `application.html.erb` inalterado (AdminLTE, exclusivo de `Presenca::`). Criado `app/views/layouts/admin.html.erb` a partir do conteúdo recuperado de `origin/main:api-ponto/app/views/layouts/application.html.erb`, adaptando `@current_user` para o helper `current_user` (exposto via `helper_method` em `Admin::ApplicationController`). Associado via `layout "admin"` no `Admin::ApplicationController`. `login.html.erb` preservado, usado explicitamente em `Admin::SessionsController#new`/`#create` via `render layout: "login"`. | Resolvido |
  | R.2 | Views movidas para `app/views/admin/{sessions,users,dashboard,time_records}/`. Referência `TimeRecordsController::PER_PAGE` na view de listagem atualizada para `Admin::TimeRecordsController::PER_PAGE`. | Resolvido |
  | R.2 (achado 1) | `bin/rails test` revelou, após corrigir `require_login`, um segundo erro pré-existente mascarado por ele: `skip_before_action :verify_authenticity_token` falhava porque `ActionController::RequestForgeryProtection` estava incluído na base mas `protect_from_forgery` nunca era chamado — o callback nunca existia para ser "pulado". Adicionado `protect_from_forgery with: :exception` na base. Como a base é `ActionController::API` (não herda o `allow_forgery_protection` configurado em `config/environments/test.rb` via `ActionController::Base`), foi replicado manualmente: `self.allow_forgery_protection = Rails.application.config.action_controller.allow_forgery_protection != false`. | Resolvido — achado de escopo adjacente, necessário para R.2 não regressar |
  | R.2 (achado 2) | `config.api_only = true` (pré-existente em `config/application.rb`, decisão de R.1) faz o Rails excluir `:new`/`:edit` das rotas padrão geradas por `resources` (comportamento documentado do `ActionDispatch::Routing::Mapper::Resources`, não é bug). Isso quebrava `new_user_path`/`edit_user_path` usados pelos testes/views administrativos. Corrigido com rotas explícitas (`get "users/new" ... as: :new_user`, `get "users/:id/edit" ... as: :edit_user`) combinadas com `resources :users, except: [:show, :new, :edit]`. | Resolvido |
  | R.2 (achado 3) | Pela mesma razão (`ActionController::API` usa `BasicImplicitRender`, que responde `head :no_content` (204) quando a action não chama `render` explicitamente, ao contrário de `ActionController::Base`), as actions administrativas (`index`, `new`, `edit` sem `render` explícito) devolviam 204 vazio em vez de renderizar a view. Adicionado `include ActionController::ImplicitRender` na base `ApplicationController` para restaurar a busca automática de template — necessário para o módulo administrativo funcionar; não afeta `Presenca::*`, que já usa `render` explícito em todas as actions. | Resolvido |
  | R.2 | `bin/rails test` (RUBYOPT=""): ANTES desta tarefa — 102 runs, 275 assertions, 0 failures, 15 errors (`require_login` indefinido). DEPOIS — 102 runs, 317 assertions, 0 failures, 0 errors. Nenhuma regressão introduzida; suíte de `presenca/*` (WebView) permanece 100% verde sem exigir sessão administrativa. | Suíte completa verde |
  | R.2 | Merge permanece NÃO commitado (`git status` segue "You have unmerged paths" / merge em andamento), conforme R.1/R.7. `tmp/cache/bootsnap` limpo durante depuração não foi deixado staged. | Confirmado |

---

### BANCO DE DADOS — Conciliação de Schema

#### Tarefa R.3 — Migração(ões) de conciliação: tabelas do módulo administrativo no schema local

- **User Story:** Como sistema, quero que as tabelas trazidas do fork (users, sessions, time_records administrativo, dashboard) coexistam no mesmo banco que as tabelas locais de Frequentador/TimeRecord, sem colisão de nomes nem perda de dados.
- **Rastreabilidade:** ADR-001 (Seção 1, 2)
- **Estimativa:** 5pt | Atribuição: Dev 1
- **Dependências:** R.1 (schema.rb resolvido no merge)
- **Critérios de aceite:**
  - [x] Levantamento explícito de colisão de nomes entre tabela `time_records` local (Sprint 5, campo `punch_type`) e qualquer tabela/model de mesmo nome trazido pelo fork — se houver colisão, a tabela local prevalece (ADR-001) e o fork é renomeado (ex: `admin_time_records`) ou seus dados são unificados na tabela local, com decisão registrada na Linha do Tempo desta tarefa — **verificado, sem colisão** (ver Linha do Tempo)
  - [x] Migration(ões) `db/migrate/*` criadas para tabelas novas do módulo administrativo — **satisfeito por descoberta**: as migrations (`create_users`, `create_time_records`, `add_punch_type_to_time_records`, `add_index_status_to_users`) já existem desde antes de R.3 (mescladas em R.1), não há tabela de sessão própria (Rails 8, sem gem de sessão persistida), nenhuma migration nova foi necessária
  - [x] `db:migrate` e `db:rollback` executam sem erro e sem perda de dados das tabelas locais (`time_records`, `users`) — validado nesta tarefa em `RAILS_ENV=test` (rollback de 2 passos + migrate de volta, sem erro, dados preservados)
  - [x] Models `User`, `TimeRecord` (local, presença) e qualquer model administrativo equivalente não colidem em `app/models/` — **satisfeito por descoberta**: não existe model administrativo equivalente separado; `User` é o MESMO model/tabela usado pelos dois módulos (campos `nome_completo`/`username`/`password_digest`/`status` para admin, `digitais_hash` para biometria/presença) — decisão de design de R.1 (schema único), não uma colisão a resolver
  - [x] Seeds/fixtures atualizados para cobrir os dois módulos — **satisfeito por descoberta**: `db/seeds.rb` já cria o usuário admin (`nome_completo: "Admin Admin"`, sem `digitais_hash`) e usuários biométricos (`digitais_hash` preenchido) na mesma leva de seed; nenhum ajuste foi necessário
  - [x] Testes de model para as tabelas novas — `TimeRecord` já tinha cobertura completa (`test/models/time_record_test.rb`); faltava `test/models/user_test.rb` — **criado nesta tarefa** (validações, callback `generate_username`, `has_secure_password`, scopes `ativos`/`com_digitais`, associação com `TimeRecord`)
- **Status:** ✅ Concluído (a maior parte dos critérios foi satisfeita por descoberta — mesclagem automática de R.1 já consolidou um único schema/model sem colisão; único trabalho novo desta tarefa foi validar migrate/rollback e adicionar `user_test.rb`)

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | R.3 (investigação) | Inspecionado `db/schema.rb` (version `2026_07_23_121710`): apenas 2 tabelas existem no schema/repo, `time_records` e `users`. Não existem `frequentadores`/`estacoes`/tabelas administrativas separadas. `users` acumula colunas dos dois módulos (`nome_completo`, `username`, `password_digest`, `status`, `digitais_hash`) na MESMA tabela — não há colisão de nome porque nunca houve duas tabelas concorrentes; o fork e o local sempre operaram sobre o mesmo `users`/`time_records` desde a mesclagem de R.1. | Verificado, sem colisão — nenhuma migration de renomeação/unificação necessária |
  | R.3 (investigação) | Inspecionado `app/models/user.rb` e `app/models/time_record.rb`: um único `User < ApplicationRecord` (com `has_secure_password`, `generate_username`, scopes `ativos`/`com_digitais`) serve tanto ao login administrativo (`Admin::SessionsController`) quanto ao matching biométrico (campo `digitais_hash`, usado por `DynFrequentadoresEstacaoController`). Nenhum `Admin::User` ou model administrativo equivalente existe — não há colisão de namespace a resolver. | Verificado, sem colisão de model |
  | R.3 (investigação) | Inspecionadas as 4 migrations existentes (`20260721190452_create_users`, `20260721190532_create_time_records`, `20260722152218_add_punch_type_to_time_records`, `20260723121710_add_index_status_to_users`) — todas já criadas/mescladas em R.1, todas usam `change` (reversíveis automaticamente), nenhuma edita `schema.rb` manualmente. Critério de "migration(ões) criadas para tabelas do módulo administrativo" já estava satisfeito antes de R.3 começar. | Satisfeito por descoberta, nenhuma migration nova criada |
  | R.3 (validação) | `RAILS_ENV=test bin/rails db:migrate:status`: todas as 4 migrations `up`. `db:rollback STEP=2` reverteu `add_index_status_to_users` e `add_punch_type_to_time_records` sem erro (`remove_index`/`remove_column`). `db:migrate` reaplicou ambas sem erro, `db:migrate:status` confirma as 4 `up` novamente. Executado apenas em `RAILS_ENV=test`, sem tocar em banco de desenvolvimento/produção. | `db:migrate`/`db:rollback` validados, sem perda de dados |
  | R.3 (investigação) | Inspecionado `db/seeds.rb`: já cria `User` admin (`"Admin Admin"`, sem `digitais_hash`, usado para login administrativo) e usuários biométricos (`digitais_hash` preenchido, usados por `TimeRecord`/presença) na mesma execução, cobrindo os dois módulos desde antes de R.3. Nenhum ajuste necessário. | Satisfeito por descoberta |
  | R.3 (investigação) | Inspecionado `test/models/time_record_test.rb`: 13 testes cobrindo `punch_type`, validações, scopes `by_date`/`last_today`. Cobertura já adequada, nenhuma alteração necessária. Não havia `test/models/user_test.rb`. | Gap real identificado: falta cobertura de `User` |
  | R.3 (execução) | Criado `test/models/user_test.rb` (15 testes): validações (`nome_completo`, `username` presença/unicidade case-insensitive, `status`, `password` mínimo 6), callback `generate_username` (geração automática, não sobrescreve username manual, resolução de colisão com sufixo numérico), `has_secure_password` (`authenticate`), scopes `ativos`/`com_digitais`, associação com `TimeRecord`. Mensagens de erro em inglês (`can't be blank`, `has already been taken`, `is too short (minimum is 6 characters)`) pois o projeto não tem locale `pt-BR` configurado para ActiveRecord — confirmado rodando os testes (falhas antes do ajuste mostraram as mensagens reais em inglês). | 15 runs, 31 assertions, 0 failures |
  | R.3 (validação final) | `bin/rails test` (suíte completa, `RUBYOPT=""`): 117 runs, 348 assertions, 0 failures, 0 errors (antes de R.3: 102 runs/317 assertions ao final de R.2; incremento de 15 runs/31 assertions vem exclusivamente de `user_test.rb`). Nenhuma regressão. | Suíte completa verde |
  | R.3 | Merge permanece NÃO commitado, conforme R.1/R.2/R.7. Nenhuma migration nova criada, nenhum dado apagado, banco de teste restaurado ao estado original (4 migrations `up`). | Confirmado |

---

### CONTRATO DE INTEGRAÇÃO — Campo Explícito Entrada/Saída

#### Tarefa R.4 — Payload da Estação: incluir campo explícito `punch_type` na marcação enviada

- **User Story:** Como Estação Ponto, quero enviar explicitamente se a marcação é entrada ou saída no payload de sincronização, para que a Frequência não dependa apenas de inferência para determinar o tipo do evento (ADR-001, Seção 3).
- **Rastreabilidade:** ADR-001 (Seção 3), ADR-08 (revisão — ver alerta de tensão acima)
- **Estimativa:** 3pt | Atribuição: Dev 1
- **Dependências:** Confirmação do dev sobre a resolução da tensão ADR-08/ADR-001 (ver alerta acima)
- **Critérios de aceite:**
  - [x] `SincronizarRegistrosPontoController` aceita campo opcional `punch_type` (`entry`/`exit`) em cada registro do payload criptografado
  - [x] Documentação de integração (`docs/relatorio-interacao-presenca-estacao.md` ou equivalente) atualizada descrevendo o novo campo do contrato, mantendo compatibilidade retroativa documentada
  - [x] Se o campo `punch_type` do payload for válido (`entry`/`exit`), ele é usado como fonte de verdade e `PunchTypeService.determine` NÃO é chamado para esse registro
  - [x] Se o campo estiver ausente ou inválido, o comportamento atual (fallback `PunchTypeService.determine`, ADR-08) é mantido sem alteração
  - [x] Teste de integração: payload com `punch_type` explícito é respeitado; payload sem o campo cai no fallback; payload com valor inválido (ex: `"foo"`) é tratado como ausente (fallback), não gera erro 500
  - [x] Nenhuma regressão nos testes existentes de `presenca_endpoints_test.rb` (Sprint A)
- **Status:** ✅ Concluído

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | R.4 (confirmação) | Dev confirmou a resolução "compatibilidade retroativa" já prevista, com a nuance de que o `PunchTypeService` continua sendo o caminho ATIVO na prática (sem cadastro/biometria real ainda enviando `punch_type`) — não deve ser descontinuado. | Confirmado, tarefa desbloqueada |
  | R.4 | Lidos por completo `app/controllers/presenca/sincronizar_registros_ponto_controller.rb` e `app/services/punch_type_service.rb` antes de qualquer alteração. Formato de linha existente: `<user_id>-<dd:mm:yyyy:hh:mm:ss>`, sem separador estruturado (não é JSON/CSV formal) — extensão de contrato precisou seguir o mesmo estilo posicional. | Investigação concluída |
  | R.4 | `sincronizar_registros_ponto_controller.rb`: regex de parsing estendida para capturar um terceiro grupo opcional `(?:-(\w+))?` ao final da linha (ex: `12-15:07:2026:14:30:45-entry`). Adicionada constante `VALID_PUNCH_TYPES = %w[entry exit]`. Se o valor capturado estiver na whitelist, é usado diretamente como `punch_type` (sem chamar `PunchTypeService.determine`); caso contrário (ausente, vazio, ou qualquer outro valor), cai no `begin/rescue` existente do fallback, sem alteração de comportamento nesse caminho. | Resolvido |
  | R.4 | `docs/relatorio-interacao-presenca-estacao.md` (Seção 3.7): adicionada subseção "Campo `punch_type` explícito (compatibilidade retroativa)" descrevendo o novo formato de linha, as regras de resolução (explícito válido vs. fallback) e deixando explícito que, no estado atual (sem Estação real enviando o campo), 100% dos registros seguem pelo fallback `PunchTypeService` — que permanece o comportamento ativo por padrão, não depreciado. | Documentado |
  | R.4 | `test/integration/presenca_endpoints_test.rb`: adicionados 3 testes — (a) `punch_type` explícito válido (`exit`) é respeitado mesmo quando o fallback daria um valor diferente (`entry`), com stub manual de `PunchTypeService.determine` que falha o teste (`flunk`) se for chamado, provando que não é invocado nesse caminho (projeto não usa gem de mock, `Minitest::Assertion` não é capturada pelo `rescue => e` do controller, propagando corretamente); (b) payload sem o campo cai no fallback (`entry`, comportamento inalterado); (c) valor inválido (`"foo"`) é tratado como ausente, cai no fallback, sem erro 500 (`assert_response :success`). | 3 novos testes, todos verdes |
  | R.4 (validação) | `RUBYOPT="" bin/rails test`: ANTES desta tarefa — 117 runs, 348 assertions, 0 failures, 0 errors (fim de R.3). DEPOIS — 120 runs, 355 assertions, 0 failures, 0 errors. Incremento de exatamente 3 runs/7 assertions vem dos novos testes desta tarefa; nenhuma regressão em `presenca_endpoints_test.rb` nem no restante da suíte. | Suíte completa verde |
  | R.4 | Escopo respeitado: NÃO foi criada coluna `punch_type_explicit` nem qualquer persistência de flag de auditoria (isso é R.5). `TimeRecord.create!` continua populando apenas `punch_type` com o valor resolvido (explícito ou inferido), sem novo campo no model/schema. Merge permanece NÃO commitado, conforme R.1/R.2/R.3/R.7. | Confirmado, sem feature creep |

---

#### Tarefa R.5 — Armazenamento explícito do tipo de evento na Frequência

- **User Story:** Como sistema de ponto, quero que o registro armazenado traga de forma auditável se o valor de `punch_type` veio explícito do payload ou foi inferido pelo serviço, para que seja possível diagnosticar divergências entre Estação e Frequência.
- **Rastreabilidade:** ADR-001 (Seção 3)
- **Estimativa:** 2pt | Atribuição: Dev 1
- **Dependências:** R.4 (payload aceita o campo), R.3 (schema conciliado, caso a coluna precise ser adicionada em tabela renomeada)
- **Critérios de aceite:**
  - [x] `TimeRecord` ganha coluna booleana `punch_type_explicit` (ou equivalente) indicando se o valor veio do payload (`true`) ou foi inferido (`false`)
  - [x] Migration correspondente com `default: false` para não quebrar registros históricos (todos os anteriores são implicitamente inferidos)
  - [x] `SincronizarRegistrosPontoController`/service popula o novo campo corretamente nos dois caminhos (explícito e fallback)
  - [x] Teste de model e de controller cobrindo os dois casos
  - [x] Nenhuma alteração no formato de exibição das views da Sprint A (badges Entrada/Saída continuam funcionando igual, independente da origem do dado)
- **Status:** ✅ Concluída

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | R.5 | Lidos por completo `app/controllers/presenca/sincronizar_registros_ponto_controller.rb`, `app/models/time_record.rb` e `db/schema.rb` antes de qualquer alteração. Confirmado que `punch_type_explicit` NÃO existia ainda (não foi introduzida sem querer em R.3/R.4, conforme suspeita levantada no boot). | Investigação concluída |
  | R.5 | Criada migration `db/migrate/20260723160000_add_punch_type_explicit_to_time_records.rb`: `add_column :time_records, :punch_type_explicit, :boolean, default: false, null: false`. Migration nova, nenhuma migration passada alterada. Aplicada em `RAILS_ENV=test` sem erro; `db/schema.rb` atualizado automaticamente para `version: 2026_07_23_160000`. | Migration aplicada, schema atualizado |
  | R.5 | `sincronizar_registros_ponto_controller.rb`: extraída a variável `punch_type_is_explicit` (resultado de `VALID_PUNCH_TYPES.include?(explicit_punch_type)`), reutilizada tanto para decidir o valor de `punch_type` (explícito vs. fallback `PunchTypeService`) quanto para popular `punch_type_explicit: punch_type_is_explicit` no `TimeRecord.create!`. Nenhuma mudança no formato de linha nem no comportamento de resolução de `punch_type` herdado de R.4. | Resolvido, sem duplicação de lógica |
  | R.5 | `test/models/time_record_test.rb`: 2 testes novos — `punch_type_explicit` default `false` ao criar sem informar o campo (cobre o caso "registro histórico/inferido"), e aceitação de `true` quando setado explicitamente. | 2 novos testes, verdes |
  | R.5 | `test/integration/presenca_endpoints_test.rb`: reaproveitados os 3 testes de R.4 (payload explícito válido, ausente, inválido) adicionando asserção de `punch_type_explicit` em cada um — `true` no caso explícito válido, `false` nos dois casos de fallback (ausente e inválido). Nenhum teste novo de arquivo, apenas asserções adicionais nos testes existentes (evita duplicação de setup). | 3 asserções novas, todas verdes |
  | R.5 | Confirmado por leitura que `app/views/presenca/iniciar_ponto/iniciar_ponto.html.erb` e `app/views/presenca/ponto_de_presenca/index.html.erb` (únicas views que leem `punch_type`) não referenciam `punch_type_explicit` e não foram alteradas — badges Entrada/Saída continuam exibindo com base apenas em `punch_type`, comportamento inalterado. | Confirmado, sem impacto em UI |
  | R.5 (validação) | `RUBYOPT="" bin/rails test`: ANTES desta tarefa — 120 runs, 355 assertions, 0 failures, 0 errors (fim de R.4). DEPOIS — 122 runs, 360 assertions, 0 failures, 0 errors. Incremento de exatamente 2 runs/5 assertions vem dos 2 novos testes de model; as 3 asserções adicionais em testes de integração existentes não criam novos runs. Nenhuma regressão. | Suíte completa verde |
  | R.5 | Escopo respeitado: nenhuma migration passada alterada, nenhum dado apagado, nenhuma alteração em views/UI, R.6 (matching biométrico) não iniciada. Merge permanece NÃO commitado, conforme R.1-R.4/R.7. Nenhum commit realizado nesta tarefa (aguardando instrução explícita). | Confirmado, sem feature creep |

---

### CONTRATO DE INTEGRAÇÃO — Matching Biométrico

#### Tarefa R.6 — Endpoint de matching biométrico: vincular marcação ao usuário/frequentador cadastrado

- **User Story:** Como Frequência, quero, ao receber uma marcação de ponto da Estação, realizar matching biométrico contra os cadastros existentes e vincular o registro ao frequentador correspondente, para que a Estação possa exibir a confirmação visual correta (ADR-001, Seção 3, passos 3-4).
- **Rastreabilidade:** ADR-001 (Seção 3), RF-03, RF-04
- **Estimativa:** 5pt | Atribuição: Dev 1
- **Dependências:** R.3 (schema conciliado — precisa saber onde estão os dados biométricos cadastrados), R.5 (campo `punch_type` já persistido)
- **Critérios de aceite:**
  - [x] Fluxo de matching reutiliza a identificação biométrica já resolvida pelo processo existente (`DynFrequentadoresEstacaoController`/hash MD5 — Sprint 3/4); esta tarefa NÃO reimplementa matching biométrico do zero, apenas garante que o resultado do matching (já feito no lado da Estação/download de digitais) seja vinculado corretamente ao `TimeRecord` no momento da sincronização
  - [x] `TimeRecord` sincronizado sempre está associado a um `Frequentador`/`User` válido (FK não nula); registro sem correspondência é rejeitado com mensagem de erro clara (não silenciosamente descartado)
  - [x] Resposta do endpoint de sincronização retorna, para cada registro aceito: nome do frequentador, foto (ou referência/URL da foto) e horário formatado — dados que a Estação usa para confirmação visual (ADR-001, Seção 3, passo 5)
  - [x] Caso a foto não esteja disponível no cadastro, a resposta indica isso explicitamente (não quebra o payload de resposta)
  - [x] Teste de integração cobrindo: registro com match válido (retorna nome/foto/horário), registro sem match (erro claro), registro com foto ausente
  - [x] Documentação de integração atualizada com o novo formato de resposta
- **Status:** ✅ Concluída

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | R.6 (investigação) | Lidos por completo `sincronizar_registros_ponto_controller.rb`, `app/models/user.rb`, `app/models/time_record.rb`, `db/schema.rb`, `frequentadores_serializer.rb` e `dyn_frequentadores_estacao_controller.rb` antes de qualquer alteração. **Achado-chave:** o payload da Estação JÁ traz o identificador do usuário como primeiro campo posicional da linha (`<user_id>-<dd:mm:yyyy:hh:mm:ss>[-<punch_type>]`, herdado desde a Sprint 5) — não há lacuna de contrato; o `user_id` É o resultado do matching biométrico feito no lado da Estação (hash MD5 via `DynFrequentadoresEstacaoController`, Sprint 3/4). Confirmado também que `users` NÃO possui nenhuma coluna de foto/avatar no schema, e o projeto não tem ActiveStorage configurado (`Gemfile` sem gem correspondente) — não foi inventado nenhum campo. Tarefa desbloqueada, sem necessidade de decisão do dev. | Investigação concluída, sem bloqueio |
  | R.6 | `sincronizar_registros_ponto_controller.rb`: substituído `next unless User.exists?(user_id)` (descarte silencioso) por `User.find_by(id: user_id)` com rejeição explícita registrada em `registros_rejeitados` (linha original + mensagem de erro) quando não há correspondência — nenhum `TimeRecord` é criado com FK nula. Linhas malformadas (regex não casa) também passam a ser reportadas como rejeitadas em vez de simplesmente puladas. | Resolvido |
  | R.6 | Resposta do endpoint alterada de texto puro `"sincronizado"` para JSON: `{status, registros_aceitos: [{user_id, nome, foto, horario}], registros_rejeitados: [{linha, erro}]}`. `nome` vem de `User#nome_completo`; `horario` é `punched_at` formatado `dd/mm/yyyy HH:MM:SS`; `foto` é um objeto `{disponivel: false, url: nil, motivo}` sempre indicando ausência explícita, já que não existe coluna de foto no schema atual (comentário no código referencia esta decisão para não ser reintroduzida por engano). Early return por `codAtivacao` ausente/inválido preservado em texto puro `"sincronizado"` (fora do escopo de R.6, comportamento pré-existente). Status HTTP: `200` quando todos os registros do payload são aceitos, `422 Unprocessable Entity` quando há pelo menos um rejeitado (padrão já usado por `Admin::UsersController`/`Admin::SessionsController`), sem descartar o lote inteiro — registros aceitos no mesmo payload continuam sendo persistidos mesmo com status `422`. | Resolvido |
  | R.6 | `test/integration/presenca_endpoints_test.rb`: reescritos os testes que dependiam do body em texto puro `"sincronizado"` para o novo contrato JSON (parse + asserções por campo). Adicionados/reescritos os 3 cenários exigidos: (a) match válido → `registros_aceitos` com `nome`/`foto`/`horario` corretos; (b) `user_id` inexistente → `422`, `registros_rejeitados` com mensagem clara, `TimeRecord.count` inalterado; (c) foto ausente no cadastro → resposta não quebra, `foto.disponivel: false`, `foto.motivo` explicativo. Teste de DES inválido também migrado para o novo contrato (linha malformada agora é rejeitada com `422` em vez de silenciosamente ignorada). | Cenários (a)/(b)/(c) cobertos, todos verdes |
  | R.6 | `docs/relatorio-interacao-presenca-estacao.md` (Seção 3.7): adicionada subseção "Formato de resposta — matching biométrico e confirmação visual (Sprint R, Tarefa R.6)" com o novo contrato JSON, exemplo completo, e as regras de `foto`/status HTTP. Preservada a nota de que o `codAtivacao` inválido mantém a resposta em texto puro (fora do escopo). | Documentado |
  | R.6 (validação) | `RUBYOPT="" bin/rails test`: ANTES desta tarefa — 122 runs, 360 assertions, 0 failures, 0 errors (fim de R.5). DEPOIS — 123 runs, 379 assertions, 0 failures, 0 errors. Incremento de 1 run (novo teste de foto ausente) e 19 assertions (novas asserções nos testes reescritos/existentes). Nenhuma regressão. | Suíte completa verde |
  | R.6 | Escopo respeitado: nenhum matching biométrico reimplementado, nenhuma coluna de foto inventada no schema/model, nenhuma migration criada. Merge permanece NÃO commitado, conforme R.1-R.5/R.7. Nenhum commit realizado nesta tarefa (aguardando instrução explícita/R.7). | Confirmado, sem feature creep |
  | R.6 (ajuste pós-conclusão) | Identificado risco de quebra de contrato: R.6 mudou a resposta padrão do endpoint de texto puro `"sincronizado"` (desde a Sprint 5) para JSON, mas o client real da Estação é código Java fora deste repositório que pode ainda esperar o texto plano. Decisão do dev: resolver via **content negotiation**, sem reverter nem forçar JSON. `sincronizar_registros_ponto_controller.rb`: extraído `formato_rico_solicitado?` (`request.format.json?` OU `params[:confirmacaoVisual] == "1"`, nome alinhado à convenção `codAtivacao`/`codigoUnicoMaquina`). Lógica de negócio de aceite/rejeição (FK não nula, `punch_type_explicit`) NÃO foi alterada — roda uma única vez, igual para os dois formatos. Sem opt-in: resposta volta a ser sempre texto puro `"sincronizado"` com `200`, idêntico ao comportamento pré-R.6, mesmo havendo registros rejeitados no lote. Com `Accept: application/json` ou `confirmacaoVisual=1`: mantém o contrato JSON rico de R.6 (`registros_aceitos`/`registros_rejeitados`, `422` quando há rejeição). | Resolvido |
  | R.6 (ajuste pós-conclusão) | `test/integration/presenca_endpoints_test.rb`: adicionados testes cobrindo (a) requisição sem sinalização → texto puro `"sincronizado"` mesmo com registro rejeitado; (b) `Accept: application/json` → JSON rico; testes existentes de R.6 (match válido, sem match, foto ausente, DES inválido, punch_type) migrados para opt-in explícito via `confirmacaoVisual: "1"`, mantendo os 3 cenários exigidos agora explicitamente no modo JSON. `docs/relatorio-interacao-presenca-estacao.md` (Seção 3.7) atualizado com a subseção "Formato de resposta — negociação de conteúdo", explicando os dois modos e como o client escolhe. | Documentado |
  | R.6 (ajuste pós-conclusão, validação) | `RUBYOPT="" bin/rails test`: 127 runs, 390 assertions, 0 failures, 0 errors (incremento de 4 runs e 11 assertions em relação ao fechamento de R.6, sem nenhuma regressão). Nenhum commit realizado (aguardando R.7). | Suíte completa verde |

---

### FINALIZAÇÃO

#### Tarefa R.7 — Commit do merge resolvido + validação de regressão completa

- **User Story:** Como time de desenvolvimento, quero commitar o merge resolvido somente depois que schema, controllers e contrato de integração estiverem conciliados e testados, para não introduzir um estado quebrado no histórico do repositório.
- **Rastreabilidade:** ADR-001
- **Estimativa:** 2pt | Atribuição: Dev 1
- **Dependências:** R.1, R.2, R.3, R.4, R.5, R.6
- **Critérios de aceite:**
  - [x] `bin/rails test` roda 100% da suíte (local + módulo administrativo) sem falhas — 127 runs, 390 assertions, 0 failures, 0 errors
  - [x] `git status` limpo antes do commit de merge (nenhum unmerged path)
  - [x] Commit de merge com mensagem descritiva referenciando ADR-001 — commit `1229f95` em `feature/sprint-a`
  - [x] `iteration_R.md` atualizado com status final de cada tarefa e Linha do Tempo preenchida
  - [ ] `progress/_context.md` atualizado (ou sinalizado ao Summarizer) indicando Sprint R concluída e sua relação com Sprint A — pendente, fora do escopo deste commit (só Frequencia)
- **Status:** ✅ Concluído (validação de integração end-to-end feita manualmente: Rails + Estação via Docker, registros reais gravados no banco com tags entry/exit corretas — ver conversa da sessão)

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | R.7 (validação manual) | Antes do commit, validação end-to-end fora do escopo automatizado: Rails subido localmente (`RUBYOPT="" bin/rails server`), Estação subida via Docker (`Estacao/run.sh`, ajustado para `--network host` + `docker run` direto, já que `docker compose run` não aceita `--network` por flag), apontando `base_intranet_url=http://localhost:3000`. Confirmado nos logs (`development.log`) requisições reais em `CarregaRelogioAtual`/`PontoDePresenca`/`SincronizarRegistrosPonto`, todas 200, com INSERT/COMMIT reais. 6 `TimeRecord`s persistidos com alternância `entry`/`exit` correta (todos via botão `simularDigitalTeste`, dev-only, `userId` hardcoded — matching biométrico real ainda não exercitado). Confirmado `User.connection.object_id == TimeRecord.connection.object_id` (mesmo banco `api_ponto_development` para os dois módulos). 1 usuário no banco (`teste.demo`, sem `digitais_hash`). | Integração validada manualmente, achados registrados |
  | R.7 (commit) | `bin/rails test`: 127 runs, 390 assertions, 0 failures, 0 errors. Staged todas as mudanças de `api-ponto/` via `git add -u` + arquivos novos legítimos (migration `20260723160000`, `test/models/user_test.rb`), **excluindo explicitamente** `api-ponto/davi`/`api-ponto/davi.pub` — par de chave SSH privada/pública encontrado não rastreado no working tree, nunca deve ir para o histórico do git. Commit `1229f95` criado em `feature/sprint-a` com mensagem referenciando ADR-001 e o resumo das tarefas R.1-R.6. `git status` limpo após commit, exceto os dois arquivos de chave SSH (deixados intencionalmente de fora, não removidos do disco). | Commit criado, chave SSH protegida |
  | R.7 | Escopo respeitado a pedido do dev: commit feito **somente** para o módulo Frequencia (`Frequencia/`); nenhuma alteração no repositório `Estacao/` (o ajuste em `run.sh` feito durante a sessão para viabilizar o teste manual permanece não commitado nesse outro repo, fora do escopo deste pedido). Atualização de `progress/_context.md` fica pendente para um passo seguinte (Summarizer ou pedido explícito do dev). | Escopo respeitado |

---

## Caminho Crítico

```
R.1 (resolver merge) ──→ R.2 (especializar controller/layout)
                     └──→ R.3 (conciliar schema) ──→ R.6 (matching biométrico)
                                                            │
                     R.4 (payload explícito) ──→ R.5 (armazenamento explícito) ──→ R.6
                                                                                      │
                                          R.2, R.3, R.4, R.5, R.6 ──→ R.7 (commit final)
```

## Riscos

| Risco | Probabilidade | Impacto | Mitigação |
|-------|:------------:|:-------:|-----------|
| **R01 — Colisão de nomes de tabela/model entre schema local e fork** | Alta | Alto | Levantamento explícito em R.3 antes de qualquer migration; decisão de renomear registrada na Linha do Tempo |
| **R02 — Tensão ADR-08 vs. ADR-001 não resolvida a tempo** | Média | Alto | R.4/R.5 bloqueadas até confirmação explícita do dev (ver alerta no topo do documento) |
| **R03 — Commit prematuro do merge com estado quebrado** | Média | Alto | R.1 explicitamente NÃO commita; commit só ocorre em R.7 após toda a suíte passar |
| **R04 — Dados biométricos cadastrados em local incompatível com schema conciliado** | Média | Alto | R.6 depende de R.3; validar view `presenca_frequentadorestacao` (risco R06 do plano macro) segue vigente |
| **R05 — Escopo de 27pts para 1 dev em 1 semana** | Alta | Médio | Priorizar R.1, R.2, R.3 (destravar o repositório) antes de R.4-R.6; R.6 pode escorregar para início da Sprint A em execução sem bloquear o restante |

## Definição de Pronto

- [ ] Merge em `feature/sprint-a` resolvido e commitado, sem conflitos pendentes
- [ ] `ApplicationController`/layout especializados por contexto (WebView vs. administrativo)
- [ ] Schema local conciliado com tabelas do módulo administrativo, sem perda de dados
- [ ] Payload da Estação e armazenamento na Frequência trazem `punch_type` explícito (com fallback documentado para ADR-08)
- [x] Endpoint de sincronização retorna nome/foto/horário para confirmação visual da Estação
- [ ] Suíte de testes completa passando (`bin/rails test`)
- [ ] `progress/_context.md` atualizado com a conclusão da Sprint R
