# Iteration 7 — Login Manual na Interface de Ponto (View PontoDePresenca)
> Status: 📋 Planejada | Período: 27/07/2026 – 31/07/2026 (1 semana) | Goal: Adicionar formulário de login manual (username + senha) na view PontoDePresenca para batida sem biometria, com toggle visível, campos `accessKey`/`plainPassword`, JS `alert('LOGINMANUAL')` e feedback de erro | RFs: RF-02, RF-04 | ADRs: ADR-01, ADR-02, ADR-03, ADR-08

## Desenvolvedores

| Dev | Perfil | Foco |
|-----|--------|------|
| Dev 1 | Fullstack Rails 8 + Frontend (AdminLTE 4) | View ERB, JS bridge, CSS, testes, documentação |

## Backlog

### FRONTEND — View, JavaScript, CSS

#### Tarefa 7.1 — Botão "Login Manual" com toggle visível entre biometria e formulário

- **User Story:** Como frequentador sem biometria cadastrada, quero um botão "Login Manual" na tela principal de ponto para que eu possa alternar entre o modo biométrico (leitor de digital) e o formulário de login manual.
- **Rastreabilidade:** RF-02 (Autenticação Manual), ADR-01, ADR-03
- **Estimativa:** 2pt | Atribuição: Dev 1
- **Dependências:** Nenhuma (view `ponto_de_presenca/index.html.erb` já existe com status card e tabela)
- **Critérios de aceite:**
  - [ ] Botão "Login Manual" visível na tela principal (não apenas em development — visível em todos os ambientes)
  - [ ] Botão estilizado como `btn btn-primary` (AdminLTE) com ícone de usuário (Font Awesome `fa-user`)
  - [ ] Ao clicar no botão: exibe o formulário de login manual (`#loginManualForm`), oculta a área de biometria (`#biometriaArea`)
  - [ ] Botão "Cancelar" dentro do formulário de login: retorna à tela principal (oculta formulário, exibe biometria)
  - [ ] Toggle suave com animação de transição (fade/slide via jQuery ou CSS transition)
  - [ ] Formulário oculto por padrão (`display: none`) no carregamento da página
  - [ ] Teste de view: botão "Login Manual" presente no HTML renderizado
- **Status:** ✅ Concluída

  **Linha do Tempo:**

  | Horário | O que foi feito | Resultado |
  |---------|-----------------|-----------|
  | 2026-07-24 10:00 | Implementação da tarefa 7.1 | Botão "Login Manual" adicionado, toggle com fade, formulário oculto por padrão, teste de view passando |

#### Tarefa 7.2 — Formulário com campos `input[name=accessKey]` e `input[name=plainPassword]`

- **User Story:** Como Estação JavaFX, quero que o formulário contenha os campos `input[name=accessKey]` e `input[name=plainPassword]` para que o `ValidarBatidaManualService` possa ler as credenciais via jQuery e criptografá-las em DES.
- **Rastreabilidade:** RF-02, ADR-02, ADR-03
- **Estimativa:** 2pt | Atribuição: Dev 1
- **Dependências:** 7.1 (toggle existe; formulário inserido no mesmo container)
- **Critérios de aceite:**
  - [ ] Campo `input[name=accessKey]` presente com `type="text"`, label "Usuário" em português, placeholder "Digite seu usuário"
  - [ ] Campo `input[name=plainPassword]` presente com `type="password"` , label "Senha" em português, placeholder "Digite sua senha"
  - [ ] Nomes dos atributos `name` exatamente como especificado (`accessKey` e `plainPassword`) — caso sensitivo, a Estação lê via `jQuery('input[name=accessKey]').val()` e `jQuery('input[name=plainPassword]').val()`
  - [ ] Campos agrupados em um card AdminLTE (`card card-primary`) com título "Login Manual"
  - [ ] Formulário com `id="loginManualForm"` e `onsubmit="return false"` (evita POST nativo — a Estação captura via alert)
  - [ ] Botão "Registrar Ponto" (`type="submit"`, `btn btn-success`) e botão "Cancelar" (`type="button"`, `btn btn-secondary`)
  - [ ] Teste de view: `input[name=accessKey]` e `input[name=plainPassword]` presentes no HTML
