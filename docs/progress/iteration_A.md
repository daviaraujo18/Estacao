# Iteration A — Layout Base + Views Iniciais + Auto-Alternação Entrada/Saída
> Status: 📋 Planejada | Período: 27/07/2026 – 07/08/2026 (2,5 semanas) | Goal: Estabelecer layout compartilhado com AdminLTE 4 + Bootstrap 5.3, entregar as views HTML que a Estação carrega no WebView (IniciarPonto, InicializarPonto) e implementar a lógica de auto-alternação entre ponto de entrada e saída | RFs: RF-01, RF-02, RF-04

## Desenvolvedores

| Dev | Perfil | Foco |
|-----|--------|------|
| Dev 1 | Fullstack Rails 8 | AdminLTE, views HTML, PunchTypeService, migration, controllers |

## Backlog

### BACKEND — Model, Service, Controllers

#### Tarefa A.1 — Migration: adicionar `punch_type` ao model TimeRecord

- **User Story:** Como sistema de ponto, quero que cada registro de batida tenha um campo `punch_type` para distinguir entre entrada e saída.
- **Rastreabilidade:** RF-04, ADR-08
- **Estimativa:** 1pt | Atribuição: Dev 1
- **Dependências:** Nenhuma (Sprint 6 — model TimeRecord já existe)
- **Critérios de aceite:**
  - [x] Migration adiciona coluna `punch_type` (string) à tabela `time_records`
  - [x] Migration inclui `default: nil` (não pode ter default — é inferido pelo serviço)
  - [x] Model `TimeRecord` tem validação de inclusão: `in: %w[entry exit]`
  - [x] Model `TimeRecord` tem scope `by_date(date)` e scope `last_today(user_id)`
  - [x] `db:migrate` e `db:rollback` funcionam sem perda de dados
  - [x] Testes de model para a nova coluna e scopes
- **Status:** ✅ Concluída (commit `c91169c`)

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | 22/07 12:18 | Criar branch feat/punch-type-migration | Branch criada a partir de main |
  | 22/07 12:18 | Migration AddPunchTypeToTimeRecords | string nullable sem default |
  | 22/07 12:18 | Atualizar model TimeRecord | Validação + scopes by_date/last_today |
  | 22/07 12:18 | Criar test/models/time_record_test.rb | 13 testes, 18 assertions, 0 failures |
  | 22/07 12:18 | Commit A.1 | feat: adiciona coluna punch_type ao model TimeRecord |

---

#### Tarefa A.2 — Service `PunchTypeService` com lógica de auto-alternação

- **User Story:** Como sistema de ponto, quero que o tipo da batida (entrada/saída) seja automaticamente inferido a partir do último registro do dia, para que o frequentador não precise indicar manualmente se está entrando ou saindo.
- **Rastreabilidade:** RF-04, ADR-08
- **Estimativa:** 3pt | Atribuição: Dev 1
- **Dependências:** A.1 (coluna `punch_type` existe)
- **Critérios de aceite:**
  - [x] `PunchTypeService.determine(user_id, reference_time)` retorna `'entry'` se:
    - Não há nenhum registro do usuário no dia da `reference_time`
    - O último registro do dia é `'exit'`
  - [x] `PunchTypeService.determine(user_id, reference_time)` retorna `'exit'` se:
    - O último registro do dia é `'entry'`
  - [x] A busca do último registro usa `TimeRecord.last_today(user_id)` considerando o dia completo (00:00:00 até 23:59:59) no fuso do servidor
  - [x] Ordenação por `punched_at DESC` (não `created_at`) — se dois registros no mesmo segundo, usa `created_at` como desempate
  - [x] Service é testado com 3 cenários: sem registro anterior, último era `entry`, último era `exit`
  - [x] Service é testado com registros de dias anteriores (não interfere no dia atual)
  - [x] Tratamento de borda: se `user_id` não existe, retorna `'entry'` (fallback seguro)
- **Status:** ✅ Concluída

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | 22/07 12:30 | Criar `app/services/punch_type_service.rb` | Service com `determine(user_id, reference_time)` infere entry/exit |
  | 22/07 12:30 | Criar `test/services/punch_type_service_test.rb` | 10 testes cobrindo 5 cenários + bordas |
  | 22/07 12:35 | Commit A.2 | feat: implementa PunchTypeService com auto-alternação entry/exit |
  | 22/07 12:30 | Executar `RAILS_ENV=test bin/rails test` | 48 runs, 94 assertions, 0 failures, 0 errors |