- **Status:** ⬜ Pendente

#### Tarefa 7.3 — JavaScript `alert('LOGINMANUAL')` ao submeter o formulário

- **User Story:** Como Estação JavaFX, quero que ao clicar em "Registrar Ponto" seja disparado `alert('LOGINMANUAL')` para que o `OnAlertListener` da Estação intercepte e execute `Operacao.LOGINMANUAL.execute()`.
- **Rastreabilidade:** RF-02
- **Estimativa:** 1pt | Atribuição: Dev 1
- **Dependências:** 7.2 (formulário com botão submit existe)
- **Critérios de aceite:**
  - [ ] Evento de `submit` (ou clique no botão "Registrar Ponto") dispara `alert('LOGINMANUAL')`
  - [ ] Após o `alert`, a página não faz submit real (formulário tem `onsubmit="return false"` e o handler JS não chama `form.submit()`)
  - [ ] `alert('LOGINMANUAL')` é o único alert disparado — sem alerts extras de validação que possam interferir com o `OnAlertListener`
  - [ ] O alert é chamado mesmo se os campos estiverem vazios (a validação é responsabilidade da Estação)
  - [ ] Teste de view: verificar que o alert é disparado ao submeter (ou que o handler JS existe no escopo global)
- **Status:** ⬜ Pendente

#### Tarefa 7.4 — Função JS `changeMensagemStatus(mensagem)` para feedback de erro

- **User Story:** Como frequentador, quero ver uma mensagem de erro clara na tela quando o login manual falha (credenciais inválidas) para que eu saiba que preciso tentar novamente.
- **Rastreabilidade:** RF-02
- **Estimativa:** 1pt | Atribuição: Dev 1
- **Dependências:** 7.2 (área do formulário existe para exibir mensagem)
- **Critérios de aceite:**
  - [ ] Função global `changeMensagemStatus(mensagem)` definida no escopo `window`
  - [ ] Função recebe string `mensagem` e exibe na tela dentro do card de login manual (ex: `#loginFeedback`)
  - [ ] Mensagem estilizada com AdminLTE (`alert alert-danger` para erro, `alert alert-success` para sucesso)
  - [ ] Função é compatível com chamada futura pela Estação JavaFX (via `The.inserirJavascript("changeMensagemStatus('Usuário ou Senha Inválidos!')")`) — linha comentada em `EventoLeitura.USUARIO_SENHA_INVALIDOS` que pode ser descomentada
  - [ ] Mensagem some automaticamente após 5 segundos ou ao iniciar novo login manual
  - [ ] Área `#loginFeedback` existe no DOM (inicialmente vazia, dentro do card de login)
  - [ ] Teste de view: função `changeMensagemStatus` definida e visível no HTML renderizado
- **Status:** ⬜ Pendente

#### Tarefa 7.5 — Compatibilidade com `process('DIGITAL_RECONHECIDA', dados)` existente