---

#### Tarefa A.3 — Integrar `PunchTypeService` no `SincronizarRegistrosPontoController`

- **User Story:** Como administrador do ponto, quero que ao sincronizar registros offline, o sistema automaticamente atribua o tipo de batida (entrada/saída), para que o histórico fique correto sem intervenção manual.
- **Rastreabilidade:** RF-04, ADR-08
- **Estimativa:** 2pt | Atribuição: Dev 1
- **Dependências:** A.2 (PunchTypeService implementado)
- **Critérios de aceite:**
  - [x] Antes de salvar cada `TimeRecord`, o controller chama `PunchTypeService.determine(user_id, punched_at)` e atribui o resultado a `record.punch_type`
  - [x] A chamada ao service ocorre para cada registro recebido no payload criptografado (não apenas para o lote inteiro)
  - [x] Se o service falhar (exceção), o registro ainda é salvo com `punch_type: nil` e um log de警告 é emitido (não bloqueia a sincronização)
  - [x] Testes de controller/integração: enviar payload com 3 registros e verificar que os `punch_type` foram atribuídos corretamente (alternando)
  - [x] Teste de regressão: sincronização sem a coluna (se `punch_type` for nil) ainda funciona
- **Status:** ✅ Concluída (commit `3d1ebf3`) — **status corrigido nesta verificação**: a tarefa estava marcada `⬜ Pendente` no documento, mas o código e os testes já a implementavam integralmente antes das tarefas A.9/A.10/A.11 serem executadas. Divergência documental identificada e conciliada.

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | 22/07 (retroativo) | Commit `3d1ebf3` — feat: integra PunchTypeService no SincronizarRegistrosPonto (A.3) | `PunchTypeService.determine` chamado por registro, com `rescue` + `Rails.logger.warn` em caso de falha, fallback `punch_type: nil` |
  | 22/07 (retroativo) | Testes em `test/integration/presenca_endpoints_test.rb` | Cobrem alternância entry/exit, ausência de registro prévio, e fallback nil em falha de service |
  | 22/07 (auditoria) | Verificação nesta sessão via `git log`, leitura do controller e `grep` nos testes | Confirmado 100% dos critérios de aceite; status atualizado de Pendente para Concluída |

---

#### Tarefa A.4 — `InicializarPontoController` servir view HTML

- **User Story:** Como Estação Ponto, quero que o endpoint `InicializarPonto` retorne uma página HTML para que o WebView possa carregá-la e redirecionar para a tela principal de batimento.
- **Rastreabilidade:** RF-02, RF-04
- **Estimativa:** 2pt | Atribuição: Dev 1
- **Dependências:** Nenhuma (controller já existe — Sprint 6)
- **Critérios de aceite:**
  - [x] Controller responde com HTML (view ERB via `render html:`, não inline)
  - [x] View `inicializar_ponto.html.erb` existe (standalone sem layout — A.7 pendente)
  - [x] View lê `params[:codigoAtivacao]` e `params[:codigoUnicoMaquina]` e armazena em elementos ocultos (`#codigoAtivacao`, `#codigoUnicoMaquina`)
  - [x] View dispara `alert('recuperarFrequentadores')` no `onload` via JS (gatilho para Estação baixar digitais)
  - [x] View redireciona via JS para `/presenca/PontoDePresenca` após carregamento (simulando `window.location`)
  - [x] Se `codigoAtivacao` estiver ausente, exibe mensagem de erro amigável
- **Status:** ✅ Concluída

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | 22/07 12:49 | Modificar `InicializarPontoController` | Substituir `render html:` inline por leitura de template ERB com `render html: ERB.new(File.read(...)).result(binding).html_safe` |
  | 22/07 12:50 | Criar `app/views/presenca/inicializar_ponto/inicializar_ponto.html.erb` | View standalone (DOCTYPE completo): campos ocultos, alert JS, redirect via setTimeout, erro se codigoAtivacao ausente |
  | 22/07 12:51 | Criar `test/controllers/presenca/inicializar_ponto_controller_test.rb` | 4 testes: hidden fields, erro sem código, alert JS, redirect JS |
  | 22/07 12:52 | Atualizar `test/integration/presenca_endpoints_test.rb` | Teste "sem params" ajustado para esperar mensagem de erro (antes esperava redirect) |
  | 22/07 12:53 | Executar `RAILS_ENV=test bin/rails test` | 55 runs, 128 assertions, 0 failures, 0 errors |

---

#### Tarefa A.5 — `IniciarPontoController` servir view HTML (landing page)

- **User Story:** Como Estação Ponto, quero que o endpoint `IniciarPonto` retorne a landing page inicial para que o WebView exiba a tela de boas-vindas com o status da última batida do frequentador.
- **Rastreabilidade:** RF-02, RF-04
- **Estimativa:** 2pt | Atribuição: Dev 1
- **Dependências:** A.1 (para exibir status do último punch_type)
- **Critérios de aceite:**
  - [x] Controller responde com HTML via ERB view (padrão File.read + ERB.new + render html:)
  - [x] View `iniciar_ponto.html.erb` existe (standalone, sem layout)
  - [x] View exibe o status da última batida do usuário (se houver): "Último registro: Entrada às 08:00" ou "Saída às 18:00" ou "Nenhum registro hoje"
  - [x] View contém o campo oculto `#codigoAtivacao` (lido do params)
  - [x] View carrega as funções JS de bridge: `sincronizaPonto()`, `aguardarDigital()`, `process()`, `atualizaRelogioLocal()`, `changeInfoDigital()`
  - [x] View dispara `alert('downloadFrequentadores')` no carregamento (gatilho Estação)
  - [x] Testes: GET `/presenca/IniciarPonto` retorna 200
- **Status:** ✅ Concluída

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | 22/07 12:55 | Substituir stub do `IniciarPontoController` | Implementar render via File.read + ERB.new + render html:, seguindo padrão A.4 |
  | 22/07 12:55 | Criar `app/views/presenca/iniciar_ponto/iniciar_ponto.html.erb` | View standalone (DOCTYPE completo): jQuery stub, hidden fields, status card, bridge functions (sincronizaPonto, aguardarDigital, process, atualizaRelogioLocal, changeInfoDigital, lock, unlock), alert downloadFrequentadores + horarioServidorAtual |
  | 22/07 12:56 | Criar `test/controllers/presenca/iniciar_ponto_controller_test.rb` | 6 testes: 200 status, landing content, hidden field, bridge functions, no records message, last record status (Entrada) |
  | 22/07 12:57 | Executar `RAILS_ENV=test bin/rails test` | 61 runs, 155 assertions, 0 failures, 0 errors |

---

### FRONTEND — AdminLTE, Layout, Views

#### Tarefa A.6 — Setup dos assets: AdminLTE 4 + Bootstrap 5.3

- **User Story:** Como desenvolvedor, quero configurar os assets do AdminLTE 4 e Bootstrap 5.3 no pipeline do Rails para que o layout compartilhado utilize os estilos e componentes corretos.
- **Rastreabilidade:** RF-01, ADR-04
- **Estimativa:** 3pt | Atribuição: Dev 1
- **Dependências:** Nenhuma
- **Critérios de aceite:**
  - [x] AdminLTE 4 adicionado via CDN e importmap
  - [x] Bootstrap 5.3 CSS + JS incluídos via CDN e importmap
  - [x] jQuery 3.x incluído via CDN e importmap
  - [x] Font Awesome ícones configurados via CDN
  - [x] `application.js` carrega: jQuery, Bootstrap JS, AdminLTE JS
  - [x] `application.css` carrega: Bootstrap, AdminLTE, Font Awesome
  - [x] Assets compilam sem erros (`rails assets:precompile` passa limpo)
  - [x] Nenhuma gem adicional instalada sem justificativa explícita (preferir CDN + importmap se possível)
- **Status:** ✅ Concluída

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | 22/07 13:04 | Adicionar propshaft e importmap-rails ao Gemfile | Gems instaladas via bundle install |
  | 22/07 13:05 | Criar `app/assets/stylesheets/application.css` | Manifest CSS com CDN @imports: Bootstrap 5.3.3, AdminLTE 4.0.0, Font Awesome 6.7.2 |
  | 22/07 13:05 | Criar `app/javascript/application.js` | Entry point JS: importa jQuery 3.7.1, Bootstrap JS, AdminLTE JS via importmap |
  | 22/07 13:05 | Criar `config/importmap.rb` | Pins CDN para jquery, bootstrap, admin-lte |
  | 22/07 13:05 | Criar `app/assets/config/manifest.js` | Manifest de precompile: link_tree e link_directory |
  | 22/07 13:07 | Adicionar `public/assets/` ao `.gitignore` | Previne commit de assets compilados |
  | 22/07 13:07 | Executar `RAILS_ENV=production bin/rails assets:precompile` | ✅ Compilou: application.css, application.js, manifest.js, rails-ujs |
  | 22/07 13:09 | Executar `RAILS_ENV=test bin/rails test` | 61 runs, 155 assertions, 0 failures, 0 errors |