- **User Story:** Como sistema de ponto, quero que o fluxo de login manual bem-sucedido termine chamando a mesma função `process('DIGITAL_RECONHECIDA', dados)` da biometria para que o cartão de status e a tabela sejam atualizados de forma consistente.
- **Rastreabilidade:** RF-02, RF-04, ADR-08
- **Estimativa:** 1pt | Atribuição: Dev 1
- **Dependências:** 7.3 (login manual dispara fluxo), A.11 (função `process()` já implementada)
- **Critérios de aceite:**
  - [ ] Verificar que `process('DIGITAL_RECONHECIDA', dadosJson)` na view `index.html.erb` atualmente espera o seguinte formato em `dadosJson`: `{ "nome": "...", "punchType": "entry"|"exit", "horario": "HH:mm" }`
  - [ ] A Estação, após login manual bem-sucedido, chama `process('DIGITAL_RECONHECIDA', JSON.stringify({id: userId, nome: nome, horario: horario}))` — verificar se o campo `punchType` é incluído pela Estação ou precisa ser inferido
  - [ ] Se a Estação não enviar `punchType`, a função `process()` já trata adequadamente (linha 201: `var isEntry = dados.punchType === 'entry'` → `isEntry` será `false` se `punchType` for `undefined`, caindo em "Fora")
  - [ ] Teste de view/JS: simular `process('DIGITAL_RECONHECIDA', payload)` com payload de login manual e verificar que cartão e tabela são atualizados sem erro
  - [ ] Nenhuma alteração na função `process()` existente (apenas verificação de compatibilidade)
- **Status:** ⬜ Pendente

#### Tarefa 7.6 — Estilos CSS AdminLTE/Bootstrap 5.3 para o formulário de login manual

- **User Story:** Como desenvolvedor, quero que o formulário de login manual siga o padrão visual AdminLTE 4 da aplicação para manter a consistência da interface.
- **Rastreabilidade:** RF-02, ADR-04
- **Estimativa:** 2pt | Atribuição: Dev 1
- **Dependências:** 7.2 (formulário HTML existe para ser estilizado)
- **Critérios de aceite:**
  - [ ] Formulário centralizado no card, com largura máxima `400px` (mesmo padrão do `status-card`)
  - [ ] Campos com ícones: `fa-user` no campo "Usuário", `fa-lock` no campo "Senha" (input groups do Bootstrap)
  - [ ] Botão "Registrar Ponto" com classe `btn btn-success btn-block` (verde, largura total)
  - [ ] Botão "Cancelar" com classe `btn btn-secondary` (cinza), posicionado ao lado do botão Registrar ou abaixo
  - [ ] Card com sombra (`box-shadow`), cantos arredondados (`border-radius: 12px`), padding consistente
  - [ ] Transição suave (fadeIn/fadeOut) ao mostrar/ocultar o formulário (`CSS transition` ou jQuery `fadeToggle`)
  - [ ] Responsivo: em telas menores que 768px, formulário ocupa 100% da largura disponível
  - [ ] Inline `<style>` na view (mesmo padrão existente no `index.html.erb`)
- **Status:** ⬜ Pendente

### TESTES — View e Integração

#### Tarefa 7.7 — Testes de view para o formulário de login manual

- **User Story:** Como desenvolvedor, quero testes automatizados que verifiquem a presença e o comportamento correto de todos os elementos do login manual na view PontoDePresenca.
- **Rastreabilidade:** RF-02, RF-04
- **Estimativa:** 2pt | Atribuição: Dev 1
- **Dependências:** 7.1 a 7.6 (elementos frontend implementados)
- **Critérios de aceite:**
  - [ ] Teste: botão "Login Manual" presente no HTML (`assert_includes @response.body, 'Login Manual'`)
  - [ ] Teste: `input[name=accessKey]` presente no HTML (`assert_includes @response.body, 'name="accessKey"'`)
  - [ ] Teste: `input[name=plainPassword]` presente no HTML (`assert_includes @response.body, 'name="plainPassword"'`)
  - [ ] Teste: botão "Cancelar" presente no HTML (`assert_includes @response.body, 'Cancelar'`)
  - [ ] Teste: função `changeMensagemStatus` definida no JS (`assert_includes @response.body, 'function changeMensagemStatus'`)
  - [ ] Teste: `onsubmit="return false"` presente no formulário (`assert_includes @response.body, 'onsubmit="return false"'`)
  - [ ] Teste: `alert('LOGINMANUAL')` presente no JS (`assert_includes @response.body, "alert('LOGINMANUAL')"`)
  - [ ] Teste: formulário está oculto por padrão (`assert_includes @response.body, 'display: none'` ou `style="display:none"` no container do formulário)
  - [ ] Teste: área `#loginFeedback` presente no DOM (`assert_includes @response.body, 'id="loginFeedback"'`)
  - [ ] `rails test` passa sem regressão (suíte completa mantém 0 failures)