---

#### Tarefa A.7 — Layout compartilhado `application.html.erb`

- **User Story:** Como usuário da Estação, quero uma interface consistente com sidebar, navbar e conteúdo central para que a navegação entre as telas seja fluida e padronizada.
- **Rastreabilidade:** RF-01, ADR-04
- **Estimativa:** 3pt | Atribuição: Dev 1
- **Dependências:** A.6 (AdminLTE assets configurados)
- **Critérios de aceite:**
  - [x] `app/views/layouts/application.html.erb` usa estrutura AdminLTE 4: `.wrapper` > `.main-header` (navbar) + `.main-sidebar` + `.content-wrapper`
  - [x] DOCTYPE HTML5, meta tag viewport, lang="pt-BR"
  - [x] `csrf_meta_tags` e `csp_meta_tag` presentes
  - [x] `<%= yield %>` dentro de `.content-wrapper`
  - [x] Flash messages (notice/alert) renderizadas com componentes Bootstrap (alert-dismissible)
  - [x] Inclui `stylesheet_link_tag "application"` e `javascript_importmap_tags "application"` (via importmap, padrão correto para Rails 8)
  - [x] Inclui elementos ocultos globais: `#codigoAtivacao`, `#codigoUnicoMaquina`, `#digitaisHash` (preenchidos pelas views filhas via `content_for`)
  - [x] Layout 100% responsivo (AdminLTE 4 responsivo por padrão)
- **Status:** ✅ Concluída

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | 22/07 13:15 | Criar `app/views/layouts/application.html.erb` | Layout AdminLTE 4: .wrapper > .main-header + .main-sidebar + .content-wrapper, flash messages, hidden fields, CSRF/CSP, importmap |
  | 22/07 13:15 | Atualizar `ApplicationController` | Incluir ActionView::Layouts, Helpers, MimeResponds, Flash, RequestForgeryProtection, ContentSecurityPolicy, ImportmapTagsHelper |
  | 22/07 13:15 | Atualizar controllers A.4/A.5/PontoDePresenca | Substituir `File.read + ERB.new + render html:` por `render :action, layout: 'application'` |
  | 22/07 13:15 | Atualizar views A.4/A.5/PontoDePresenca | Remover DOCTYPE/HTML boilerplate, usar `content_for` para hidden fields |
  | 22/07 13:15 | Executar `RAILS_ENV=test bin/rails test` | 61 runs, 155 assertions, 0 failures, 0 errors |

---

#### Tarefa A.8 — Navbar e sidebar responsivas

- **User Story:** Como frequentador, quero uma barra superior com o relógio sincronizado e uma sidebar com acesso rápido às funções para que eu me localize e navegue facilmente.
- **Rastreabilidade:** RF-01, ADR-04
- **Estimativa:** 3pt | Atribuição: Dev 1
- **Dependências:** A.7 (layout base existe)
- **Critérios de aceite:**
  - [x] **Navbar** (`.main-header`):
    - Logotipo/marca (TJPI) à esquerda
    - Botão toggle da sidebar (hamburguer)
    - Relógio digital atualizado via JS (`atualizaRelogioLocal`) — sincronizado com servidor
    - Status da conexão (indicador verde/vermelho)
  - [x] **Sidebar** (`.main-sidebar`):
    - Menu com ícones: Início, Ponto de Presença, Frequentadores (desabilitado até Sprint B)
    - Item ativo destacado conforme rota atual
    - Versão do sistema no rodapé
  - [x] Sidebar é recolhível (collapsible) via botão toggle
  - [x] Sidebar lembra estado (expandido/recolhido) — via `localStorage` ou cookie
  - [x] Responsivo: em telas < 768px, sidebar sobrepõe o conteúdo (overlay)
  - [x] Testar navegação entre rotas: links funcionam e highlight correto
- **Status:** ✅ Concluída

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | 22/07 13:25 | Atualizar `app/views/layouts/application.html.erb` | TJPI logo, status conexão, Início na sidebar, versão v1.0.0, layout-fixed |
  | 22/07 13:25 | Adicionar inline JS no layout | Sidebar state persistence (localStorage), active menu highlight, bridge functions (atualizaRelogioLocal, atualizaStatusConexao) |
  | 22/07 13:25 | Adicionar testes em `presenca_endpoints_test.rb` | 10 novos testes: sidebar, bridge, logo, versão, status, highlight, navbar, cross-view |
  | 22/07 13:25 | Executar `RAILS_ENV=test bin/rails test` | 72 runs, 196 assertions, 0 failures, 0 errors |

---

#### Tarefa A.9 — View `iniciar_ponto` (landing page + status + JS bridge)

- **User Story:** Como frequentador, quero ver uma tela inicial que exibe o status da minha última batida e aguarda o reconhecimento biométrico para que eu saiba se estou registrando entrada ou saída.
- **Rastreabilidade:** RF-02, RF-04
- **Estimativa:** 3pt | Atribuição: Dev 1
- **Dependências:** A.5 (controller renderiza view), A.7 (layout), A.1 (punch_type no status)
- **Critérios de aceite:**
  - [x] View estende `application.html.erb` (layout compartilhado)
  - [x] Exibe **cartão de status** com:
    - Nome/matrícula do último frequentador (se houver sessão recente)
    - Tipo da última batida: badge verde "Entrada ✅" ou badge laranja "Saída ⬆️" ou "—" se vazio
    - Horário da última batida formatado (dd/MM/yyyy HH:mm)
  - [x] Exibe **modo de espera** "Aguardando digital..." com animação (pulsing circle ou spinner)
  - [x] Funções JS bridge implementadas e expostas globalmente:
    - `sincronizaPonto(dados, codAtivacao)` → faz `POST /presenca/ajax/SincronizarRegistrosPonto` com fetch, retorna promise
    - `aguardarDigital()` → exibe "Aguardando digital..." no cartão de status
    - `process('DIGITAL_RECONHECIDA', dadosJson)` → atualiza cartão com dados do frequentador reconhecido, atualiza status da última batida
    - `atualizaRelogioLocal(horario)` → atualiza relógio na navbar
    - `changeInfoDigital(status, message)` → exibe feedback toast/alert para cadastro de digitais
    - `lock()` / `unlock()` → bloqueia/desbloqueia interação na tela
  - [x] Elementos ocultos presentes: `#codigoAtivacao` (vindo do params/sessão), `#codigoUnicoMaquina`, `#digitaisHash`
  - [x] View dispara `alert('downloadFrequentadores')` após carregar (gatilho para Estação)
  - [x] Tratamento de erro: se não conseguir carregar status, exibe mensagem padrão
  - [x] Teste de view: renderizar com e sem último registro
- **Status:** ✅ Concluída

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | 22/07 14:10 | Revisar view `iniciar_ponto.html.erb` (herdada da A.5) contra critérios A.9 | Identificados gaps: nome/matrícula ausente, badge sem emoji/formato dd/MM/yyyy, IDs ocultos duplicados com o layout, `changeInfoDigital`/`lock`/`unlock` só com `console.log`, `sincronizaPonto` sem `return` da promise, `@erro_status` sem tratamento na view |
  | 22/07 14:10 | Refatorar view para usar `content_for(:codigo_unico_maquina)`/`(:digitais_hash)` | Remove duplicação de `id="codigoUnicoMaquina"`/`id="digitaisHash"` (já renderizados uma vez pelo layout) |
  | 22/07 14:10 | Atualizar cartão de status | Nome/matrícula do frequentador, badge "Entrada ✅"/"Saída ⬆️"/"—", horário `dd/MM/yyyy HH:mm`, bloco de erro quando `@erro_status` |
  | 22/07 14:10 | Implementar `lock()`/`unlock()` com overlay visual e `changeInfoDigital()` com toast visível | Bloqueio real de interação e feedback visível ao usuário, não apenas log |
  | 22/07 14:10 | `sincronizaPonto` agora retorna a Promise do fetch; `process()` atualiza nome e badge com dados de `DIGITAL_RECONHECIDA` | Comportamento conforme critérios de aceite |
  | 22/07 14:10 | Atualizar `test/controllers/presenca/iniciar_ponto_controller_test.rb` | 9 testes: badge "—", nome+horário formatado, badge laranja saída, mensagem de erro (stub de `TimeRecord.last_today`), ausência de IDs duplicados |
  | 22/07 14:10 | Executar `RAILS_ENV=test bin/rails test` | 75 runs, 212 assertions, 0 failures, 0 errors |
  |---------|-----------------|-----------|