- **Status:** ⬜ Pendente

#### Tarefa 7.8 — Teste de integração (login → batida → sincronização)

- **User Story:** Como administrador do sistema, quero um teste de integração que valide o fluxo completo de login manual (cadastro de usuário via admin → autenticação DES → sincronização de batida) para garantir que o pipeline ponta-a-ponta funciona.
- **Rastreabilidade:** RF-02, RF-04, ADR-02, ADR-03
- **Estimativa:** 3pt | Atribuição: Dev 1
- **Dependências:** 7.7 (testes de view passando), Sprints 2-5 (ValidarFrequentador, SincronizarRegistrosPonto, CryptoDes)
- **Critérios de aceite:**
  - [ ] Teste cria um `User` com `username` e `password` (senha bcrypt) e `status: "ativo"`
  - [ ] Teste chama `GET /presenca/ValidarFrequentador` com:
    - `loginAccessKey` = DES+UrlBase64 do username
    - `plainPassword` = DES+UrlBase64 da senha
    - `codAtivacao` = `poc-ativacao-001`
  - [ ] Resposta contém o `user.id` como string (ex: `"1"`)
  - [ ] Teste chama `POST /presenca/ajax/SincronizarRegistrosPonto` com registro no formato `<id>-<dd:MM:yyyy:HH:mm:ss>`
  - [ ] `TimeRecord` é criado com `user_id` correto e `authentication_mode: "manual"`
  - [ ] Teste também cobre cenário de credenciais inválidas: resposta `"USUARIO_SENHA_INVALIDOS"`
  - [ ] Teste cobre cenário de usuário inativo: valida que a resposta é de erro
  - [ ] `rails test` passa sem regressão
- **Status:** ⬜ Pendente

### DOCUMENTAÇÃO — Integração

#### Tarefa 7.9 — Documentação de integração do login manual

- **User Story:** Como desenvolvedor de integração, quero documentação atualizada descrevendo o formulário de login manual, os campos esperados pela Estação e o fluxo JS para que futuras manutenções e integrações sejam mais fáceis.
- **Rastreabilidade:** RF-02
- **Estimativa:** 1pt | Atribuição: Dev 1
- **Dependências:** 7.1 a 7.6 (todos os elementos implementados e verificados)
- **Critérios de aceite:**
  - [ ] Atualizar `docs/relatorio-interacao-presenca-estacao.md` (ou equivalente em `Frequencia/documentacao-estacao-ponto.md`) com seção específica do login manual
  - [ ] Documentar: IDs dos campos (`input[name=accessKey]`, `input[name=plainPassword]`)
  - [ ] Documentar: fluxo JS (`alert('LOGINMANUAL')` → Estação intercepta → criptografa DES → chama API)
  - [ ] Documentar: função `changeMensagemStatus(mensagem)` e como a Estação pode chamá-la
  - [ ] Referências cruzadas para o código da Estação: `Operacao.java:53-54`, `EventoLeitura.java:69-76`
  - [ ] Diagrama de sequência simplificado (textual ou mermaid) mostrando o fluxo usuário → view → alert → Estação → API → process()
- **Status:** ⬜ Pendente

## Caminho Crítico

```
7.1 (botão toggle) ──→ 7.2 (formulário campos) ──→ 7.3 (alert JS)
                        │                             │
                        └──→ 7.6 (estilos CSS)         │
                              │                        │
                              └──→ 7.4 (changeMsg) ────┤
                                                       │
                             7.5 (compatibilidade) ←───┘
                              │
                            7.7 (testes view) ──→ 7.8 (teste integração)
                                                       │
                                                     7.9 (documentação)
```