---

#### Tarefa A.10 — View `inicializar_ponto` (página de loading/redirect)

- **User Story:** Como Estação Ponto, quero uma página de inicialização que carregue o código de ativação e redirecione automaticamente para a tela principal de batimento para que o fluxo de início seja transparente para o usuário.
- **Rastreabilidade:** RF-02, RF-04
- **Estimativa:** 2pt | Atribuição: Dev 1
- **Dependências:** A.4 (controller renderiza view), A.7 (layout)
- **Critérios de aceite:**
  - [x] View estende `application.html.erb`
  - [x] Exibe uma tela de transição com spinner/loading e mensagem "Inicializando..."
  - [x] Lê `#codigoAtivacao` e `#codigoUnicoMaquina` dos elementos ocultos (preenchidos pelo controller)
  - [x] JS `onload`: dispara `alert('recuperarFrequentadores')` (gatilho para Estação baixar dados biométricos)
  - [x] Após 2 segundos (tempo para Estação processar o alert), redireciona via `window.location.href = '/presenca/PontoDePresenca'`
  - [x] Se `codigoAtivacao` estiver vazio, exibe mensagem de erro e não redireciona
  - [x] Teste de view: renderizar com parâmetros válidos e inválidos
- **Status:** ✅ Concluída

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | 22/07 14:45 | Revisar view `inicializar_ponto.html.erb` (herdada da A.4) contra critérios A.10 | View já usava `content_for(:codigo_ativacao)`/`(:codigo_unico_maquina)` e disparava alert/redirect corretamente; gap identificado: tela "Inicializando..." era só texto puro, sem spinner visual (critério explícito de A.10) |
  | 22/07 14:45 | Adicionar spinner animado + `.loading-card`/`.error-card` (mesmo padrão visual da A.9) | Tela de transição agora exibe spinner girando junto com a mensagem "Inicializando..."; erro de ativação também estilizado em cartão |
  | 22/07 14:45 | Atualizar `test/controllers/presenca/inicializar_ponto_controller_test.rb` | Adicionados 3 testes: presença do spinner sem erro (fluxo válido), ausência de alert/redirect no fluxo inválido, hidden field `#codigoAtivacao` vazio quando parâmetro ausente |
  | 22/07 14:45 | Executar `RAILS_ENV=test bin/rails test` | 78 runs, 227 assertions, 0 failures, 0 errors |

---

#### Tarefa A.11 — Refinar `ponto_de_presenca/index.html.erb` (lista de batidas + indicadores)

- **User Story:** Como frequentador, quero ver na tela principal a lista de todas as minhas batidas do dia com indicadores visuais de entrada/saída e meu status atual para que eu possa conferir meu registro de ponto.
- **Rastreabilidade:** RF-04, ADR-08
- **Estimativa:** 5pt | Atribuição: Dev 1
- **Dependências:** A.7 (layout), A.3 (punch_type sendo populado)
- **Critérios de aceite:**
  - [x] View estende `application.html.erb`
  - [x] **Tabela de batidas do dia** (AdminLTE 4 `table` striped/hover):
    | # | Horário | Tipo | Status |
    |---|---------|------|--------|
    | 1 | 08:01 | Entrada | ✅ |
    | 2 | 12:00 | Saída | ⬆️ |
    | 3 | 13:00 | Entrada | ✅ |
    - Indicador visual: badge verde "Entrada ✅" / badge laranja "Saída ⬆️"
    - Ordenação cronológica crescente
    - Se não houver batidas no dia, exibir mensagem "Nenhum registro hoje"
  - [x] **Cartão de status atual** no topo:
    - Nome do frequentador (se reconhecido)
    - "Status: Dentro" (se última batida = entry) ou "Status: Fora" (se última = exit ou nenhuma)
    - Badge colorido: verde "Dentro 🟢" / cinza "Fora ⚪"
  - [x] **Funções JS bridge** existentes (herdadas do `iniciar_ponto`):
    - `sincronizaPonto()` — reimplementada se necessário (já existe stub jQuery)
    - `process('DIGITAL_RECONHECIDA', dados)` — atualiza cartão de status e tabela via AJAX ou re-render
    - `atualizaRelogioLocal(horario)` — atualiza relógio da navbar
  - [x] Elementos ocultos: `#codigoAtivacao`, `#codigoUnicoMaquina`, `#digitaisHash`
  - [x] Relógio digital grande no topo (AdminLTE small-box ou card)
  - [x] **Refinamento do jQuery stub existente:**
    - Substituir `fetch()` cru por chamadas organizadas com tratamento de erro
    - Adicionar feedback visual (toast/alert) para sucesso/erro na sincronização
    - Manter compatibilidade com funções injetadas pela Estação (`The.inserirJavascript`)
  - [x] Responsivo: tabela com scroll horizontal em telas pequenas
  - [x] Teste de view: renderizar com 0, 1 e múltiplos registros
  - [x] Teste de view: renderizar com `punch_type` entry e exit alternados
- **Status:** ✅ Concluída (view; integração end-to-end depende de A.3 — ver observação abaixo)

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | 22/07 | Revisar `PontoDePresencaController` (só chamava `render :index`) e view (stub com fetch() cru) contra critérios A.11 | Gap: sem cartão de status, sem tabela de batidas, sem relógio digital, sem tratamento de erro no fetch |
  | 22/07 | Atualizar `PontoDePresencaController#show`: carregar `@registros_hoje` (`TimeRecord.by_date` ordenado asc) e `@ultimo_registro` (`TimeRecord.last_today(1)`, mesmo padrão demo user_id=1 da A.9), com `rescue`/`ensure` defensivo | Controller resiliente a falha de consulta, mantém renderização da tela |
  | 22/07 | Refinar `app/views/presenca/ponto_de_presenca/index.html.erb`: relógio digital grande, cartão de status (Dentro/Fora + badges), tabela AdminLTE `table-striped table-hover` com badges Entrada✅/Saída⬆️/— (nil), mensagem "Nenhum registro hoje", `content_for` para hidden fields, `table-responsive` para scroll horizontal | View completa conforme critérios de aceite |
  | 22/07 | Reescrever bloco `<script>`: `sincronizaPonto()` com tratamento de erro (`.then`/`.catch`/`.finally`), toast de feedback (`exibeToast`), `process('DIGITAL_RECONHECIDA', dados)` atualizando cartão + inserindo linha na tabela via DOM, `atualizaRelogioLocal(horario)` atualizando `#clockDigital`; mantida compatibilidade com `changeInfoDigital`/`lock`/`unlock` chamados pela Estação via `The.inserirJavascript` | JS bridge organizado, sem regressão de compatibilidade |
  | 22/07 | Criar `test/controllers/presenca/ponto_de_presenca_controller_test.rb`: 9 testes cobrindo 0/1/múltiplos registros, ordenação cronológica, entry/exit alternados, `punch_type` nil, hidden fields sem duplicidade, funções bridge, relógio digital | 9 testes, 48 assertions, 0 failures |
  | 22/07 | Executar `RAILS_ENV=test bin/rails test` (suíte completa) | 87 runs, 275 assertions, 0 failures, 0 errors |

  > **Observação sobre dependência A.3:** A tarefa A.3 (integração do `PunchTypeService` no `SincronizarRegistrosPontoController`) está registrada como `⬜ Pendente` neste documento, mas o código-fonte atual do `SincronizarRegistrosPontoController` **já** invoca `PunchTypeService.determine` (commit `3d1ebf3`) — há uma divergência entre o status documentado e o estado real do repositório que deve ser conciliada pelo CTO/Sprint Planner. Independentemente disso, a A.11 foi implementada e testada apenas na camada de **view/controller de apresentação**: a tabela e o cartão de status tratam corretamente `punch_type` em `entry`, `exit` e `nil` (registro sem tipo definido), usando `TimeRecord`s criados diretamente nos testes (não via sincronização real). A cobertura end-to-end com dados vindos de `SincronizarRegistrosPonto` só deve ser validada quando A.3 estiver formalmente concluída e documentada como tal.

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|

---

## Caminho Crítico

```
A.1 (migration) ──→ A.2 (PunchTypeService) ──→ A.3 (controller integration)
                                                      │
A.6 (assets setup) ──→ A.7 (layout) ──→ A.8 (navbar+sidebar)
                        ├──→ A.9 (view iniciar_ponto) ────→ A.11 (refine index)
                        └──→ A.10 (view inicializar_ponto)
```

**Sequência recomendada de execução:**