**Sequência recomendada de execução:**

| Dia | Tasks |
|-----|-------|
| Dia 1 (27/07) | 7.1 (botão toggle) + 7.2 (formulário) — estrutura da view |
| Dia 2 (28/07) | 7.3 (alert JS) + 7.6 (estilos CSS) — interatividade e aparência |
| Dia 3 (29/07) | 7.4 (changeMensagemStatus) + 7.5 (compatibilidade process()) — feedback e integração |
| Dia 4 (30/07) | 7.7 (testes view) + 7.8 (teste integração) — validação |
| Dia 5 (31/07) | 7.9 (documentação) + revisão final + `rails test` completo |

> As tarefas 7.1, 7.2 e 7.6 formam o backbone visual e podem ser feitas sequencialmente no mesmo dia.
> 7.4 e 7.5 são independentes entre si e podem ser paralelizadas se houver mais de um dev.
> 7.8 (teste de integração) depende de 7.3 e 7.5 estarem completos para validar o fluxo completo.

## Riscos

| Risco | Probabilidade | Impacto | Mitigação |
|-------|:------------:|:-------:|-----------|
| **R01 — Nome dos campos `accessKey`/`plainPassword` divergir do que a Estação JavaFX espera** | Baixa | Alto | Verificar no código da Estação (`Operacao.java:53-54`) os seletores jQuery exatos. Testar com curl simulando a leitura. |
| **R02 — `alert('LOGINMANUAL')` interferir com outros alerts da página** | Média | Médio | Garantir que o alert só seja disparado no submit do formulário de login, não em outros eventos. Remover alerts extras durante o fluxo. |
| **R03 — WebView da Estação não suportar CSS moderno (transitions, flexbox do formulário)** | Baixa | Médio | Usar transições CSS simples (`opacity`/`display` com `setTimeout`) e fallback para jQuery `fadeToggle()`. Testar em WebView real. |
| **R04 — `changeMensagemStatus` não ser chamável pela Estação via `The.inserirJavascript`** | Baixa | Alto | Garantir que a função seja global (`window.changeMensagemStatus = function...`). Testar via console do navegador. |
| **R05 — Teste de integração (7.8) falhar por dependência do `CryptoDes` service** | Média | Médio | Usar `CryptoDes.encrypt` diretamente no teste (já testado e validado na Sprint 2). Verificar encoding UrlBase64. |
| **R06 — 15pts estimados para 1 dev em 1 semana** | Média | Médio | Priorizar 7.1-7.4 (core do login manual) + 7.7 (testes). Se apertar, adiar 7.5 (já é verificação) e 7.9 (documentação). |

## Definição de Pronto

- [ ] Todas as 9 tasks com status ✅ Concluída e rastreabilidade no `iteration_7.md`
- [ ] `rails test` passa sem regressão — suíte completa com 0 failures, 0 errors
- [ ] Botão "Login Manual" visível e funcional (toggle com formulário) — validado via teste de view
- [ ] Campos `input[name=accessKey]` e `input[name=plainPassword]` presentes — validado via teste de view
- [ ] `alert('LOGINMANUAL')` disparado ao clicar em "Registrar Ponto" — validado via teste de view
- [ ] `changeMensagemStatus(mensagem)` global e funcional — validado via teste de view
- [ ] `process('DIGITAL_RECONHECIDA', dados)` compatível com payload de login manual — verificado
- [ ] Teste de integração cobre fluxo completo: cadastro → DES → ValidarFrequentador → SincronizarRegistrosPonto → TimeRecord criado
- [ ] Documentação de integração atualizada com campos, fluxo JS e referências ao código da Estação
- [ ] Nenhum alerta de segurança novo (CSRF continua desabilitado nos controllers presenca via `skip_before_action` conforme padrão existente)