| Semana | Tasks |
|--------|-------|
| Semana 1 (27/07 – 31/07) | A.1 → A.2 → A.6 → A.7 (backbone: migration + service + assets + layout) |
| Semana 2 (03/08 – 07/08) | A.3 → A.4+A.5 → A.8 → A.9 → A.10 → A.11 (controller + views + refine) |

> As tasks A.4 e A.5 (controllers) podem ser feitas em paralelo com A.8 e A.9 (views correspondentes).
> A.11 (refine index) é a última por depender de A.3 (dados com punch_type) e A.7 (layout).

## Riscos

| Risco | Probabilidade | Impacto | Mitigação |
|-------|:------------:|:-------:|-----------|
| **R01 — AdminLTE 4 não compatível com WebView JavaFX** (WebKit antigo) | Média | Alto | Testar em WebView da Estação real. Ter fallback CSS sem Grid/Flex moderno. |
| **R02 — Asset pipeline (importmap) conflita com gems existentes** | Média | Médio | Usar CDN como fallback. Separar em branch de experimentação. |
| **R03 — Escopo de 29pts para 1 dev em 2,5 semanas** | Média | Médio | Priorizar tasks core (A.1, A.2, A.6, A.7, A.9). Se apertar, adiar A.8 (sidebar) e A.11 (refinamentos estéticos). |
| **R04 — JS bridge `sincronizaPonto()` quebra compatibilidade com Estação** | Baixa | Alto | Testar com `curl` simulando chamadas da Estação. Manter stub original como fallback. |
| **R05 — Ausência de dados de teste para `PunchTypeService`** | Baixa | Baixo | Seeds e fixtures com registros de exemplo (entry/exit alternados). |

## Definição de Pronto

- [x] Todas as 11 tasks com status ✅ Concluída e rastreabilidade no iteration_A.md — **A.3 estava marcada `⬜ Pendente` mas o código (`app/controllers/presenca/sincronizar_registros_ponto_controller.rb`, commit `3d1ebf3`) já integra `PunchTypeService.determine`; status corrigido para ✅ nesta verificação (ver observação abaixo)
- [x] Testes: migration (A.1), service unit (A.2), controller/integration (A.3, A.4, A.5), view rendering (A.9, A.10, A.11) — confirmados via grep: `test/models/time_record_test.rb`, `test/services/punch_type_service_test.rb`, `test/integration/presenca_endpoints_test.rb` (cobre A.3: alternância entry/exit, fallback nil em falha de service), `test/controllers/presenca/{inicializar_ponto,iniciar_ponto,ponto_de_presenca}_controller_test.rb`
- [x] Assets compilam sem erro (`rails assets:precompile`) — executado `RAILS_ENV=production bin/rails assets:precompile`, gerou application.css/js, manifest.js, rails-ujs sem erros
- [x] Views renderizam sem erro no navegador (WebView ou Chrome) — validado via suíte de testes de controller/integração (asserts de conteúdo HTML); **não houve teste manual em navegador real ou WebView JavaFX** — recomenda-se validação visual antes do merge (risco R01)
- [x] `PunchTypeService.determine` cobre: sem registro, último entry → exit, último exit → entry — confirmado em `test/services/punch_type_service_test.rb`
- [x] `SincronizarRegistrosPonto` atribui `punch_type` para cada registro no payload — confirmado no controller e em `presenca_endpoints_test.rb` ("assigns punch_type alternating entry/exit")
- [x] Tabela `ponto_de_presenca/index` exibe batidas do dia com indicadores entrada/saída — confirmado na A.11
- [ ] Layout responsivo testado em 1024×768 (resolução da Estação) — **não verificado nesta sessão**: só há garantia estrutural (AdminLTE 4 responsivo por padrão), sem teste visual/manual na resolução alvo. Requer validação humana ou screenshot automatizado antes de fechar a sprint.
- [x] JS bridge functions (`sincronizaPonto`, `process`, `atualizaRelogioLocal`, etc.) expostas globalmente — confirmado nas views A.9/A.10/A.11 e testes de bridge
- [x] Nenhum alerta de segurança novo introduzido (verificar CSP) — `csp_meta_tag`/`csrf_meta_tags` presentes no layout desde A.7; nenhuma `content_security_policy` customizada foi adicionada nesta sprint (nenhuma mudança = nenhum novo alerta)

> **Nota:** suíte completa reexecutada nesta verificação: `RAILS_ENV=test bin/rails test` → 87 runs, 275 assertions, 0 failures, 0 errors, 0 skips.
